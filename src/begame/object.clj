(ns begame.object)

(defprotocol actor
  "A protocol for objects that do stuff"
  (act [this id world]
    "Gets called in the logic loop.
    Return the new state for the world.
    Do not update other actors."))

(defprotocol visible
  "A protocol for objects that need to display themselves"
  (paint [this graphic canvas]
    "Gets called in the paint loop.
    Paint object to grapics,
    using canvas as a possible ImageObserver")
  (layer [this]
    "The layer of this object.
    Objects with a lower layer get drawn first"))

(defprotocol solid
  "Objects that collide with the world"
  (rectangle [this]
    "Return a Rectangle representing
    the bouding box of this object"))

(defn wrap-entry [f this & args]
  (apply f (val this) args))

(extend clojure.lang.MapEntry
  actor
  {:act (partial wrap-entry act)}
  visible
  {:paint (partial wrap-entry paint)
   :layer (partial wrap-entry layer)}
  solid
  {:rectangle (partial wrap-entry rectangle)})
