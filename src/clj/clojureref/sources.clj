(ns clojureref.sources
  (:require [clojure.string :as str]
            [clojureref.util :as util]))

(def namespaces
  (->> '(clojure.core
         clojure.core.protocols
         clojure.core.reducers
         clojure.data
         clojure.edn
         clojure.inspector
         clojure.instant
         clojure.java.browse
         clojure.java.io
         clojure.java.javadoc
         clojure.java.shell
         clojure.main
         clojure.pprint
         clojure.reflect
         clojure.repl
         clojure.set
         clojure.stacktrace
         clojure.string
         clojure.template
         clojure.test
         clojure.walk
         clojure.xml
         clojure.zip
         clojure.algo.generic
         clojure.algo.monads
         clojure.core.cache
         clojure.core.contracts
         clojure.core.incubator
         clojure.core.logic
         clojure.core.match
         clojure.core.memoize
         clojure.core.typed
         clojure.core.unify
         clojure.data.codec.base64
         clojure.data.csv
         clojure.data.finger-tree
         clojure.data.generators
         clojure.data.json
         clojure.data.priority-map
         clojure.data.xml
         clojure.data.zip
         clojure.java.classpath
         clojure.java.data
         clojure.java.jdbc
         clojure.java.jmx
         clojure.math.combinatorics
         clojure.math.numeric-tower
         clojure.test.generative
         clojure.tools.cli
         clojure.tools.logging
         clojure.tools.macro
         clojure.tools.namespace
         clojure.tools.namespace.find
         clojure.tools.nrepl
         clojure.tools.reader
         clojure.tools.trace)
       (mapv (fn [ns-symb]
               (require ns-symb)
               (find-ns ns-symb)))))

(defn dox-for-symbol
  "Return dictionary of docstr, args, etc. for SYMB."
  [ns-name-str symb-name]
  (let [qualified-symbol (symbol ns-name-str symb-name)]
    (util/doc-for-symbol qualified-symbol)))

(defn get-source
  [ns-name-str symb-name]
  (let [qualified-symbol (symbol ns-name-str symb-name)]
    {:source (util/source-for-symbol qualified-symbol)}))

(defn symbol-info [ns-info ns-name-str symb-name]
  (merge ns-info
         (dox-for-symbol ns-name-str symb-name)))

;; (def symbol-info (memoize -symbol-info))

(defn symbols-for-ns
  "Return seq of symbols + info like [symb-name {info}] for NS."
  [ns]
  (let [ns-name-str (-> ns ns-name str)
        ns-info {:namespace ns-name-str}]
    (->> ns
         ns-publics                     ; map of symbol -> var
         keys
         (map (fn [symb]
                (let [symb-name (str symb)]
                  [symb-name (symbol-info ns-info ns-name-str symb-name)]))))))

(def all-symbols
  "Create a dict of {symb-name -> [{info}+]}"
  (->> namespaces
       (mapcat symbols-for-ns)
       (group-by first)
       (map (fn [[k valls]]
              {k (map second valls)}))
       (reduce merge)))

(def all-symbols-keys
  "Sorted vector of all string name of all symbols."
  (->> all-symbols
       keys
       sort
       vec))

(defn matching-symbols
  "Return vector of [{:name symbol-name :matches [infodict+]}+] of symbols that match Q."
  [q]
  (->> (filter (fn [symbol-str] (.contains symbol-str q))
               all-symbols-keys)
       (take 50) ; reasonable limit <3
       (map (fn [match]
              {:name match
               :matches (all-symbols match)}))))
