(defproject lein-instant-cheatsheet "2.1.5"
  :description "Leiningen plugin to create a cheatsheet for your Clojure project <3"
  :url "https://github.com/cammsaul/lein-instant-cheatsheet"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :min-lein-version "2.0.0"
  :eval-in-leiningen true
  :dependencies [[org.clojure/java.classpath "0.2.3"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [amalloy/ring-gzip-middleware "0.1.3"]
                 [cheshire "5.5.0"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [ring/ring-jetty-adapter "1.4.0"]]
  :profiles {:dev {:plugins [[jonase/eastwood "0.2.3"]
                             [lein-bikeshed "0.3.0"]]
                   :eastwood {:add-linters [:unused-private-vars]
                              :exclude-linters [:unused-ret-vals]}}})
