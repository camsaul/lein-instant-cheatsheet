(ns clojureref.layout
  (:use [hiccup.page :only (html5 include-css include-js)]
        [hiccup.core :only (html h)])
  (:require [clojureref.util :as util]))

(defn make-header
  "Helper method to build the page's header."
  [title]
  [:div.row
   [:span.span9
    [:div.navbar
     [:div.navbar-inner
      [:div.container
       [:div.nav-collapse.collapse
        [:ul.nav
         [:li [:a title]]]]]]]]
   [:input#filter.span10 {:type "text" :placeholder "filter"}]]
)
(defn make-footer
  "Helper method to build the page's footer."
  []
  [:footer
   [:div.container
    [:div.row
     [:h3.footer-title "2013 Cam Saul"]]]])

(defn page
  "Takes a page title and hiccup HMTL elements and wraps it with common stylesheet references etc."
  [title body]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:title title]
    (include-css "/css/bootstrap.min.css"
                 "/css/flat-ui.min.css"
                 "/css/application.css")]
   [:body
    [:div.container
     (make-header title)
     body]
    (make-footer)
    ;; JS is placed at the end so the pages load faster
    (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"
                "/js/bootstrap-tooltip.min.js"
                "/js/application.js")]))

(defn build-fn-div
  "Helper method to build the entries and popovers for various symbols tags."
  [nmsp [symb doc forms source]]
  [:div.fn
   [:a {:data-html "true"
        :data-original-title (html [:div.doc [:i.right nmsp] [:b symb] " " (util/replace-nl-with-br doc)])
        :data-placement "right"
        :source (when-let [source (util/replace-nl-with-br source)]
                  (html [:a.fn-source {:data-html "true"
                                       :data-original-title (html [:pre source])
                                       :data-placement "right"}
                         "source"]))}
    symb]
   forms])

(def html-docs-for-ns
  "Formats docstrings for a namespace as html."
  (memoize
   (fn [nmsp]
     (let [docs (util/doc-for-ns nmsp)]
       [:div.row
        [:div.namespace
         [:h4 nmsp]
         (map (partial build-fn-div nmsp) docs)]]))))
