(ns begame.collision
  (:use begame.object)
  (:import java.awt.Rectangle)
  (:require [clojure.set :as s])
  (:refer-clojure :exclude [contains?]))

(defn centre [rect]
  (java.awt.Point.
    (+ (.getX rect)
       (/ (.getWidth rect) 2))
    (+ (.getY rect)
       (/ (.getHeight rec) 2))))

(defn contains? [obj1 obj2]
  (.conatins (rectangle obj1) (rectangle obj2)))
  
(defn intersects? [obj1 obj2]
  (.intersects (rectangle obj1) (rectangle obj2)))

(defn collisions [frame]
  (for [rights (->> (filter (comp (partial instance? begame.object.solid) val) frame)
                    (map (juxt key rectangle))
                    (sort-by #(.getX %))
                    (iterate next)
                    (take-while (complement nil?)))]))

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

