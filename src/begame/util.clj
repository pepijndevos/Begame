(ns begame.util
  (:use begame.object)
  (:require [clojure.java.io :as io]))

(def dimension (juxt (memfn getHeight) (memfn getWidth)))

(defn printable-image [sprite]
  (let [sprite (proxy [java.awt.image.BufferedImage]
                 [(.getWidth sprite) (.getHeight sprite) (.getType sprite)]
                 (toString [] "image"))]
    (-> sprite
      (.getRaster)
      (.setRect (.getData sprite)))
    sprite))

(defrecord duck [x y width height sprite]
  actor
  (act [this _ _]
    (update-in
      (update-in this
                 [:y] (partial + (- (rand-int 10) 5)))
      [:x] (partial + (- (rand-int 10) 5))))
  visible
  (paint [this g can]
    (.drawImage g sprite x y width height can))
  solid
  (rectangle [_]
    (java.awt.Rectangle. x y width height)))

(defmacro do-while [test & body]
  `(loop []
     ~@body
     (when ~test
       (recur))))

(defmacro memoizing [fns & body]
  (let [mems (mapcat (fn [f] `(~f (memoize ~f))) fns)]
    `(binding [~@mems] ~@body)))

(defn sprite [file]
  (javax.imageio.ImageIO/read (io/input-stream file)))
