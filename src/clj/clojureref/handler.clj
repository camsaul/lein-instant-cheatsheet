(ns clojureref.handler
  (:use [compojure.core :only (context defroutes GET)])
  (:require [clojure.data.json :as json]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :refer [wrap-params]]
            [clojureref.api :as api]
            [clojureref.index :as index]))

(defroutes app-routes
  (GET "?:q" [q] (index/main-page))
  (GET "/" [] (index/main-page))
  (context "/api" [] api/routes)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      wrap-params))
