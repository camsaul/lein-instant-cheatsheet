(ns clojureref.util
  (:require clojure.repl
            [clojure.string :as str]))

(declare replace-nl-with-br)

(defn str-nl
  "Helper version of \"str\" that adds a newline at the end."
  [& args]
  (str (apply str args) "\n"))

(defn get-doc
  "Gets and formats docstrings from function/macro/special form metadata."
  [m]
  (str
   (cond
    (:forms m) (format "(%s)\n" (apply str (interpose " " (:forms m))))
    (:arglists m) (format "(%s)\n"(apply str (interpose " " (:arglists m)))))
   (if (:special-form m)
     (str
      "<i>Special Form</i>"
      (str-nl " " (:doc m))
      (if (contains? m :url)
        (when (:url m)
          (str-nl "\n  Please see http://clojure.org/" (:url m)))
        (str-nl "\n  Please see http://clojure.org/special_forms#"
                (:name m))))
     (str
      (when (:macro m)
        "<i>Macro</i>\n")
      (str " " (:doc m))))))

(defn get-forms
  "Gets and formats forms/arglists from function/macro/special form metadata."
  [m]
  (cond
   (:forms m) (apply str (interpose " " (:forms m)))
   (:arglists m) (apply str (interpose " " (:arglists m)))))

(defn -source-for-symbol
  "Gets source from function/macro/special form var."
  [symb]
  (clojure.repl/source-fn symb))

(def source-for-symbol
  (memoize -source-for-symbol))

(defn doctext-split-args-and-description
  "Given DOC text split the first part that specifies arg formats; return pair of [args-formats description]"
  [doctext]
  (let [[_ args-form description] (re-find #"^(\([^\)]+\))\s*(.*)" doctext)]
    {:args args-form
     :doc description}))

(defn doc-for-symbol
  [qualified-symbol]
  (->> qualified-symbol
       find-var
       meta
       ((fn [metta]
           (def -m metta)
           metta))
       get-doc
       (#(str/replace % #"\s+" " ")) ;; replace multiple spaces or newlines with single space
       doctext-split-args-and-description))

(defn replace-nl-with-br
  "Helper method to replace newlines with <br />."
  [text]
  (when text (interpose [:br] (str/split text #"\n"))))
