(ns clojureref.util
  (:use [hiccup.core :only (html)])
  (:require clojure.repl
            [clojure.string :as str]))

(declare replace-nl-with-br)
(defn get-doc
  "Gets and formats docstrings from function/macro/special form metadata."
  [{:keys [forms arglists doc url macro special-form name]}]
  (let [args (or forms arglists)]
    {:doc doc
     :special_type (if special-form "Special Form"
                       (when macro "Macro"))
     :args (when args
             (format "(%s)" (apply str (interpose " " args))))
     :url (when special-form
            (str "http://clojure.org/"
                 (or url (str "special_forms#" name))))}))

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

(defn doc-for-symbol
  [qualified-symbol]
  (->> qualified-symbol
       find-var
       meta
       get-doc))

(defn replace-nl-with-br
  "Helper method to replace newlines with <br />."
  [text]
  (when text (interpose [:br] (str/split text #"\n"))))
