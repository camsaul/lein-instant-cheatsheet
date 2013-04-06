(ns clojureref.util
  (:require [clojure.string :as str]))

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

(defn get-source
  "Gets source from function/macro/special form var."
  [nmsp symb]
  (clojure.repl/source-fn (symbol (str nmsp) (str symb))))

(defn doc-for-ns
  "Returns a seq of [symbol-name doc forms source] for each public function/macro/special form in the namespace."
  [n]
  (sort-by first (filter not-empty (map (fn [[symb varr]]
                                          (let [metta (meta varr)]
                                            (when-let [forms (get-forms metta)]
                                              [(name symb) (get-doc metta) forms (get-source n symb)])))
                                        (ns-publics (symbol n))))))

(defn replace-nl-with-br
  "Helper method to replace newlines with <br />."
  [text]
  (when text (interpose [:br] (str/split text #"\n"))))
