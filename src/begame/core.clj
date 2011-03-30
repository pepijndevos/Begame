(ns begame.core
  (:use [begame
         [schedule :only [schedule]]])
  (:import [javax.swing JFrame JPanel WindowConstants]
           [java.awt Graphics2D Color])
  (:require [clojure.set :as s]))

(def width 500)
(def height 500)

; Sorted for collisions detection
(def state (ref (sorted-set-by #(compare [(:x %1) (:y %1) (:id %1)] [(:x %2) (:y %2) (:id %2)]))))

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
  (doto g
    (.setColor (Color. (rand-int 0xffffff)))
    (.fillRect 0 0 width height))
  (doseq [obj @state]
    (.drawImage g (:sprite obj) (:x obj) (:y obj) pane)))

(def pane (canvas paint-component))
(def manager (javax.swing.RepaintManager/currentManager pane))

(add-watch state :state-change
  (fn [_ _ old new]
    (doseq [obj (s/difference
                  (s/union old new)
                  (s/intersection old new))]
      (.addDirtyRegion
        manager
        pane
        (:x obj)
        (:y obj)
        (:width obj)
        (:height obj)))))
