(defproject lein-instant-cheatsheet "2.2.2"
  :description "Leiningen plugin to create a cheatsheet for your Clojure project <3"
  :url "https://github.com/camsaul/lein-instant-cheatsheet"
  :license {:name "MIT"
            :url "https://raw.githubusercontent.com/camsaul/lein-instant-cheatsheet/master/LICENSE.txt"}
  :min-lein-version "2.0.0"
  :eval-in-leiningen true
  :dependencies [[org.clojure/tools.namespace "0.2.10"]
                 [amalloy/ring-gzip-middleware "0.1.3"]
                 [cheshire "5.8.0"]
                 [compojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [ring/ring-jetty-adapter "1.6.3"]]
  :profiles {:dev {:plugins [[jonase/eastwood "0.2.3"]
                             [lein-bikeshed "0.3.0"]]
                   :eastwood {:add-linters [:unused-private-vars]
                              :exclude-linters [:unused-ret-vals]}}}
   :deploy-repositories [["clojars" {:sign-releases false}]])
