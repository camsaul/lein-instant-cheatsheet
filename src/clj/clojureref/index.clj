(ns clojureref.index
  (:require [clojureref.layout :as layout])
  (:use [hiccup.page :only (html5 include-css include-js)]
        [hiccup.core :only (html)]))

(declare main-head
         main-body
         left-column
         right-column
         css-includes
         js-includes)

(defn -main-page []
  (html5 {:class "no-js"
          :ng-app "cheatsheet"}
         (main-head)
         (main-body)))

(def main-page (memoize -main-page))

(defn main-head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:title "Instant Clojure Cheatsheet"]
   (apply include-css css-includes)])

(defn main-body []
  [:body
   [:div.container {:ng-controller "MainController"}
    (layout/make-header "Instant Clojure Cheatsheet")
    [:input#filter.span10 {:type "text"
                           :placeholder "filter"
                           :ng-model "textInput"
                           :ng-change "onTextChange()"}]
    [:div.row
     (left-column)
     (right-column)]]
   (layout/make-footer)
   ;; JS is placed at the end so the pages load faster
   (apply include-js js-includes)])

(defn symbol-doc-div []
  [:div.doc
   [:i.right "{{subresult.namespace}}"]
   [:b "{{result.name}} "]
   [:i {:style "color: #08c;"}
    "{{subresult.args}}"]
   [:br]
   [:i {:ng-if "subresult.special_type"}
    "{{subresult.special_type}} "]
   "{{subresult.doc}}"
   [:i {:ng-if "subresult.url"}
    "<br /><br />See "
    [:a {:href "{{subresult.url}}"}
     "{{subresult.url}}"]]])

(defn left-column []
  [:div#left.span4
   [:div.namespace {:style "display: block;"
                    :ng-repeat "result in results"}
    [:a {:href "#?q={{result.name}}"
         :tooltip-placement "right"
         :tooltip-html-unsafe (html [:div "{{leftTooltip(result)}}"])}
     "{{result.name}}"]]])

(defn right-column []
  [:div#source.span6
   [:div {:ng-repeat "result in results"}
    [:div {:ng-repeat "subresult in result.matches"}
     (symbol-doc-div)
     [:span {:style "float: right;"}
      [:a.fn-source {:href "http://clojuredocs.org/{{subresult.namespace}}/{{result.name}}#examples"}
       "examples"]
      " "
      [:a.fn-source {:href "#"
                     :ng-mouseover "subresult.src || fetchSource(result, subresult)"
                     :tooltip-html-unsafe (html [:pre "{{subresult.src || ''}}"])
                     :tooltip-placement "left"}
       "source"]]
     [:hr]]]])

(def css-includes ["css/bootstrap.min.css"
                   "css/application.css"])

(def js-includes ["//ajax.googleapis.com/ajax/libs/angularjs/1.3.11/angular.min.js"
                  "//ajax.googleapis.com/ajax/libs/angularjs/1.3.11/angular-resource.min.js"
                  "//ajax.googleapis.com/ajax/libs/angularjs/1.3.11/angular-route.min.js"
                  "//cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.12.0/ui-bootstrap-tpls.min.js"
                  "js/bootstrap-tooltip.min.js"
                  "js/app.js"])
