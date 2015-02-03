(defproject clojureref "1.0"
  :description "Cam Saul's Instant Clojure Cheatsheet"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "LATEST"]
                 [org.clojure/algo.generic "LATEST"]
                 [org.clojure/algo.monads "LATEST"]
                 [org.clojure/core.async "LATEST"]
                 [org.clojure/core.cache "LATEST"]
                 [org.clojure/core.contracts "LATEST"]
                 [org.clojure/core.incubator "LATEST"]
                 [org.clojure/core.logic "LATEST"]
                 [org.clojure/core.match "LATEST"]
                 [org.clojure/core.memoize "LATEST"]
                 [org.clojure/core.typed "LATEST"]
                 [org.clojure/core.unify "LATEST"]
                 [org.clojure/data.codec "LATEST"]
                 [org.clojure/data.csv "LATEST"]
                 [org.clojure/data.finger-tree "LATEST"]
                 [org.clojure/data.generators "LATEST"]
                 [org.clojure/data.json "LATEST"]
                 [org.clojure/data.priority-map "LATEST"]
                 [org.clojure/data.xml "LATEST"]
                 [org.clojure/data.zip "LATEST"]
                 [org.clojure/java.classpath "LATEST"]
                 [org.clojure/java.data "LATEST"]
                 [org.clojure/java.jdbc "LATEST"]
                 [org.clojure/java.jmx "LATEST"]
                 [org.clojure/math.combinatorics "LATEST"]
                 [org.clojure/math.numeric-tower "LATEST"]
                 [org.clojure/test.generative "LATEST"]
                 [org.clojure/tools.cli "LATEST"]
                 [org.clojure/tools.logging "LATEST"]
                 [org.clojure/tools.macro "LATEST"]
                 [org.clojure/tools.namespace "LATEST"]
                 [org.clojure/tools.nrepl "LATEST"]
                 [org.clojure/tools.reader "LATEST"]
                 [org.clojure/tools.trace "LATEST"]
                 [amalloy/ring-gzip-middleware "LATEST"]
                 [compojure "LATEST"]
                 [hiccup "LATEST"]
                 [swiss-arrows "LATEST"]
                 [korma "LATEST"]]
  :plugins [[lein-ring "LATEST"]
            [codox "LATEST"] ; TODO marginalia instead
            [cider/cider-nrepl "LATEST"]]
  :ring {:handler clojureref.handler/app}
  :update :always
  :source-paths ["src/clj"]
  :codox {:sources ["src/clj"]
          :src-dir-uri "http://github.com/cammsaul/instant-clojure-cheetsheet/blob/master/"
          :src-linenum-anchor-prefix "L"}
  :profiles
  {:dev {:dependencies [[ring-mock "LATEST"]]}}
  :min-lein-version "2.0.0")
