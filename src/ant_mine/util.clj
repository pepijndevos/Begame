(ns ant-mine.util)

(def dimension (juxt (memfn getHeight) (memfn getWidth)))

(defn printable-image [sprite]
  (let [sprite (proxy [java.awt.image.BufferedImage]
                 [(.getWidth sprite) (.getHeight sprite) (.getType sprite)]
                 (toString [] "image"))]
    (-> sprite
      (.getRaster)
      (.setRect (.getData sprite)))
    sprite))

(defn supdate [s o f & args]
  (conj (disj s o) (apply f o args)))

(defn rejoin [s o n]
  (conj (disj s o) n))
