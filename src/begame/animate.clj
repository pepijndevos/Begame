(ns begame.animate
  (:use [begame util object]))

(defn now
  "The current time in miliseconds"
  []
  (System/currentTimeMillis))

(defn transition
  "Time-based transition.
  Return the current value
  between from and to over ms
  staring at start"
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
      (max mi)
      (int))))

(defn trickle
  "Release one frame every duration"
  [duration slow]
  (map #(do (Thread/sleep duration) %) slow))

(defn animate*
  "Generate a transition frame between from and to
  from start during duration"
  [duration [from to] start]
  (into {}
    (for [[id o n] (align from to)]
      (if (extends? actor (class n))
        [id (merge-with
              #(if (and (number? %1) (not= %1 %2))
                 (transition start duration %1 %2)
                 %2)
              o n)]
        [id n]))))

(defn animate
  "Generate transition frames between
  logic frames laying duration appart"
  [duration frames]
  (let [start (now)]
    (lazy-cat
      (take-while (fn [_] (< (now) (+ start duration)))
                  (repeatedly #(animate* duration frames start)))
      (animate duration (rest frames)))))
