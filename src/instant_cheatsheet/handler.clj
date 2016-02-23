(ns instant-cheatsheet.handler
  "Primary Compjure/Ring entry point of the app"
  (:require [clojure.java.classpath :as classpath]
            (clojure.tools.namespace [file :as ns-file]
                                     ;; [find :as ns-find]
                                     [repl :as repl])
            (compojure [core :refer [context defroutes GET]]
                       [route :as route])
            [ring.adapter.jetty :as jetty]
            (ring.middleware [gzip :refer [wrap-gzip]]
                             [params :refer [wrap-params]])
            (instant-cheatsheet [api :as api]
                                [index :as index]
                                [sources :as sources]))
  (:import java.io.File))

;; ## SOURCE RELOADING MIDDLEWARE

(def ^:private last-reload
  (atom 0))

(def ^:private needs-reload
  (atom false))

(declare reload-if-needed)

(defn- reload-directory-if-needed [last-reload, ^File dir]
  (doseq [file-or-dir (.listFiles dir)]
    (reload-if-needed last-reload file-or-dir)))

(defn- reload-file-if-needed [last-reload, ^File file]
  (when (and (ns-file/clojure-file? file)
             (> (.lastModified file) last-reload))
    (try
      (load-file (.getPath file))
      (reset! needs-reload true)
      (catch Exception e
        (println "Error loading file:" file e)))))

(defn- reload-if-needed [last-reload, ^File file-or-dir]
  (if (.isDirectory file-or-dir)
    (reload-directory-if-needed last-reload file-or-dir)
    (reload-file-if-needed      last-reload file-or-dir)))

(defn- reload-project-if-needed
  "Look for Clojure files that were modified since the last time this function was called;
   if any were found, reload them and call `sources/set-namespaces!`."
  []
  (let [last-reload-timestamp @last-reload]
    ;; reset `last-reload` before commencing so subsequent API calls
    ;; in short succession don't reload a file more than once
    (reset! last-reload (System/currentTimeMillis))
    (doseq [dir (classpath/classpath-directories)]
      (reload-directory-if-needed last-reload-timestamp dir))
    (when @needs-reload
      (reset! needs-reload false)
      (println "Reloading documentation...")
      (sources/set-namespaces! (vec (all-ns))))))

(defn- reload-if-needed-middleware
  "Middleware that sees if any files in the classpath have been modified since the last call; if so, reloads
   namespaces and calls `sources/set-namespaces!`."
  [handler]
  (fn [request]
    (future (reload-project-if-needed)) ; reload files asynchronously so we don't slow down API calls
    (handler request)))


;; ## ROUTES / HANDLER

(defroutes ^:private app-routes
  (GET "?:q" [q] (index/main-page))
  (GET "/" [] (index/main-page))
  (context "/api" [] api/routes)
  (route/resources "/")
  (route/not-found "Not Found"))

(def ^:private app
  (-> app-routes
      reload-if-needed-middleware
      wrap-params
      wrap-gzip))


;; ## JETTY SERVER

(def ^:private jetty-instance
  (atom nil))

(defn start-jetty! []
  (when-not @jetty-instance
    (reload-project-if-needed)
    (future
      (reset! jetty-instance (jetty/run-jetty app {:port 13370
                                                   :join? false})))))
