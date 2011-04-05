(ns begame.object)

(defprotocol actor
  (act [this id world]))

(defprotocol visible
  (paint [this graphic canvas])
  (priority [this]))

(defprotocol solid
  (rectangle [this]))

(defn wrap-entry [f this & args]
  (clojure.lang.MapEntry.
    (key this)
    (apply f (val this) args)))

(extend clojure.lang.MapEntry
  actor
  {:act (partial wrap-entry act)}
  visible
  {:paint (partial wrap-entry paint)}
  solid
  {:rectangle (partial wrap-entry rectangle)})
