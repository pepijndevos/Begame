(ns ant-mine.schedule
  (:refer-clojure :exclude [future future-call])
  (:import [java.util.concurrent TimeUnit ScheduledThreadPoolExecutor]))

(def runner (ScheduledThreadPoolExecutor. (.availableProcessors (Runtime/getRuntime))))

(defn schedule
  ([interval f]
   (.scheduleWithFixedDelay runner f 0 interval TimeUnit/MILLISECONDS))
  ([interval f & args]
   (schedule interval #(apply f args))))

(defn cancel [job]
  (.cancel job false))

(defn future-call [f]
  (let [fut (.submit runner ^Callable f)]
    (reify 
     clojure.lang.IDeref 
      (deref [_] (.get fut))
     java.util.concurrent.Future
      (get [_] (.get fut))
      (get [_ timeout unit] (.get fut timeout unit))
      (isCancelled [_] (.isCancelled fut))
      (isDone [_] (.isDone fut))
      (cancel [_ interrupt?] (.cancel fut interrupt?)))))

(defmacro future [& body]
  `(future-call (^{:once true} fn* [] ~@body)))

