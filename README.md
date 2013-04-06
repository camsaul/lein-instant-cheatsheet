Instant Clojure Cheatsheet
==========================

[Instant Clojure Cheatsheet][2] is a customizable reference for Clojure with built-in filtering. It's inspired by other excellent Clojure cheatsheets like [this one][1].

[![Screenshot](screenshot.png)][2]

[Instant Clojure Cheatsheet][2] has a few features that might just make it your go-to Clojure reference:


#### Symbol Filtering ####
Instant Clojure Cheatsheet has a built-in filter bar at the top to search for matching symbols. A list of matching symbols and their namespaces appears on the left side of the screen; documentation for those symbols appear on the right. You can also see documentation in a tooltip by hovering over symbols on the left, and you can see source code (if available) by hovering over the `source` links on the right.


#### Easy Customization ####
Instant Clojure Cheatsheet is built in Clojure. You can easily add any libraries you want to your cheatsheet. By default, I've added almost all of the Clojure core and contrib libraries, but it's really easy to choose your own -- just add the dependencies in `project.clj` and add `use` or `require` statements in `handler.clj`. That's it. Run `lein ring server` from the project's root directory and you have your own Instant Clojure Cheatsheet. You can use it to make cheatsheets for `core.logic` or even your own projects. You can save your cheatsheets for later use with

	wget -R localhost:3000

or something similar like `curl` -- Instant Clojure Cheatsheet works fine as a static page.


Instant Clojure Cheatsheet uses [Hiccup][3], [Twitter Bootstrap][4], and [jQuery][6], so it should be pretty easy to tweak as needed.


#### Filtering Based on GET Params ####
Last but not least, Instant Clojure Cheatsheet will automatically use a GET parameter passed to it as an initial value for the filter bar. That means a request like  [http://cammsaul.github.io/instant-clojure-cheatsheet/?print][5] will automatically search for symbols containing `print`. 

You can create an Emacs function to search Instant Clojure Cheatsheet, and even bind it to a keyboard shortcut:
```Lisp
(defun instant-clojure-cheatsheet-search ()
  "Searches Instant Clojure Cheatsheet for a query or selected region if any."
  (interactive)
  (browse-url
   (concat
    "http://cammsaul.github.io/instant-clojure-cheatsheet/?"
    (active-region-or-prompt "Search Instant Clojure Cheatsheet for: "))))

(define-key clojure-mode-map (kbd "<f12> i") 'instant-clojure-cheatsheet-search)
(define-key nrepl-interaction-mode-map (kbd "<f12> i") 'instant-clojure-cheatsheet-search)
```

If you have an active region (highlighted text), hitting `F12 i` (or the keyboard shortcut of your choice) will open a new Instant Cheatsheet browser tab with highlighted text as the inital filter text. If you don't have any text highlighted, it will prompt you to enter a search query in the minibuffer.


#### Building Upon Instant Clojure Cheatsheet ####

Instant Clojure Cheatsheet was an afternoon project of mine to build a great single-page Clojure reference. There's lots of ways it can be made even better - turning it into a Leiningen plugin like [Codox][7] and rewriting the JS portion is ClojureScript are future possibilities that spring to mind. If you manage to add anything cool to Instant Clojure Cheatsheet, be sure to send me a pull request!

You can try my instant cheatsheet [here][2].

[1]: http://jafingerhut.github.io/cheatsheet-clj-1.3/cheatsheet-tiptip-no-cdocs-summary.html
[2]: http://cammsaul.github.io/instant-clojure-cheatsheet/
[3]: https://github.com/weavejester/hiccup
[4]: http://twitter.github.io/bootstrap/
[5]: http://cammsaul.github.io/instant-clojure-cheatsheet/?print
[6]: http://jquery.com/
[7]: https://github.com/weavejester/codox
