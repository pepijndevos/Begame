(ns begame.animate
  (:refer-clojure :exclude [future future-call])
  (:use [begame
         util
         [schedule]
         [core :only [state]]]))

(def animations (ref (list)))
(def kill-ring (ref #{}))

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
  (dosync (alter animations conj [obj trs (+ now ms)]))))

(defn stop [id]
  (dosync
    (alter kill-ring conj id)))

(defn alive? [id]
  (if (contains? @kill-ring id)
    (do
      (alter kill-ring disj id)
      false)
    true))

(defn current? [[{id :id} _ end]]
  (and (< (now) end)
       (alive? id)))

(defn process [[obj anns end]]
  (let [nobj (into obj
                   (map (juxt first
                              (comp int deref peek))
                        anns))]
    (alter state rejoin obj nobj)
    [nobj anns end]))

(defn sweep [anims]
  (when (seq @anims)
    (dosync
      (alter anims (partial filter current?))
      (alter anims (comp doall (partial map process))))))

(defn run [timeout]
  (schedule timeout sweep animations))
