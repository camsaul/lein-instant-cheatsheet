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
         [:li [:a title]]]]]]]]])

(defn make-footer
  "Helper method to build the page's footer."
  []
  [:footer
   [:div.container
    [:div.row
     [:h4 [:a {:href "http://www.camsaul.com"} "2013 Cam Saul"]]]]])

(defn build-fn-div
  "Helper method to build the entries and popovers for various symbols tags."
  [nmsp [symb doc forms source]]
  [:div.fn
   [:a {:data-html "true"
        :data-original-title (html [:div.doc [:i.right nmsp] [:b symb] " " (util/replace-nl-with-br doc)])
        :data-placement "right"
        ;; :source (when-let [source (util/replace-nl-with-br source)]
        ;;           (html [:a.fn-source {:data-html "true"
        ;;                                :data-original-title (html [:pre source])
        ;;                                :data-placement "right"}
        ;;                  "source"]))
        }
    symb]
   forms])
