(ns clojureref.handler
  "Primary Compjure/Ring entry point of the app"
  (:require (compojure [core :refer [context defroutes GET]]
                       [route :as route])
            [ring.adapter.jetty :as jetty]
            (ring.middleware [gzip :refer [wrap-gzip]]
                             [params :refer [wrap-params]])
            (clojureref [api :as api]
                        [index :as index])))

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

(defn start-jetty []
  (jetty/run-jetty app {:port 13370
                        :join? false}))
