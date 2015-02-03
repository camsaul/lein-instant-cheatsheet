(ns clojureref.api
  (:use [compojure.core :only (defroutes GET)])
  (:require [clojure.data.json :as json]
            [clojureref.sources :as sources]))

(defmacro def-api-fn [name args-form & body]
  `(defn ~name ~args-form
     {:status 200
      :headers {"Content-Type" "application/json"}
      :body (clojure.data.json/write-str
             (do ~@body)
             ;; (do (println ~(str name))
             ;;     (time ~@body))
             )}))

(def-api-fn get-matches [q]
  (sources/matching-symbols q))

(def-api-fn get-source [ns symb]
  (sources/get-source ns symb))

(defroutes routes
  (GET "/matches" [q] (get-matches q))
  (GET "/source" [ns symbol] (get-source ns symbol)))
