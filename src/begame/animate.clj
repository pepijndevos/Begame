(ns begame.animate
  (:refer-clojure :exclude [future future-call])
  (:use [begame
         util
         [schedule]
         [core :only [state]]]))

(def animations (java.util.concurrent.LinkedBlockingQueue.))

(defn now [] (System/currentTimeMillis))

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

(defn animation [obj ms trs]
  (let [now (now)
        trs (for [[k to] trs] [k (transition now ms (k obj) to)])]
  (.put animations [obj trs (+ now ms)])))

(defn syquel [q]
  (let [[_ _ end :as i] (.take q)]
    (when (< (now) end)
      (.put q i))
    i))

(defn process []
  (let [[obj anns end] (.take animations)
        nobj (into obj
                   (map (juxt first
                              (comp int deref peek))
                        anns))]
    (when (< (now) end)
      (.put animations [nobj anns end])
      (send state rejoin obj nobj))))

(defn run []
  (println "running")
  (let [fut (future
              (process)
              (Thread/yield)
              (recur))]
    #(cancel fut)))
