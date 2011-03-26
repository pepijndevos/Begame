(ns ant-mine.schedule
  (:import [java.util.concurrent TimeUnit ScheduledThreadPoolExecutor]))

(def runner (ScheduledThreadPoolExecutor. (.availableProcessors (Runtime/getRuntime))))

(defn schedule
  ([interval f]
   (.scheduleWithFixedDelay runner f 0 interval TimeUnit/MILLISECONDS))
  ([interval f & args]
   (schedule interval #(apply f args))))

(defn cancel [job]
  (.cancel job true))
