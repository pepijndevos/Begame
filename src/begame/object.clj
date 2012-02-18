(ns begame.object
  (:require [clojure.java.io :as io]))

(defrecord thing [sprite trans order])

(defn sprite
  "Read a BufferedImage from a file"
  [file]
  (javax.imageio.ImageIO/read (io/input-stream file)))

(defn rect [{:keys [sprite trans]}]
  (.getBounds
    (.createTransformedShape
      trans
      (java.awt.Rectangle.
        0 0
        (.getWidth sprite) (.getHeight sprite)))))

(defn make-thing
  ([path x y] (make-thing path x y 0))
  ([path x y order]
   (->thing (sprite path)
            (java.awt.geom.AffineTransform/getTranslateInstance x y)
            order)))
