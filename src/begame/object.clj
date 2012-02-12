(ns begame.object
  (:require [clojure.java.io :as io]))

(defrecord thing [sprite rect trans])

(defn sprite
  "Read a BufferedImage from a file"
  [file]
  (javax.imageio.ImageIO/read (io/input-stream file)))

(defn rect [x y w h]
  (java.awt.Rectangle. x y w h))

(defn transform [x y & {:keys [scale scalex scaley rot], :or {scaley 1, scalex 1, scale 1, rot 0}}]
  (doto (java.awt.geom.AffineTransform.)
    (.translate x y)
    (.rotate rot)
    (.scale scale scale)
    (.scale scalex scaley)))

(defn make-thing [path x y & trans]
  (let [s (sprite path)
        r (rect x y (.getWidth s) (.getHeight s))
        t (apply transform x y trans)]
  (->thing s r t)))
