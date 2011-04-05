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
       (/ (.getHeight rect) 2))))

(defn contains? [obj1 obj2]
  (.conatins (rectangle obj1) (rectangle obj2)))
  
(defn intersects? [obj1 obj2]
  (.intersects (rectangle obj1) (rectangle obj2)))

(defn collisions [frame]
  (for [rights (->> (filter (comp (partial instance? begame.object.solid) val) frame)
                    (map (juxt key (comp rectangle val)))
                    (sort-by #(.getX (peek %)))
                    (iterate next)
                    (take-while (complement nil?)))
        :let [[[k rect] & r] rights]
        [ckey crect] (take-while #(> (+ (.getX rect) (.getWidth rect)) (.getX  (peek %))) r)
        :when (.intersects rect crect)]
    [k ckey]))

(defn collision-map [state]
  (let [col (collisions state)]
    (into (s/map-invert col) col)))

