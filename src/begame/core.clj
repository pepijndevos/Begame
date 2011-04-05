(ns begame.core
  (:use [begame
         object
         animate
         event
         util])
  (:import [javax.swing JFrame WindowConstants]
           [java.awt Canvas Graphics2D Color])
  (:require [clojure.set :as s]))

(def state
  "The game state."
  (ref {}))

(defn canvas
  "Get a fresh JFrame with a Canvas in it.
  Has repaint disabled and a bufferStrategy set"
  [w h]
  (let [can (Canvas.)]
    (doto (new JFrame)
      (.setDefaultCloseOperation WindowConstants/EXIT_ON_CLOSE)
      ;(.setUndecorated true)
      (.setIgnoreRepaint true)
      (.add can)
      (.setSize w h)
      (.setVisible true))
    (doto can
      (.setIgnoreRepaint true)
      (.createBufferStrategy 2))))

(defn draw-frame
  "Iterate over the object in this frame
  and call paint on the visible ones"
  [frame g can]
  (doseq [obj (->> (vals frame)
                   (filter #(extends? visible (class %)))
                   (sort-by priority))]
    (paint obj g can)))

(defn draw-loop
  "Draws frames from logic and paints them to can"
  [^Canvas can logic]
  (let [strategy (.getBufferStrategy can)]
    (loop [frame logic]
      (do-while (.contentsLost strategy)
        (do-while (.contentsRestored strategy)
          (let [g (.getDrawGraphics strategy)]
            (draw-frame (first frame) g can)
            (.dispose g)))
        (.show strategy))
      (recur (rest frame)))))

(defn iteration
  "Compute the next frame
  by calling act on all actors"
  [frame]
  (into {}
    (for [[id obj] frame
          :when (not (nil? obj))]
      (if (extends? actor (class obj))
        [id (act obj id frame)]
        [id obj]))))

(defn logic-loop
  "An inifnit lazy seq of frame iterations"
  []
  (cons (dosync (alter state iteration))
        (lazy-seq (logic-loop))))

(defn game
  "Create a new game loop and window
  of the specified size"
  [w h]
  (let [can (canvas w h)]
    (watch can)
    (->> (logic-loop)
      ;(seque 10)
      ;(trickle)
      (animate)
      (draw-loop can))))

