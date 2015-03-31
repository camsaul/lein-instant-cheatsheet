(ns leiningen.instant-cheatsheet
  (:require [clojure.tools.namespace.find :as ns-find]
            [leiningen.core.eval :as lein]))

(defn- instant-cheatsheet-version [{:keys [plugins]}]
  {:post [(string? %)]}
  (some (fn [[plugin-name version]]
          (when (= plugin-name 'lein-instant-cheatsheet/lein-instant-cheatsheet)
            version))
        plugins))

(defn instant-cheatsheet
  "Start a cheatsheet server for this project + dependencies."
  [project & args]
  (let [main-class (:main project)]
    (lein/eval-in-project
     (-> project
         (update-in [:dependencies] conj ['lein-instant-cheatsheet (instant-cheatsheet-version project)]))
     `(do (require '~main-class
                   'clojure.java.browse
                   'instant-cheatsheet.handler)
          (instant-cheatsheet.handler/start-jetty!)
          (clojure.java.browse/browse-url "http://localhost:13370")))))
