(ns clojureref.handler
  (:use [compojure.core :only (context defroutes GET)])
  (:require [clojure.data.json :as json]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :refer [wrap-params]]
            [clojureref.api :as api]))

;; (def main-page
;;   "Builds the main page for the instant Clojure cheatsheet"
;;   (memoize (fn []
;;              (let [all-docs (map layout/html-docs-for-ns (sort (map str (all-ns))))]
;;                (layout/page "Instant Clojure Cheatsheet"
;;                             [:div
;;                              [:div.span4
;;                               (apply (partial vector :div) all-docs)]
;;                              [:div#source.span6]])))))



(defroutes app-routes
  ;; (GET "/" [] (main-page))
  ;; (GET "?:q" [q] (main-page))
  (context "/api" [] api/routes)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/site app-routes)
      wrap-params))
