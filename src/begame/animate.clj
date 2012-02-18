(ns begame.animate
  (:use [begame util object]))

(defn now
  "The current time in miliseconds"
  []
  (System/currentTimeMillis))

(defmulti transition
  "Time-based transition.
  Return the current value
  between from and to over ms
  staring at start"
  #(type %4))

(defmethod transition :default [start ms from to] to)

(defmethod transition java.lang.Number
  [start ms from to]
  (let [ms (max 1 ms)
        step (/ (- to from) ms)
        mi (min from to)
        ma (max from to)]
    (-> (now)
      (- start)
      (* step)
      (+ from)
      (min ma)
      (max mi))))

(defmethod transition java.awt.geom.AffineTransform
  [start ms from to]
  (let [fmatrix (double-array 6)
        tmatrix (double-array 6)]
    (.getMatrix from fmatrix)
    (.getMatrix to tmatrix)
    (java.awt.geom.AffineTransform.
      (amap fmatrix idx ret
        (transition start ms (aget fmatrix idx) (aget tmatrix idx))))))

(defmethod transition clojure.lang.IPersistentMap
  [start ms from to]
  (into {}
    (zipmap (keys to)
            (map #(transition start ms %1 %2)
                 (vals from) (vals to)))))

(defn trickle
  "Release one frame every duration"
  [duration slow]
  (map #(do (Thread/sleep duration) %) slow))

(defn animate*
  "Generate a transition frame between from and to
  from start during ms"
  [start ms [from to]]
  (transition start ms from to))    

(defn animate
  "Generate transition frames between
  logic frames laying duration appart"
  [duration frames]
  (let [start (now)]
    (lazy-cat
      (take-while (fn [_] (< (now) (+ start duration)))
                  (repeatedly #(animate* start duration frames)))
      (animate duration (rest frames)))))
