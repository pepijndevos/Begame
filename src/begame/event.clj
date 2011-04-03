(ns begame.event)

(def pressed (ref #{}))
(def mouse (ref {:x nil :y nil}))

(deftype
  watcher []
  java.awt.event.KeyListener
  (keyTyped [_ _])
  (keyPressed [_ evt]
    (dosync (alter pressed conj (.getKeyCode evt))))
  (keyReleased [_ evt]
    (dosync (alter pressed disj (.getKeyCode evt))))
  java.awt.event.MouseListener
  (mouseClicked [_ _])
  (mouseEntered [_ _])
  (mouseExited [_ _])
  (mousePressed [_ evt]
    (dosync
      (alter mouse assoc :x (.getX evt) :y (.getY evt))
      (alter pressed conj (.getButton evt))))
  (mouseReleased [_ evt]
    (dosync
      (alter mouse assoc :x (.getX evt) :y (.getY evt))
      (alter pressed disj (.getButton evt)))))

(defn watch [canvas]
  (let [w (watcher.)]
    (doto canvas
      (.addKeyListener w)
      (.addMouseListener w))))
