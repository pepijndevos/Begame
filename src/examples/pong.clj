(ns examples.pong
  (:use [begame core object collision event util]))

(def images {:ball (sprite "src/examples/ball.png")
             :bat (sprite "src/examples/paddle_red.png")
             :bot (sprite "src/examples/paddle_blue.png")})

(def score (java.util.concurrent.atomic.AtomicInteger. 0))

(defrecord bat [y]
  actor
  (act [_ id world]
    (cond
      (clojure.core/contains? @pressed 38) (update-in world [id :y] - 15)
      (clojure.core/contains? @pressed 40) (update-in world [id :y] + 15)
      :else world))
  visible
  (paint [ball g can] (.drawImage ^java.awt.Graphics g (:bat images) 760 y can))
  (layer [_] 1)
  solid
  (rectangle [ball] (java.awt.Rectangle. 760 y 40 120)))

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
    (println obj)
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
          (= paddle bot) (assoc-in world [id :xdelt] 20)
          (= paddle bat) (assoc-in world [id :xdelt] -20)
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

(dosync
  (alter state assoc
         :field field
         :ball (ball. 380 230 20 20)
         :bat (bat. 160)
         :bot (bot. 160)))

(game 800 500)
