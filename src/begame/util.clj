(ns begame.util)

(def dimension (juxt (memfn getHeight) (memfn getWidth)))

(defn printable-image [sprite]
  (let [sprite (proxy [java.awt.image.BufferedImage]
                 [(.getWidth sprite) (.getHeight sprite) (.getType sprite)]
                 (toString [] "image"))]
    (-> sprite
      (.getRaster)
      (.setRect (.getData sprite)))
    sprite))

(defn rejoin [s o n]
  (conj (disj s o) n))

(defn supdate [s o f & args]
  (rejoin s o (apply f o args)))

(defmacro do-while [test & body]
  `(loop []
     ~@body
     (when ~test
       (recur))))

(defn real [d]
  (if (instance? clojure.lang.IDeref d)
    @d
    d))
