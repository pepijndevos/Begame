(ns begame.util
  (:require [clojure.java.io :as io]
            [clojure.set :as s]))

(defn printable-image
  "A BufferedImage with a shorter printed form"
  [^java.awt.image.BufferedImage sprite]
  (let [sprite (proxy [java.awt.image.BufferedImage]
                 [(.getWidth sprite) (.getHeight sprite) (.getType sprite)]
                 (toString [] "image"))]
    (-> sprite
      (.getRaster)
      (.setRect (.getData sprite)))
    sprite))

(defmacro do-while
  "Like while with the check at the end"
  [test & body]
  `(loop []
     ~@body
     (when ~test
       (recur))))

(defmacro memoizing
  "Memoize the given functions during the body"
  [fns & body]
  (let [mems (mapcat (fn [f] `(~f (memoize ~f))) fns)]
    `(binding [~@mems] ~@body)))

(defn align
  "Allign the keys of the input maps so that
  {:foo 1 :bar 2 :baz 3} {:foo 3 :baz 2 :boo 6} =>
  [[:foo 1 3] [:baz 2 3]]"
  [m1 m2]
  (map
    (juxt identity m1 m2)
    (s/intersection (set (keys m1)) (set (keys m2)))))

(defn uuid
  "Generate an unique identifier"
  []
  (keyword (gensym)))

(defn idseq [s]
  (into {} (map #(vector (uuid) %) s)))

(defn update-id
  "Update id in m to be the result of
  associating the key to the val,
  or the result of caling value with
  the current value"
  [m id & key-vals]
  (assoc m id
    (reduce conj (get m id)
      (for [[k v] (partition 2 key-vals)]
        [k (if (fn? v)
             (v (get-in m [id k]))
             v)]))))

(defn octopus
  "Merge 3 maps intelligently"
  [org mrg1 mrg2]
  (apply
    dissoc
    (into
      mrg1
      (s/difference (set mrg2) (set org)))
    (s/difference (set (keys org)) (set (keys mrg2)))))

(defn pipe []
  (let [q (java.util.concurrent.LinkedBlockingQueue.)
        EOQ (Object.)
        NIL (Object.)
        s (fn s [] (lazy-seq (let [x (.take q)]
                               (when-not (= EOQ x)
                                 (cons (when-not (= NIL x) x) (s))))))]
    [s (fn ([] (.put q EOQ)) ([x] (.put q (or x NIL))))]))
