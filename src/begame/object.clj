(ns begame.object
  (:use begame.util)
  (:require [clojure.java.io :as io]))

(defmulti act (fn [obj world] (type obj)))

(defmethod act :default [obj _] obj)

(defrecord game-object [id x y height width sprite]
  clojure.lang.IMapEntry
  (key [_] id)
  (val [this] this)
  (getKey [this] (.key this))
  (getValue [this] (.val this)))

(defn object
  ([x y file]
   (object (keyword (gensym)) x y file))
  ([id x y file]
    (let [sprite (javax.imageio.ImageIO/read (io/input-stream file))
          ;sprite (printable-image sprite) ; debug
          [height width] (dimension sprite)]
      (object id x y height width sprite)))
  ([id x y height width sprite]
   (game-object. id x y height width sprite)))

(defn extended-object [obj typ mp]
  (with-meta (into obj mp) {:type typ}))
