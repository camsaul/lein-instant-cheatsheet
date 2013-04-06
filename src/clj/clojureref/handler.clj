(ns clojureref.handler
  (:use compojure.core
        clojure.repl)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojureref.layout :as layout]
            hiccup.util
            clojure.core
            clojure.core.protocols
            [clojure.core.reducers :as r]            
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
            clojure.tools.trace))

(def main-page
  "Builds the main page for the instant Clojure cheatsheet"
  (memoize (fn []
             (let [all-docs (map layout/html-docs-for-ns (sort (map str (all-ns))))]
               (layout/page "Instant Clojure Cheatsheet"
                            [:div
                             [:div.span4
                              (apply (partial vector :div) all-docs)]
                             [:div#source.span6]])))))

(defroutes app-routes
  (GET "/" [] (main-page))
  (GET "?:q" [q] (main-page))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

