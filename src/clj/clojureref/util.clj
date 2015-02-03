(ns clojureref.util
  "Helper methods to get dox/source for symbols, and calculate string distance."
  (:use [hiccup.core :only (html)])
  (:require clojure.repl
            [clojure.string :as str]))

(def source-for-symbol
  "Gets source from function/macro/special form var."
  (memoize
   (fn [symb]
     (clojure.repl/source-fn symb))))

(defn doc-for-symbol
  "Get documentation (description, args, special type, etc.) from metadata for function/macro/special form."
  [qualified-symbol]
  (let [metta (->> qualified-symbol
                   find-var
                   meta)
        {:keys [forms arglists doc url macro special-form name]} metta
        args (or forms arglists)]
    {:doc doc
     :special_type (if special-form "Special Form"
                       (if macro "Macro"))
     :args (when args
             (format "(%s)" (apply str (interpose " " args))))
     :url (when special-form
            (str "http://clojure.org/"
                 (or url (str "special_forms#" name))))}))

(def levenshtein-distance
  "Calculates the edit-distance between two sequences"
  (memoize
   (fn [seq1 seq2]
     (cond
       (empty? seq1) (count seq2)
       (empty? seq2) (count seq1)
       :else (min
              (+ (if (= (first seq1) (first seq2)) 0 1)
                 (levenshtein-distance (rest seq1) (rest seq2)))
              (inc (levenshtein-distance (rest seq1) seq2))
              (inc (levenshtein-distance seq1 (rest seq2))))))))
