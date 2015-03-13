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
         (update-in [:dependencies] conj ['lein-instant-cheatsheet "2.0.0"]))
     `(do (require '~main-class)
          (require 'clojure.java.browse
                   'instant-cheatsheet.handler
                   'instant-cheatsheet.sources)
          (instant-cheatsheet.sources/set-namespaces! (all-ns))
          (instant-cheatsheet.handler/start-jetty)
          (clojure.java.browse/browse-url "http://localhost:13370")))))
