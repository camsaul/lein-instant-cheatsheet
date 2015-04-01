(ns instant-cheatsheet.api
  "API endpoints that return some subset of the data defined in `instant-cheatsheet.sources`."
  (:require [clojure.string :as s]
            [cheshire.core :as cheshire]
            [compojure.core :refer [defroutes GET]]
            (instant-cheatsheet [sources :as sources]
                                [util :as util])))


(defmacro def-api-fn
  "Helper macro to create an API function that returns JSON"
  [fn-name docstr args-form & body]
  `(defn ~fn-name ~docstr ~args-form
     {:status 200
      :headers {"Content-Type" "application/json"}
      :body (cheshire/generate-string (do ~@body))}))

(def-api-fn get-matches
  "Return vector of [{:name symbol-name :matches [infodict+]}+] of symbols that match Q."
  [q]
  (->> (filter (fn [^String symbol-str] (.contains symbol-str q))
               @sources/all-symbols-keys)
       (take 100) ; reasonable limit <3
       (sort-by (partial util/levenshtein-distance q))
       (map (fn [match]
              {:name match
               :matches (@sources/all-symbols match)}))))

(def-api-fn get-source
  "Return the source code for symbol in specified namespace."
  [ns-name-str symb-name]
  (let [qualified-symbol (symbol ns-name-str symb-name)
        docstr (:doc (meta (find-var qualified-symbol)))
        source (util/read-source qualified-symbol)]
    {:source (when source
               (if-not docstr source                                   ; strip out docstr if one exists
                       (let [docstr (->> (s/escape docstr {\" "\\\""}) ; \" in the docstr will look like \\"" in the source itself
                                         (format "\"%s\""))]           ; docstr is surrounded by quotes in source
                         (->> (s/replace source docstr "")
                              s/split-lines
                              (filter (complement s/blank?))           ; filter out any blank lines
                              (interpose "\n")
                              (apply str)))))}))

(defroutes routes
  (GET "/matches" [q] (get-matches q))
  (GET "/source" [ns symbol] (get-source ns symbol)))
