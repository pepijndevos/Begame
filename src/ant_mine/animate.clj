(ns ant-mine.animate
  (:refer-clojure :exclude [future])
  (:use [ant-mine
         [schedule :only [future]]
         [core :only [state]]]))

(def animations (java.util.concurrent.LinkedBlockingQueue.))

(defn now [] (System/currentTimeMillis))
                  now 1000 300  0
(defn transition [start ms from to]
  (let [step (/ (- to from) ms)
        mi (min from to)
        ma (max from to)]
    (reify clojure.lang.IDeref
      (deref [_]
        (-> (now)
          (- start)
          (* step)
          (+ from)
          (min ma)
          (max mi))))))

(defn animation [k ms trs]
  (let [now (now)
        trs (for [[k tr] trs] [k (apply transition now ms tr)])]
  (.put animations [k trs (+ now ms)])))

(defn syquel [q]
  (let [[_ _ end :as i] (.take q)]
    (when (< (now) end)
      (.put q i))
    i))

(defn process []
  (let [[k anns] (syquel animations)
        anns (map (juxt first (comp int deref peek)) anns)
        assoc* (fn [m k kvs]
                 (assoc m k
                        (apply assoc
                               (get m k)
                               (apply concat kvs))))]
    (send state assoc* k anns)))

(defn run []
  (println "running")
  (future
    (process)
    (Thread/yield)
    (recur)))
