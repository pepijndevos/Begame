(ns begame.object
  (:use begame.util)
  (:require [clojure.java.io :as io]))

(defmulti act type)

(defmethod act :default [_])

(defmulti react (fn [obj evtype event]
                  [(type obj) evtype]))

(defmethod react :default [_ _ _])

(defrecord game-object [id x y height width sprite])

(defn object [id x y file]
  (let [sprite (javax.imageio.ImageIO/read (io/input-stream file))
        ;sprite (printable-image sprite) ; debug
        [height width] (dimension sprite)]
    (game-object. id x y height width sprite)))
