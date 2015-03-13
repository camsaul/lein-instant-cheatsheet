lein-instant-cheatsheet
==========================

[![Clojars Project](http://clojars.org/lein-instant-cheatsheet/latest-version.svg)](http://clojars.org/lein-instant-cheatsheet)

[![Dependencies Status](http://jarkeeper.com/cammsaul/instant-clojure-cheatsheet/status.png)](http://jarkeeper.com/cammsaul/instant-clojure-cheatsheet)

[Instant Cheatsheet][2] instantly creates a cheatsheet for your project and included dependecies. Just add `lein-instant-cheatsheet` to your Leiningen `:plugins` and run `lein instant-cheatsheet`. Magic!
[![Screenshot](screenshot.png)][2]

[Instant Cheatsheet][2] has a few features that might just make it your go-to Clojure reference:


#### Symbol Filtering ####
Instant Cheatsheet has a built-in filter bar at the top to search for matching symbols. A list of matching symbols and their namespaces appears on the left side of the screen; documentation for those symbols appear on the right. You can also see documentation in a tooltip by hovering over symbols on the left, and you can see source code (if available) by hovering over the `source` links on the right.


#### Easy Customization ####
Instant Cheatsheet is built in Clojure. You can easily add any libraries you want to your cheatsheet. By default, I've added almost all of the Clojure core and contrib libraries, but it's really easy to choose your own -- just add the dependencies in `project.clj` and add `use` or `require` statements in `sources.clj`. That's it. Run `lein ring server` from the project's root directory and you have your own Instant Cheatsheet. You can use it to make cheatsheets for `core.logic` or even your own projects.

Instant Cheatsheet uses [Hiccup][3], [Twitter Bootstrap][4], and [AngularJS][6], so it should be pretty easy to tweak as needed.


#### Filtering Based on GET Params ####
Last but not least, Instant Cheatsheet will automatically use the GET parameter `q` as an initial value for the filter bar. That means a request like  [http://cammsaul.github.io/instant-clojure-cheatsheet/?print][5] will automatically search for symbols containing `print`.

You can create an Emacs function to search Instant Cheatsheet, and even bind it to a keyboard shortcut:
```Lisp
(defun instant-cheatsheet-search (search-term)
  "Opens Instant Cheatsheet in a new browser tab and searches for SEARCH-TERM."
  (interactive "sSearch Instant Cheatsheet for: ")
  (browse-url
   (concat "http://localhost:13370/#?q="
           (url-hexify-string search-term))))

(define-key clojure-mode-map (kbd "<f12> i") 'instant-cheatsheet-search)
(define-key cider-repl-mode-map (kbd "<f12> i") 'instant-cheatsheet-search)
```

If you have an active region (highlighted text), hitting `f12 i` (or the keyboard shortcut of your choice) will open a new Instant Cheatsheet browser tab with the highlighted text as the inital filter text. If you don't have any text highlighted, it will prompt you to enter a search query in the minibuffer.

You can try my instant cheatsheet [here][2].

[1]: http://jafingerhut.github.io/cheatsheet-clj-1.3/cheatsheet-tiptip-no-cdocs-summary.html
[2]: http://cammsaul.github.io/instant-clojure-cheatsheet/
[3]: https://github.com/weavejester/hiccup
[4]: http://twitter.github.io/bootstrap/
[5]: http://cammsaul.github.io/instant-clojure-cheatsheet/?print
[6]: https://angularjs.org
[7]: https://github.com/weavejester/codox
