(ns begame.core
  (:use [begame
         object
         animate
         event
         util])
  (:import [javax.swing JFrame WindowConstants]
           [java.awt Canvas Graphics2D Color])
  (:require [clojure.set :as s]))

; Sorted for collisions detection
(def state (ref {}))

(defn canvas [w h]
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

(defn draw [frame g can]
  (doseq [obj (->> (vals frame)
                   (filter #(extends? visible (class %)))
                   (sort-by priority))]
    (paint obj g can)))

(defn draw-loop [can logic]
  (let [strategy (.getBufferStrategy can)]
    (loop [frame logic]
      (do-while (.contentsLost strategy)
        (do-while (.contentsRestored strategy)
          (let [g (.getDrawGraphics strategy)]
            (draw (first frame) g can)
            (.dispose g)))
        (.show strategy))
      (recur (rest frame)))))

(defn iteration [frame]
  (into {}
    (for [[id obj] frame
          :when (not (nil? obj))]
      (if (extends? actor (class obj))
        [id (act obj id frame)]
        [id obj]))))

(defn logic-loop []
  (cons (dosync (alter state iteration))
        (lazy-seq (logic-loop))))

(defn game [w h]
  (let [can (canvas w h)]
    (watch can)
    (->> (logic-loop)
      ;(seque 10)
      ;(trickle)
      (animate)
      (draw-loop can))))

