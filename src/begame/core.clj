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

(def ^:dynamic *frame-duration* 100)

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
  (Thread/sleep 10) ;kill
  (doto g
    (.setColor Color/BLACK)
    (.fillRect 0 0 (.getWidth pane) (.getHeight pane)))
  (doseq [obj frame]
    (try
      (.drawImage g (:sprite obj) (real (:x obj)) (real (:y obj)) pane)
      (catch Exception e (println obj)))))

(defn fast-loop [slow can]
  (let [strategy (.getBufferStrategy can)]
    (loop [slow slow]
      (do-while (.contentsLost strategy)
        (do-while (.contentsRestored strategy)
          (let [g (.getDrawGraphics strategy)]
            (draw g (first slow) can)
            (.dispose g)))
        (.show strategy))
      (if (< (now)
             (+ *frame-duration*
                (:timestamp (meta (first slow)))))
        (recur slow)
        (recur (rest slow))))))

(defn slow-loop [frame]
  (cons frame
    (lazy-seq
      (slow-loop
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
    (-> (slow-loop board)
      (animate)
      (fast-loop can))))

