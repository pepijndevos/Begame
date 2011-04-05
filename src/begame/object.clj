(ns begame.object)

(defprotocol actor
  (act [this id world]))

(defprotocol visible
  (paint [this graphic canvas]))

(defprotocol solid
  (rectangle [this]))
