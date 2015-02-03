(ns clojureref.sources)

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
               (find-ns ns-symb)))
       (drop 1)
       (take 2)))

(defn symbols-map-for-ns [ns]
  (let [ns-name-str (-> ns ns-name str)
        ns-info {:namespace ns-name-str}]
    (->> ns
         ns-publics                     ; map of symbol -> var
         keys
         ;; (filter symbol?)
         (map str)
         (map (fn [symb]
                [symb ns-info])))))

(def all-symbols
  "Dictionary of symbol-string -> [info-dict+]"
  (->> namespaces
       (mapcat symbols-map-for-ns)
       (group-by first)
       (map (fn [[k valls]]
              {k (mapv second valls)}))
       (reduce merge)))

(def all-symbols-keys
  "Sorted vector of all string name of all symbols."
  (->> all-symbols
       keys
       sort
       vec))

(defn matching-symbols
  "Return vector of [[symbol-name [infodict+]]+] of symbols that match Q."
  [q]
  (->> (filter (partial re-find (re-pattern q))
               all-symbols-keys)
       (mapcat (fn [match]
                 [match (all-symbols match)]))))
