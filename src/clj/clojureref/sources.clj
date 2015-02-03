(ns clojureref.sources
  "Static data relating to namespaces and symbols."
  (:require [clojure.string :as str]
            [clojureref.util :as util]))

(defonce namespaces
  (->> '(clojure.algo.generic
         clojure.algo.monads
         clojure.core
         clojure.core.async
         clojure.core.cache
         clojure.core.contracts
         clojure.core.incubator
         clojure.core.logic
         clojure.core.match
         clojure.core.memoize
         clojure.core.protocols
         clojure.core.reducers
         clojure.core.typed
         clojure.core.unify
         clojure.data
         clojure.data.codec.base64
         clojure.data.csv
         clojure.data.finger-tree
         clojure.data.generators
         clojure.data.json
         clojure.data.priority-map
         clojure.data.xml
         clojure.data.zip
         clojure.edn
         clojure.inspector
         clojure.instant
         clojure.java.browse
         clojure.java.classpath
         clojure.java.data
         clojure.java.io
         clojure.java.javadoc
         clojure.java.jdbc
         clojure.java.jmx
         clojure.java.shell
         clojure.main
         clojure.math.combinatorics
         clojure.math.numeric-tower
         clojure.pprint
         clojure.reflect
         clojure.repl
         clojure.set
         clojure.stacktrace
         clojure.string
         clojure.template
         clojure.test
         clojure.test.generative
         clojure.tools.cli
         clojure.tools.logging
         clojure.tools.macro
         clojure.tools.namespace
         clojure.tools.namespace.find
         clojure.tools.nrepl
         clojure.tools.reader
         clojure.tools.trace
         clojure.walk
         clojure.xml
         clojure.zip
         hiccup.compiler
         hiccup.core
         hiccup.def
         hiccup.element
         hiccup.form
         hiccup.middleware
         hiccup.page
         hiccup.util
         korma.config
         korma.core
         korma.db
         korma.mysql
         swiss.arrows)
       (mapv (fn [ns-symb]
               (require ns-symb)
               (find-ns ns-symb)))))

(defn- -dox-for-symbol
  "Return dictionary of docstr, args, etc. for SYMB."
  [ns-name-str symb-name]
  (let [qualified-symbol (symbol ns-name-str symb-name)]
    (util/doc-for-symbol qualified-symbol)))

(defn- -symbol-info [ns-info ns-name-str symb-name]
  (merge ns-info
         (-dox-for-symbol ns-name-str symb-name)))

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
                  [symb-name (-symbol-info ns-info ns-name-str symb-name)]))))))

;; Create a dict of {symb-name -> [{info}+]}
(defonce all-symbols
  (->> namespaces
       (mapcat symbols-for-ns)
       (group-by first)
       (map (fn [[k valls]]
              {k (map second valls)}))
       (reduce merge)))

;; Sorted vector of all string name of all symbols.
(defonce all-symbols-keys
  (->> all-symbols
       keys
       sort
       vec))
