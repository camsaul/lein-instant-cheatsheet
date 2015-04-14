(ns instant-cheatsheet.index
  "The Hiccup templates that define index.html"
  (:require (hiccup [core :refer [html]]
                    [page :refer [html5 include-css include-js]])))

(declare head
         body
         header
         footer
         left-column
         right-column
         right-column-symbol-div
         css-includes
         js-includes)


;;; HELPER FNS + MACROS

(defmacro ng-repeat
  "Convenience that wraps BODY in an element like <div ng-repeat='VARNAME in COLL'> ... </div>"
  [varname coll & body]
  `[:div {:ng-repeat ~(str varname " in " coll)}
    ~@body])

(defmacro bind [symb]
  (format "{{%s}}" (name symb)))


;;; INDEX.HTML TEMPLATES

(def page-name
  "Name that should be used in title / header of the page."
  "Instant Clojure Cheatsheet")

(def main-page
  "Top-level template for index.html"
  (memoize
   (fn []
     (html5 {:class "no-js"
             :ng-app "cheatsheet"}
            (head)
            (body)))))

(defn head
  "Template for HTML <head> and contents."
  []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:title page-name]
   (apply include-css css-includes)])

(defn body
  "Template for HTML <body> and contents."
  []
  [:body
   [:div.container {:ng-controller "MainController"}
    (header page-name)
    [:input#filter.span12 {:type "text"
                           :placeholder "filter"
                           :ng-model "textInput"
                           :ng-change "onTextChange()"}]
    [:div.row
     (left-column)
     (right-column)]]
   (footer)
   ;; JS is placed at the end so the pages load oatfaster
   (apply include-js js-includes)])

(defn left-column
  "Template for the HTML that lays out the left column."
  []
  [:div#left.span2
   (ng-repeat result results
     [:div.namespace
      [:a {:href (str "#?q=" (bind result.name))
           :tooltip-placement "right"
           :tooltip-html-unsafe (html [:div (bind "leftTooltip(result)")])}
       (bind result.name)]])])

(defn right-column
  "Template for HTML that lays out the right-column."
  []
  [:div#source.span10
   (ng-repeat result results
     (ng-repeat subresult result.matches
       (right-column-symbol-div)))])

(defn right-column-symbol-div
  "The div template that is used to display symbol entries in the right column."
  []
  [:div
   [:div.doc
    [:i.right (bind subresult.namespace)]
    [:b (bind result.name) " "]
    [:i.args (bind subresult.args)]
    [:div.description
     [:i {:ng-if "subresult.special_type"}
      (bind subresult.special_type) " "]
     [:div {:markdown "subresult.doc"}]]
    [:i.url {:ng-if "subresult.url"}
     "See "
     [:a {:href (bind subresult.url)}
      (bind subresult.url)]]]
   [:div.right
    [:a.fn-source {:href "http://clojuredocs.org/{{subresult.namespace}}/{{result.name}}#examples"}
     "examples"]
    " "
    [:a.fn-source {:href "#"
                   :ng-mouseover "subresult.src || fetchSource(result, subresult)"
                   :tooltip-html-unsafe (html [:pre.source-tooltip (bind "subresult.src || ''")])
                   :tooltip-placement "left"}
     "source"]]
   [:hr]])

(defn header
  "Helper method to build the page's header."
  [title]
  [:div.row
   [:span.span12
    [:div.navbar
     [:div.navbar-inner
      [:div.container
       [:div.nav-collapse.collapse
        [:ul.nav
         [:li [:a title]]]]]]]]])

(defn footer
  "Helper method to build the page's footer."
  []
  [:footer
   [:div.container
    [:div.row
     [:h4 [:a {:href "http://github.com/cammsaul"} "© 2013 - 2015 Cam Saul"]]]]])

(def css-includes ["cheatsheet/css/bootstrap.min.css"
                   "cheatsheet/css/application.css"])

(def js-includes ["//ajax.googleapis.com/ajax/libs/angularjs/1.3.11/angular.min.js"
                  "//ajax.googleapis.com/ajax/libs/angularjs/1.3.11/angular-resource.min.js"
                  "//ajax.googleapis.com/ajax/libs/angularjs/1.3.11/angular-route.min.js"
                  "//cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.12.0/ui-bootstrap-tpls.min.js"
                  "//cdnjs.cloudflare.com/ajax/libs/showdown/0.3.1/showdown.min.js"
                  "cheatsheet/js/bootstrap-tooltip.min.js"
                  "cheatsheet/js/app.js"])
