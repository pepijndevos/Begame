(ns begame.core
  (:use [begame
         object
         animate
         event
         util])
  (:import [javax.swing JFrame WindowConstants]
           [java.awt Canvas Graphics2D Color])
  (:require [clojure.set :as s]
            begame.collision))

; Sorted for collisions detection
(def additions (ref []))

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

(defn draw [g frame pane]
  (doto g
    (.setColor Color/BLACK)
    (.fillRect 0 0 (.getWidth pane) (.getHeight pane)))
  (doseq [obj frame]
    (try
      (.drawImage g (:sprite obj) (:x obj) (:y obj) (:width obj) (:height obj) pane)
      (catch Exception e (println obj)))))

(defn draw-loop [can logic]
  (let [strategy (.getBufferStrategy can)]
    (loop [frame logic]
      (do-while (.contentsLost strategy)
        (do-while (.contentsRestored strategy)
          (let [g (.getDrawGraphics strategy)]
            (draw g (first frame) can)
            (.dispose g)))
        (.show strategy))
      (recur (rest frame)))))

(defn logic-loop [frame]
  (cons frame
    (lazy-seq
      (logic-loop
        (dosync
          (let [ad @additions]
            (alter additions empty)
            (keep identity
                  (concat
                    (map #(act % frame) frame)
                    ad))))))))

(defn game [w h board]
  (let [can (canvas w h)]
    (watch can)
    (->> (logic-loop board)
      ;(seque 10)
      ;(trickle)
      (animate)
      (draw-loop can))))

