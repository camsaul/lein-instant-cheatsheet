(ns clojureref.api
  (:use [compojure.core :only (defroutes GET)])
  (:require [clojure.data.json :as json]
            [clojureref.sources :as sources]))

(defmacro def-api-fn [name args-form & body]
  `(defn ~name ~args-form
     {:status 200
      :headers {"Content-Type" "application/json"}
      :body (clojure.data.json/write-str
             (do ~@body))}))

(def-api-fn get-matches [q]
  {:q q
   :results (sources/matching-symbols q)})

(defroutes routes
  (GET "/matches" [q] (get-matches q)))
