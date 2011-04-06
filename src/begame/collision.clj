(ns begame.collision
  (:use [begame util object])
  (:import java.awt.Rectangle)
  (:require [clojure.set :as s])
  (:refer-clojure :exclude [contains?]))

(defn centre
  "Returns a Point representing
  the centre of a Rectangle"
  [^java.awt.Rectangle rect]
  (java.awt.Point.
    (+ (.getX rect)
       (/ (.getWidth rect) 2))
    (+ (.getY rect)
       (/ (.getHeight rect) 2))))

(defn collisions
  "Returns a seq of sets.
  Every set contains two keys of objects
  that have intersecting Rectangles."
  [frame]
  (for [rights (->> (filter (partial val-extends? solid) frame)
                    (map (juxt val rectangle))
                    (sort-by #(.getX ^java.awt.Rectangle (peek %)))
                    (iterate next)
                    (take-while (complement nil?)))
        :let [[[obj ^java.awt.Rectangle rect] & r] rights]
        [cval ^java.awt.Rectangle crect] (take-while
                                           #(> (+ (.getX rect) (.getWidth rect))
                                               (.getX ^java.awt.Rectangle (peek %)))
                                           r)
        :when (.intersects rect crect)]
    #{obj cval}))

