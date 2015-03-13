(ns leiningen.instant-cheatsheet
  (:require [clojure.tools.namespace.find :as ns-find]
            [leiningen.core.eval :as lein]))

(defn instant-cheatsheet [project & args]
  (let [main-class (:main project)
        source-paths (->> project
                          :source-paths
                          (map #(java.io.File. ^String %)))]
    (lein/eval-in-project
     (-> project
         (update-in [:dependencies] conj ['instant-cheatsheet "1.0"]))
     `(do (require '~main-class)
          (require 'clojure.java.browse
                   'clojureref.handler
                   'clojureref.sources)
          (clojureref.sources/set-namespaces! (all-ns))
          (clojureref.handler/start-jetty)
          (clojure.java.browse/browse-url "http://localhost:13370")))))
