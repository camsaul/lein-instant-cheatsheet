(ns instant-cheatsheet.util
  "Helper methods to get dox/source for symbols, and calculate string distance."
  (:require [clojure.java.io :as io]
            clojure.repl)
  (:import (java.io InputStreamReader LineNumberReader PushbackReader)))


(defn doc-for-symbol
  "Get documentation (description, args, special type, etc.) from metadata for function/macro/special form."
  [qualified-symbol]
  (let [metta (->> qualified-symbol
                   find-var
                   meta)
        {:keys [forms arglists doc url macro special-form name]} metta
        args (or forms arglists)]
    {:doc doc
     :special_type (cond special-form "Special Form"
                         macro        "Macro"
                         :else        nil)
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

(defn read-source
  "Attempt to read source via `/clojure.repl/source-fn`; otherwise read via an alternate implementation
   that uses a regular input stream."
  [symb]
  (or
   (clojure.repl/source-fn symb)
   (let [varr (resolve symb)                                               ; this implementation is almost exactly the same as clojure.repl/source-fn
         {:keys [file line]} (meta varr)]                                  ; but uses clojure.java.io/input-stream instead of clojure.lang.RT/baseLoader
     (when file                                                            ; which seems to fail sometimes for files that aren't AOT-compiled
       (with-open [stream (io/input-stream file)                           ; e.g. files in our project
                   reader (LineNumberReader. (InputStreamReader. stream))]
         (dotimes [_ (dec line)] (.readLine reader))                       ; skip up to symbol
         (let [text (StringBuilder.)
               pbr (proxy [PushbackReader] [reader]                        ; proxy version of PushbackReader that additionally pushes text into StringBuilder
                     (read [] (let [i (proxy-super read)]
                                (.append text (char i))
                                i)))]
           (binding [*read-eval* false]                                    ; no need to eval what we read
             (read (PushbackReader. pbr)))                                 ; read up until we have a valid form
           (str text)))))))                                                ; return the text of the form we just read (*not* the form itself)
