(ns begame.util
  (:use begame.object)
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

(defrecord duck [x y width height ^java.awt.Image sprite]
  actor
  (act [this _ _]
    (update-in
      (update-in this
                 [:y] (partial + (- (rand-int 10) 5)))
      [:x] (partial + (- (rand-int 10) 5))))
  visible
  (paint [this g can]
    (.drawImage ^java.awt.Graphics g sprite x y width height can))
  solid
  (rectangle [_]
    (java.awt.Rectangle. x y width height)))

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

(defn sprite
  "Read a BufferedImage from a file"
  [file]
  (javax.imageio.ImageIO/read (io/input-stream file)))

(defn align
  "Allign the keys of the input maps so that
  {:foo 1 :bar 2 :baz 3} {:foo 3 :baz 2 :boo 6} =>
  [[:foo 1 3] [:baz 2 3]]"
  [m1 m2]
  (map
    (juxt identity m1 m2)
    (s/intersection (set (keys m1)) (set (keys m2)))))
