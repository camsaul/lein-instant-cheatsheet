(ns instant-cheatsheet.api
  "API endpoints that return some subset of the data defined in `instant-cheatsheet.sources`."
  (:require [cheshire.core :as cheshire]
            [clojure.string :as s]
            [compojure.core :refer [defroutes GET]]
            [instant-cheatsheet
             [sources :as sources]
             [util :as util]]))

(defn- response [body]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (cheshire/generate-string body)})

(defn- sorted-matches
  "Get first 100 matches for QUERY-STRING, sorted by Levenshtein distance."
  [query-string]
  (sort-by (partial util/levenshtein-distance query-string)
           (take 100 (for [^String symbol-str @sources/all-symbols-keys
                           :when              (.contains symbol-str query-string)]
                       symbol-str))))

(defn- get-matches
  "Return vector of [{:name symbol-name :matches [infodict+]}+] of symbols that match Q."
  [q]
  (response (for [match (sorted-matches q)]
              {:name    match
               :matches (@sources/all-symbols match)})))

(defn- docstr->source-docstr
  "Make DOCSTR look like it does in the source code, so we can strip it out."
  [docstr]
  ;; docstr is surrounded by quotes in source
  ;; \" in the docstr will look like \\"" in the source itself
  (str \" (s/escape docstr {\" "\\\""}) \"))

(defn- strip-docstr-from-source
  "Remove DOCSTR from SOURCE if it exists."
  [source docstr]
  (if-not (and source (seq docstr))
    source
    (apply str (interpose "\n" (for [source-line (s/split-lines (s/replace source (docstr->source-docstr docstr) ""))
                                     :when       (not (s/blank? source-line))]
                                 source-line)))))

(defn- get-source
  "Return the source code for symbol in specified namespace."
  [ns-name-str symb-name]
  (let [qualified-symbol (symbol ns-name-str symb-name)
        docstr           (:doc (meta (find-var qualified-symbol)))
        source           (util/read-source qualified-symbol)]
    (response {:source (strip-docstr-from-source source docstr)})))

(defroutes routes
  (GET "/matches" [q] (get-matches q))
  (GET "/source" [ns symbol] (get-source ns symbol)))
