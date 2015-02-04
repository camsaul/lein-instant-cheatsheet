(ns clojureref.index
  "The Hiccup templates that define index.html"
  (:use [hiccup.page :only (html5 include-css include-js)]
        [hiccup.core :only (html)]))

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

(defmacro ng-repeat [varname coll & body]
  `[:div {:ng-repeat ~(str varname " in " coll)}
    ~@body])

(defmacro ng-if
  ([condition elem]
   (let [[type & elem] elem
         [attrs & body] (if (map? (first elem)) elem
                            (concat [nil] elem))
         attrs (-> (or attrs {})
                   (assoc :ng-if (str condition)))]
     `[~type ~attrs ~@body]))
  ([[condition element]]
   `(ng-if ~condition ~element)))

(defn binder
  "Used by the #bind reader macro:
   `#bind result.name ->> '{{result.name}}'"
  [symb]
  (str "{{" (name symb) "}}"))


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
    [:input#filter.span10 {:type "text"
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
  [:div#left.span4
   (ng-repeat result results
     [:div.namespace
      [:a {:href (str "#?q=" #bind result.name)
           :tooltip-placement "right"
           :tooltip-html-unsafe (html [:div #bind "leftTooltip(result)"])}
       #bind result.name]])])

(defn right-column
  "Template for HTML that lays out the right-column."
  []
  [:div#source.span6
   (ng-repeat result results
     (ng-repeat subresult result.matches
       (right-column-symbol-div)))])

(defn right-column-symbol-div
  "The div template that is used to display symbol entries in the right column."
  []
  [:div
   [:div.doc
    [:i.right #bind subresult.namespace]
    [:b #bind result.name " "]
    [:i.args #bind subresult.args]
    [:div.description
     (ng-if subresult.special_type
            [:i #bind subresult.special_type " "])
     #bind subresult.doc]
    (ng-if subresult.url
           [:i.url
            "See "
            [:a {:href #bind subresult.url}
             #bind subresult.url]])]
   [:div.right
    [:a.fn-source {:href "http://clojuredocs.org/{{subresult.namespace}}/{{result.name}}#examples"}
     "examples"]
    " "
    [:a.fn-source {:href "#"
                   :ng-mouseover "subresult.src || fetchSource(result, subresult)"
                   :tooltip-html-unsafe (html [:pre #bind "subresult.src || ''"])
                   :tooltip-placement "left"}
     "source"]]
   [:hr]])

(defn header
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

(defn footer
  "Helper method to build the page's footer."
  []
  [:footer
   [:div.container
    [:div.row
     [:h4 [:a {:href "http://github.com/cammsaul"} "© 2013 - 2015 Cam Saul"]]]]])

(def css-includes ["css/bootstrap.min.css"
                   "css/application.css"])

(def js-includes ["//ajax.googleapis.com/ajax/libs/angularjs/1.3.11/angular.min.js"
                  "//ajax.googleapis.com/ajax/libs/angularjs/1.3.11/angular-resource.min.js"
                  "//ajax.googleapis.com/ajax/libs/angularjs/1.3.11/angular-route.min.js"
                  "//cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.12.0/ui-bootstrap-tpls.min.js"
                  "js/bootstrap-tooltip.min.js"
                  "js/app.js"])
