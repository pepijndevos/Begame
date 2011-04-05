(ns examples.pong
  (:refer-clojure :exclude [contains?])
  (:use [begame core object collision event util]))

(def images {:ball (sprite "src/examples/ball.png")
             :bat (sprite "src/examples/paddle_red.png")
             :bot (sprite "src/examples/paddle_blue.png")})

(defrecord ball [x y xdelt ydelt]
  actor
  (act [ball _ field]
    (let [cen ^java.awt.Point (centre (rectangle ball))
          cx (.getX cen)
          cy (.getY cen)
          ball (cond
                 (not (< 0 cx 800)) (assoc ball :x 380 :y 230)
                 (not (< 0 cy 500)) (update-in ball [:ydelt] -)
                 (get (collision-map field) :ball) (update-in ball [:xdelt] -)
                 :else ball)]
        (assoc ball
          :x (+ (:x ball) (:xdelt ball))
          :y (+ (:y ball) (:ydelt ball)))))
  visible
  (paint [ball g can] (.drawImage ^java.awt.Graphics g (:ball images) x y can))
  (priority [_] 2)
  solid
  (rectangle [ball] (java.awt.Rectangle. x y 40 40)))

(defrecord bat [y]
  actor
  (act [bat _ _]
    (cond
      (clojure.core/contains? @pressed 38) (update-in bat [:y] - 15)
      (clojure.core/contains? @pressed 40) (update-in bat [:y] + 15)
      :else bat))
  visible
  (paint [ball g can] (.drawImage ^java.awt.Graphics g (:bat images) 760 y can))
  (priority [_] 1)
  solid
  (rectangle [ball] (java.awt.Rectangle. 760 y 40 120)))

(defrecord bot [y]
  actor
  (act [bot _ field]
    (let [ball (:ball field)]
      (assoc bot :y (- (:y ball) 40))))
  visible
  (paint [ball g can] (.drawImage ^java.awt.Graphics g (:bot images) 0 y can))
  (priority [_] 1)
  solid
  (rectangle [ball] (java.awt.Rectangle. 0 y 40 120)))

(def field (reify
             visible
             (paint [_ g _]
                    (.setColor ^java.awt.Graphics g java.awt.Color/BLACK)
                    (.fillRect ^java.awt.Graphics g 0 0 800 500))
             (priority [_] 0)))

(dosync
  (alter state assoc
         :field field
         :ball (ball. 380 230 20 20)
         :bat (bat. 160)
         :bot (bot. 160)))

(game 800 500)
