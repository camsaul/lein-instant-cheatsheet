(ns clojureref.handler
  "Primary Compjure/Ring entry point of the app"
  (:use [compojure.core :only (context defroutes GET)]
        [ring.middleware.params :only (wrap-params)]
        [ring.middleware.gzip :only (wrap-gzip)])
  (:require ring.middleware.multipart-params
            [clojure.data.json :as json]
            [compojure.route :as route]
            [clojureref.api :as api]
            [clojureref.index :as index]))

(defroutes app-routes
  (GET "?:q" [q] (index/main-page))
  (GET "/" [] (index/main-page))
  (context "/api" [] api/routes)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      wrap-params
      wrap-gzip))
