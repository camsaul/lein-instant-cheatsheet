(ns leiningen.instant-cheatsheet
  (:require [leiningen.core.eval :as lein]))

(defn- instant-cheatsheet-version [{:keys [plugins]}]
  {:post [(string? %)]}
  (some (fn [[plugin-name version]]
          (when (= plugin-name 'lein-instant-cheatsheet/lein-instant-cheatsheet)
            version))
        plugins))

(defn instant-cheatsheet
  "Start a cheatsheet server for this project + dependencies."
  [project & args]
  (lein/eval-in-project
   (-> project
       (update-in [:dependencies] conj ['lein-instant-cheatsheet (instant-cheatsheet-version project)]))
   `(do
      ;; load the instant-cheatsheet namespaces
      (require 'clojure.java.browse
               'instant-cheatsheet.handler)
      ;; start the web server
      (instant-cheatsheet.handler/start-jetty!)
      ;; set the atom that keeps track of directories to search for Clojure dependencies in
      (reset! instant-cheatsheet.handler/source-directories ~(vec (:source-paths project)))
      ;; ok, now open the instant cheatsheet in the browser :)
      (clojure.java.browse/browse-url "http://localhost:13370"))))
