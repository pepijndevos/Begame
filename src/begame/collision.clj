(ns begame.collision
  (:use [begame
         object
         util
         [schedule :only [schedule]]
         [core :only [state]]])
  (:refer-clojure :exclude [contains?]))

(defn contains?
  [{y1 :y height :height} y2]
  (<= y1 y2 (+ y1 height)))

(defn overlaps? [{y1 :y height1 :height :as bb1}
                 {y2 :y height2 :height :as bb2}]
  (let [by1 (+ y1 height1)
        by2 (+ y2 height2)]
    (or (contains? bb1 y2)
        (contains? bb1 by2)
        (contains? bb2 y1)
        (contains? bb2 by1))))

(defn collisions []
  (for [rights (take-while (complement nil?) (iterate next (seq @state)))
        :let [[f & r] rights]
        candidate (take-while #(> (+ (:x f) (:width f)) (:x %)) r)
        :when (overlaps? f candidate)]
    [f candidate]))

(defn notify [colls]
  (doseq [[l r] colls]
    (do
      (react l :collision r)
      (react r :collision l))))

(defn run [timeout]
  (schedule timeout (comp notify collisions)))
