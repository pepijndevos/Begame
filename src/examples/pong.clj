(ns examples.pong
  (:use [begame core object collision event util]))

(def images {:ball (sprite "src/examples/ball.png")
             :bat (sprite "src/examples/paddle_red.png")
             :bot (sprite "src/examples/paddle_blue.png")})

(def score (java.util.concurrent.atomic.AtomicInteger. 0))

(defrecord bat [x y img up down]
  actor
  (act [_ id world]
    (cond
      (clojure.core/contains? @pressed up) (update-in world [id :y] - 15)
      (clojure.core/contains? @pressed down) (update-in world [id :y] + 15)
      :else world))
  visible
  (paint [ball g can] (.drawImage ^java.awt.Graphics g (img images) x y can))
  (layer [_] 1)
  solid
  (rectangle [ball] (java.awt.Rectangle. x y 40 120)))

(defrecord bot [y]
  actor
  (act [_ id field]
    (let [ball (:ball field)]
      (assoc-in field [id :y] (- (:y ball) 40))))
  visible
  (paint [ball g can] (.drawImage ^java.awt.Graphics g (:bot images) 0 y can))
  (layer [_] 1)
  solid
  (rectangle [ball] (java.awt.Rectangle. 0 y 40 120)))

(defn move [obj]
  (let [{:keys [x y xdelt ydelt]} obj]
    (assoc obj
           :x (+ x xdelt)
           :y (+ y ydelt))))

(defrecord ball [x y xdelt ydelt]
  actor
  (act [obj id world]
    (let [paddle (class (some #(when (contains? % obj) (first (disj % obj))) (collisions world)))]
      (update-in
        (cond
          (not (< 0 y 460)) (update-in world [id :ydelt] -)
          (> 0 x) (do (.incrementAndGet score)
                      (assoc world id (ball. 380 230 xdelt ydelt)))
          (< 760 x) (do (.decrementAndGet score)
                        (assoc world id (ball. 380 230 xdelt ydelt)))
          (or (= paddle bot)
              (= paddle bat)) (update-in world [id :xdelt] -)
          :else world)
        [id] move)))
  visible
  (paint [ball g can] (.drawImage ^java.awt.Graphics g (:ball images) x y can))
  (layer [_] 2)
  solid
  (rectangle [ball] (java.awt.Rectangle. x y 40 40)))

(def field (reify
             visible
             (paint [_ g _]
                    (.setColor ^java.awt.Graphics g java.awt.Color/BLACK)
                    (.fillRect ^java.awt.Graphics g 0 0 800 500))
             (layer [_] 0)))

(def controler (reify
                 visible
                 (paint [_ g _]
                        (.setColor ^java.awt.Graphics g java.awt.Color/WHITE)
                        (.setFont ^java.awt.Graphics g (java.awt.Font. "Monospaced" java.awt.Font/PLAIN 30))
                        (.drawString ^java.awt.Graphics g (str (.get score)) 400 50))
                 (layer [_] 5)))

(defrecord
  button [x y w h text field]
  actor
  (act [_ _ world]
    (if (and (contains? (:buttons @mouse) 1)
             (:loc @mouse)
             (.contains
               (java.awt.Rectangle. x y w h)
               (:loc @mouse)))
      field
      world))
  visible
  (paint [_ g _]
    (.setColor ^java.awt.Graphics g java.awt.Color/RED)
    (.fillRect ^java.awt.Graphics g x y w h)
    (.setColor ^java.awt.Graphics g java.awt.Color/WHITE)
    (.setFont ^java.awt.Graphics g (java.awt.Font. "Monospaced" java.awt.Font/PLAIN 30))
    (.drawString ^java.awt.Graphics g text (+ 50 x) (+ y 40)))
  (layer [_] 0))

(def single-player
  {:cnt controler
   :field field
   :ball (ball. 380 230 20 20)
   :bat (bat. 760 160 :bat :up :down)
   :bot (bot. 160)})

(def multi-player
  {:cnt controler
   :field field
   :ball (ball. 380 230 20 20)
   :bat (bat. 760 160 :bat :up :down)
   :bat2 (bat. 0 160 :bot :w :s)})

(def menu
  {:single (button. 200 100 400 60 "single player" single-player)
   :multi (button. 200 300 400 60 "multi player" multi-player)})

(defn -main []
  (game 800 500
        menu
        :transition 100))
