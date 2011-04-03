(ns begame.collision
  (:import java.awt.Rectangle)
  (:require [clojure.set :as s])
  (:refer-clojure :exclude [contains?]))

(defn rectangle [obj]
  (Rectangle. (:x obj) (:y obj) (:width obj) (:height obj)))

(defn centre [{:keys [x y width height]}]
  [(+ x (/ width 2)) (+ y (/ height 2))])

(defn contains? [obj1 obj2]
  (.conatins (rectangle obj1) (rectangle obj2)))
  
(defn intersects? [obj1 obj2]
  (.intersects (rectangle obj1) (rectangle obj2)))

(defn collisions [state]
  (for [rights (take-while
                 (complement nil?)
                 (iterate
                   next
                   (sort-by :x state)))
        :let [[f & r] rights]
        candidate (take-while #(> (+ (:x f) (:width f)) (:x %)) r)
        :when (intersects? f candidate)]
    [f candidate]))

(defn collision-map [state]
  (let [col (collisions state)]
    (into (s/map-invert col) col)))

