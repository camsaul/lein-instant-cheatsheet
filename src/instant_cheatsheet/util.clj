(ns instant-cheatsheet.util
  "Helper methods to get dox/source for symbols, and calculate string distance."
  (:require [clojure.java.io :as io]
            [clojure.repl :as repl])
  (:import [java.io InputStreamReader LineNumberReader PushbackReader]))


(defn symbol->metadata
  "Get documentation (description, args, special type, etc.) from metadata for function/macro/special form."
  [qualified-symbol]
  (let [{:keys [forms arglists doc url macro special-form name]} (meta (find-var qualified-symbol))
        args (or forms arglists)]
    {:doc          doc
     :special_type (cond special-form "Special Form"
                         macro        "Macro"
                         :else        nil)
     :args         (when args
                     (format "(%s)" (apply str (interpose " " args))))
     :url          (when special-form
                     (str "http://clojure.org/"
                          (or url (str "special_forms#" name))))}))

(def levenshtein-distance
  "Calculates the edit-distance between two sequences"
  (memoize
   (fn [seq1 seq2]
     (cond
       (empty? seq1) (count seq2)
       (empty? seq2) (count seq1)
       :else         (min
                      (+ (if (= (first seq1) (first seq2)) 0 1)
                         (levenshtein-distance (rest seq1) (rest seq2)))
                      (inc (levenshtein-distance (rest seq1) seq2))
                      (inc (levenshtein-distance seq1 (rest seq2))))))))

(defn- read-source-with-input-stream
  "This implementation is almost exactly the same as `clojure.repl/source-fn` but uses `clojure.java.io/input-stream` instead of `clojure.lang.RT/baseLoader`
   which seems to fail sometimes for files that aren't AOT-compiled."
  [qualified-symbol]
  (when-let [{:keys [file line]} (meta (resolve qualified-symbol))]
    (when file
      ;; no need to eval what we read
      (binding [*read-eval* false]
        (with-open [stream (io/input-stream file)
                    reader (LineNumberReader. (InputStreamReader. stream))]
          ;; skip up to symbol
          (dotimes [_ (dec line)] (.readLine reader))
          ;; proxy version of PushbackReader that additionally pushes text into StringBuilder
          (let [string-builder  (StringBuilder.)
                pushback-reader (proxy [PushbackReader] [reader]
                                  (read [] (let [i (proxy-super read)]
                                             (.append string-builder (char i))
                                             i)))]
            ;; read up until we have a valid form
            (read (PushbackReader. pushback-reader))
            ;; return the text of the form we just read (*not* the form itself)
            (str string-builder)))))))

(defn read-source
  "Attempt to read source via `clojure.repl/source-fn`; otherwise read via an alternate implementation
   that uses a regular input stream."
  [qualified-symbol]
  (or (repl/source-fn qualified-symbol)
      (read-source-with-input-stream qualified-symbol)))
