(ns begame.util
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
