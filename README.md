lein-instant-cheatsheet
==========================

[![Clojars Project](https://clojars.org/lein-instant-cheatsheet/latest-version.svg)](http://clojars.org/lein-instant-cheatsheet)

[![Dependencies Status](http://jarkeeper.com/camsaul/lein-instant-cheatsheet/status.png)](http://jarkeeper.com/camsaul/lein-instant-cheatsheet) [![Circle CI](https://circleci.com/gh/camsaul/lein-instant-cheatsheet.svg?style=svg)](https://circleci.com/gh/camsaul/lein-instant-cheatsheet)

[Instant Cheatsheet][2] instantly creates a cheatsheet for your project and its dependecies.

1.  Add `lein-instant-cheatsheet` to your Leiningen `:plugins`
2.  Start the webserver with `lein instant-cheatsheet`

Instant Cheatsheet spins up a Ring server at `localhost:13370` and automatically reloads documentation whenever a Clojure file in your project changes.

[![Screenshot](screenshot.gif)][2]

[Instant Cheatsheet][2] has a few features that might just make it your go-to Clojure reference:


#### Symbol Filtering ####
Instant Cheatsheet has a built-in filter bar at the top to search for matching symbols. A list of matching symbols and their namespaces appears on the left side of the screen; documentation for those symbols appear on the right.
You can also see documentation in a tooltip by hovering over symbols on the left, and you can see source code (if available) by hovering over the `source` links on the right.
You can also search for examples from [ClojureDocs][8] by clicking the `examples` link.


#### Filtering Based on GET Params ####
Instant Cheatsheet will automatically use the GET parameter `q` as an initial value for the filter bar.
That means a request like `http://localhost:13370/#?q=print` will automatically search for symbols containing `print`.


#### Markdown Support ####

Like [Marginalia][5], Instant Cheatsheet parses docstrs as Markdown. Add examples, links, and more to your documentation!

#### Emacs Keybinding ####

You can create an Emacs function to search Instant Cheatsheet, and even bind it to a keyboard shortcut:
```Lisp
(defun string-remove-text-properties (string)
  "Return a copy of STRING with all of its text properties removed."
  (let ((s (copy-sequence string)))
    (set-text-properties 0 (length s) nil s)
    s))

(defun instant-cheatsheet-search (search-term)
  "Open a browser window and search Instant Clojure Cheatsheet for SEARCH-TERM."
  (interactive (list (read-string "Search Instant Clojure Cheatsheet for: " (when (symbol-at-point)
                                                                              (string-remove-text-properties (symbol-name (symbol-at-point)))))))
  (browse-url (concat "http://localhost:13370/#?q=" (url-hexify-string search-term))))

(define-key clojure-mode-map (kbd "<f12> i") #'instant-cheatsheet-search)
(define-key cider-repl-mode-map (kbd "<f12> i") #'instant-cheatsheet-search)
```

Hitting `<f12> i` will prompt you for a search term and open a new browser tab with that term as the initial filter text.


#### Changelog ####
*  `2.2.0` (Februrary 26th, 2016)
   *  Added support for replacing GitHub-style named `:emoji:` with the equivalent characters via [js-emoji](https://github.com/iamcal/js-emoji).
*  `2.1.5` (February 23rd, 2016)
   *  Fix issue where some project symbols wouldn't appear in cheatsheet.
*  `2.1.4` (April 14th, 2015)
   *  Fix issue where cheatsheet would appear empty on launch. ([#1](https://github.com/camsaul/lein-instant-cheatsheet/issues/1))
   *  Fix conflicts if target project had resources named `app.js`. ([#2](https://github.com/camsaul/lein-instant-cheatsheet/issues/2))
*  `2.1.3` (April 1st, 2015)
   *  Fix issue where [instant-cheatsheet](https://github.com/camsaul/lein-instant-cheatsheet) would fail to launch
      in projects that did not have a `:main` class defined
*  `2.1.1` (March 31st, 2015)
   *  Fix overflowing left-hand symbols list
   *  Add custom source reader that works in cases `clojure.repl/source-fn` does not
   *  Strip docstrings from source
*  `2.1.0` (March 30th, 2015)
   *  Automatically reload documentation from files that have changed since last API call
*  `2.0.0` (March 13th, 2015)
   *  Rewrite [instant-cheatsheet](https://github.com/camsaul/lein-instant-cheatsheet) as a Leiningen plugin
*  `1.5.0` (Feb 4th, 2015)
   * Rework [Instant Clojure Cheatsheet](https://github.com/camsaul/lein-instant-cheatsheet) to use Angular instead of jQuery
*  `1.0.0` (April 5th, 2013)
   *  Initial release of standalone, static [Instant Clojure Cheatsheet](https://github.com/camsaul/lein-instant-cheatsheet)


#### About ####

Instant Cheatsheet is inspired by other excellent Clojure cheatsheets like [this one][1].

Instant Cheatsheet was built with [Hiccup][3], [Bootstrap][4], and [AngularJS][6].

[1]: http://jafingerhut.github.io/cheatsheet/clojuredocs/cheatsheet-tiptip-no-cdocs-summary.html
[2]: https://github.com/camsaul/lein-instant-cheatsheet
[3]: https://github.com/weavejester/hiccup
[4]: http://twitter.github.io/bootstrap/
[5]: http://gdeer81.github.io/marginalia/
[6]: https://angularjs.org
[8]: http://clojuredocs.org/
