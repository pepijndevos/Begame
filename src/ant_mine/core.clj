(ns ant-mine.core
  (:import [javax.swing JFrame JPanel WindowConstants]
           [java.awt Graphics2D Color])
  (:require [clojure.java.io :as io]
            [clojure.set :as s]))

;(def counter (java.util.concurrent.atomic.AtomicInteger. 0))

(def width 500)
(def height 500)

(def state (agent {}))

(defrecord game-object [x y height width sprite])

(def dimension (juxt (memfn getHeight) (memfn getWidth)))

(defn printable-image [sprite]
  (let [sprite (proxy [java.awt.image.BufferedImage]
                 [(.getWidth sprite) (.getHeight sprite) (.getType sprite)]
                 (toString [] "image"))]
    (-> sprite
      (.getRaster)
      (.setRect (.getData sprite)))
    sprite))

(defn object [x y file]
  (let [sprite (javax.imageio.ImageIO/read (io/input-stream file))
        ;sprite (printable-image sprite) ; debug
        [height width] (dimension sprite)]
    (game-object. x y height width sprite)))

(defn game [w h panel]
  (doto (new JFrame)
    (.setDefaultCloseOperation WindowConstants/DISPOSE_ON_CLOSE)
    ;(.setUndecorated true)
    (.setContentPane panel)
    (.setSize w h)
    (.setVisible true)))

(defn canvas [f]
  (proxy [JPanel] []
    (paintComponent [g] (f g this))))

(defn paint-component [g pane]
  ;(println (.incrementAndGet counter))
  (doto g
    (.setColor (Color. (rand-int 0xffffff)))
    (.fillRect 0 0 width height))
  (doseq [obj @state
          :let [v (val obj)]]
    (.drawImage g (:sprite v) (:x v) (:y v) pane)))

(def pane (canvas paint-component))
(def manager (javax.swing.RepaintManager/currentManager pane))

(add-watch state :state-change
  (fn [_ _ old new]
    (doseq [pair (s/difference (set new) (set old))
            obj  [(val pair) (get old (key pair))]
            :when (not (nil? obj))]
      (.addDirtyRegion
        manager
        pane
        (:x obj)
        (:y obj)
        (:width obj)
        (:height obj)))))
