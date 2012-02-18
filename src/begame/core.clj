(ns begame.core
  (:use [begame
         object
         animate
         event
         util])
  (:import [javax.swing JFrame WindowConstants]
           [java.awt Canvas Graphics2D Color])
  (:require [clojure.set :as s]))

(defn canvas
  "Get a fresh JFrame with a Canvas in it.
  Has repaint disabled and a bufferStrategy set"
  [w h]
  (let [can (Canvas.)]
    (doto (new JFrame)
      (.setDefaultCloseOperation WindowConstants/DISPOSE_ON_CLOSE)
      ;(.setUndecorated true)
      (.setIgnoreRepaint true)
      (.add can)
      (.setSize w h)
      (.setVisible true))
    (doto can
      (.setIgnoreRepaint true)
      (.createBufferStrategy 2))))

(defn draw-frame
  "Iterate over the objects in this frame
  and call paint on the visible ones"
  [frame g can]
  (doseq [[_ obj] (sort-by :order frame)]
    (.drawImage
      ^java.awt.Graphics g
      (:sprite obj)
      (:trans obj)
      can)))

(defn draw-loop
  "Draws frames and paints them to can"
  [^Canvas can frames]
  (let [strategy (.getBufferStrategy can)]
    (loop [frame frames]
      (do-while (.contentsLost strategy)
        (do-while (.contentsRestored strategy)
          (let [g (.getDrawGraphics strategy)]
            (draw-frame (first frame) g can)
            (.dispose g)))
        (.show strategy))
      (recur (next frame)))))

(defn game
  "Create a new game loop and window
  of the specified size"
  [w h frames & {:keys [transition fix queue]}]
  (let [can (canvas w h)]
    (watch can)
    (->> frames
      ((if queue (partial seque queue) identity))
      ((if fix (partial trickle fix) identity))
      ((if transition (partial animate transition) identity))
      (draw-loop can))))

