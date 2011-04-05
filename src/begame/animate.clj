(ns begame.animate
  (:use [begame util object]))

(def ^:dynamic *frame-duration* 100)

(defn now [] (System/currentTimeMillis))

(defn transition [start ms from to]
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

(defn trickle [slow]
  (map #(do (Thread/sleep *frame-duration*) %) slow))

(defn animate* [[from to] start]
  (into {}
    (for [[id o n] (align from to)]
      (if (extends? actor (class n))
        [id (merge-with
              #(if (and (number? %1) (not= %1 %2))
                 (transition start *frame-duration* %1 %2)
                 %2)
              o n)]
        [id n]))))

(defn animate [frames]
  (let [start (now)]
    (lazy-cat
      (take-while (fn [_] (< (now) (+ start *frame-duration*)))
                  (repeatedly #(animate* frames start)))
      (animate (rest frames)))))
