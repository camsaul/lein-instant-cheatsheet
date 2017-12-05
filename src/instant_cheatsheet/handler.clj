(ns instant-cheatsheet.handler
  "Primary Compjure/Ring entry point of the app"
  (:require [clojure.tools.namespace.file :as ns-file]
            [compojure
             [core :refer [context defroutes GET]]
             [route :as route]]
            [instant-cheatsheet
             [api :as api]
             [index :as index]
             [sources :as sources]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware
             [gzip :refer [wrap-gzip]]
             [params :refer [wrap-params]]])
  (:import java.io.File))

;; ## SOURCE RELOADING MIDDLEWARE

(def ^:private last-reload-started
  "System time in milliseconds when the last reload was started."
  (atom 0))

(def ^:private files-have-been-reloaded
  "Have any files been reloaded recently? If so, we need to update the namespaces presented in the UI by calling
   `set-namespaces!`."
  (atom false))


(declare reload-if-needed)

(def source-directories
  "Sequence of directories (as strings) to look for source files in. Set on launch by looking at the `:source-paths`
  property of the project."
  (atom nil))

(defn- reload-directory-if-needed [last-reload-started, ^File dir]
  (doseq [file-or-dir (.listFiles dir)]
    (reload-if-needed last-reload-started file-or-dir)))

(defn- reload-file-if-needed [last-reload-started, ^File file]
  (when (and (ns-file/clojure-file? file)
             (> (.lastModified file) last-reload-started))
    (println "Reloading" (str file))
    (try
      (load-file (.getPath file))
      (reset! files-have-been-reloaded true)
      (catch Exception e
        (println "Error loading file:" file e)))))

(defn- reload-if-needed [last-reload-started, ^File file-or-dir]
  (if (.isDirectory file-or-dir)
    (reload-directory-if-needed last-reload-started file-or-dir)
    (reload-file-if-needed      last-reload-started file-or-dir)))

(defn- reload-project-if-needed
  "Look for Clojure files that were modified since the last time this function was called;
   if any were found, reload them and call `sources/set-namespaces!`."
  []
  ;; This may end up getting called a few times before `source-directories` is populated. Wait until it is before
  ;; trying to reload the project.
  (when (seq @source-directories)
    (let [last-reload-started-timestamp @last-reload-started]
      ;; reset `last-reload-started` before commencing so subsequent API calls
      ;; in short succession don't reload a file more than once
      (reset! last-reload-started (System/currentTimeMillis))
      (doseq [dir @source-directories]
        (reload-directory-if-needed last-reload-started-timestamp (File. (str dir))))
      (when @files-have-been-reloaded
        (reset! files-have-been-reloaded false)
        (println "Reloading documentation...")
        (sources/set-namespaces! (vec (all-ns)))))))

(defn- reload-if-needed-middleware
  "Middleware that sees if any files in the source directories have been modified since the last call; if so, reloads
   namespaces and calls `sources/set-namespaces!`."
  [handler]
  (fn [request]
    ;; reload files asynchronously so we don't slow down API calls
    (future (try (reload-project-if-needed)
                 (catch Throwable e
                   (println "Error reloading project:" e))))
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
