(ns instant-cheatsheet.sources
  "Static data relating to namespaces and symbols."
  (:require [clojure.string :as str]
            [instant-cheatsheet.util :as util]))

(def ^:private namespaces (atom []))

(defn- ns->symbol-name+info
  "Return seq of symbols + info like [symb-name {info}] for NMSPACE."
  [nmspace]
  (let [ns-name-str (str (ns-name nmspace))]
    (for [[symb varr] (ns-publics nmspace)]
      (let [symb-name (name symb)]
        [symb-name (assoc (util/symbol->metadata (symbol ns-name-str symb-name))
                          :namespace ns-name-str)]))))

(def all-symbols
  "Dict of {symb-name -> [{info}+]}"
  (atom {}))

(defn- reset-all-symbols! []
  (reset! all-symbols (into {} (for [[k valls] (group-by first (mapcat ns->symbol-name+info @namespaces))]
                                 {k (map second valls)})) ))

(def all-symbols-keys
  "Sorted vector of all string names of all symbols."
  (atom []))

(defn- reset-all-symbols-keys! []
  (reset! all-symbols-keys (vec (sort (keys @all-symbols)))))

(defn set-namespaces! [ns-list]
  (reset! namespaces ns-list)
  (reset-all-symbols!)
  (reset-all-symbols-keys!))
