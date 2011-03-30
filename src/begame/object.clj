(ns begame.object
  (:use begame.util)
  (:require [clojure.java.io :as io]))

(defprotocol
  actor
  (act [this world])
  (react [this world event]))

(defrecord game-object [id x y height width sprite])

(defn object [id x y file]
  (let [sprite (javax.imageio.ImageIO/read (io/input-stream file))
        ;sprite (printable-image sprite) ; debug
        [height width] (dimension sprite)]
    (game-object. id x y height width sprite)))
