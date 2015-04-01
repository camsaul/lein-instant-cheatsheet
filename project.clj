(defproject lein-instant-cheatsheet "2.1.2"
  :description "Leiningen plugin to create a cheatsheet for your Clojure project <3"
  :url "https://github.com/cammsaul/instant-clojure-cheatsheet"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :min-lein-version "2.0.0"
  :eval-in-leiningen true
  :dependencies [[org.clojure/java.classpath "0.2.2"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [amalloy/ring-gzip-middleware "0.1.3"]
                 [cheshire "5.4.0"]
                 [compojure "1.3.2"]
                 [hiccup "1.0.5"]
                 [ring/ring-jetty-adapter "1.3.2"]]
  :profiles {:dev {:plugins [[jonase/eastwood "0.2.1"]
                             [lein-bikeshed "0.2.0"]]}})
