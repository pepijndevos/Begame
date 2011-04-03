(ns begame.animate)

(defn now [] (System/currentTimeMillis))

(defn transition [start ms from to]
  (let [ms (max 1 ms)
        step (/ (- to from) ms)
        mi (min from to)
        ma (max from to)]
    (reify clojure.lang.IDeref
      (deref [_]
        (-> (now)
          (- start)
          (* step)
          (+ from)
          (min ma)
          (max mi)
          (int))))))

(defn animate* [[from to]]
  (let [start (:timestamp (meta to))
        ms (- start (:timestamp (meta from)))]
    (with-meta
      (map
        (fn mrg [o n]
          (merge-with
            #(if (and (number? %1) (not= %1 %2))
               (transition start ms %1 %2)
               %2)
            o n))
        from to)
      {:timestamp start})))

(defn animate [slow]
  (->> (map #(with-meta % {:timestamp (now)}) slow)
       (partition 2 1)
       (map animate*)))
