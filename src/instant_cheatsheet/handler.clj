(ns instant-cheatsheet.handler
  "Primary Compjure/Ring entry point of the app"
  (:require clojure.java.classpath
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
                                [sources :as sources])))

;; ## SOURCE RELOADING MIDDLEWARE

(def ^:private reload-files-if-needed
  "Look for Clojure files that were modified since the last time this function was called;
   if any were found, reload them and call `sources/set-namespaces!`."
  (let [last-reload  (atom 0)
        needs-reload (atom false)]
    (fn ([]
        (let [last-reload-timestamp @last-reload]
          (reset! last-reload (.getTime (java.util.Date.)))                               ; reset `last-reload` before commencing so subsequent API calls
          (->> (clojure.java.classpath/classpath-directories)                             ; in short succession don't reload a file more than once
               (pmap (partial reload-files-if-needed last-reload-timestamp))
               dorun)
          (when @needs-reload
            (reset! needs-reload false)
            (println "Reloading documentation...")
            (sources/set-namespaces! (all-ns)))))
      ([last-reload ^java.io.File file]
       (if (.isDirectory file) (->> (.listFiles file)
                                    (pmap (partial reload-files-if-needed last-reload))
                                    dorun)
           (when (and (ns-file/clojure-file? file)
                      (> (.lastModified file) last-reload))
             (try
               (do (load-file (.getPath file))
                   (reset! needs-reload true))
               (catch Exception _))))))))

(defn- reload-if-needed-middleware
  "Middleware that sees if any files in the classpath have been modified since the last call; if so, reloads
   namespaces and calls `sources/set-namespaces!`."
  [handler]
  (fn [request]
    (future
      (reload-files-if-needed)) ; reload files asynchronously so we don't slow down API calls
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
    (reload-files-if-needed)
    (future
      (reset! jetty-instance (jetty/run-jetty app {:port 13370
                                                   :join? false})))))
