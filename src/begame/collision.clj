(ns begame.collision
  (:use begame.object)
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
  (for [rights (->> (filter (comp (partial instance? begame.object.solid) val) frame)
                    (map (juxt key (comp rectangle val)))
                    (sort-by #(.getX ^java.awt.Rectangle (peek %)))
                    (iterate next)
                    (take-while (complement nil?)))
        :let [[[k ^java.awt.Rectangle rect] & r] rights]
        [ckey ^java.awt.Rectangle crect] (take-while
                                           #(> (+ (.getX rect) (.getWidth rect))
                                               (.getX ^java.awt.Rectangle (peek %)))
                                           r)
        :when (.intersects rect crect)]
    #{k ckey}))

