(ns begame.event)

(def pressed
  "Contains all currently presse keys"
  (ref #{}))

(def mouse
  "Contains the state of the mouse"
  (ref {:x nil, :y nil, :buttons #{}}))

(def codes (into {}
             (for [field
                   (filter
                     #(.startsWith (.getName %) "VK_")
                     (.getFields java.awt.event.KeyEvent))]
               [(-> (.getName field)
                  (.substring 3)
                  (.toLowerCase)
                  (.replace \_ \-)
                  (keyword))
                (.getInt field nil)])))

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
      (alter mouse assoc
             :x (.getX evt)
             :y (.getY evt)
             :buttons (conj (get @mouse :buttons) (.getButton evt)))))
  (mouseReleased [_ evt]
    (dosync
      (alter mouse assoc
             :x (.getX evt)
             :y (.getY evt)
             :buttons (disj (get @mouse :buttons) (.getButton evt))))))

(defn watch
  "Watch given canvas for keyboard and mouse events.
  Mouse movement is not tracked."
  [^java.awt.Canvas canvas]
  (let [w (watcher.)]
    (doto canvas
      (.addKeyListener w)
      (.addMouseListener w))))
