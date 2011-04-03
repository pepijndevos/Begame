(ns examples.pong
  (:refer-clojure :exclude [contains?])
  (:use [begame core object collision event]))

(def field
  [(extended-object (object :ball 380 230 "src/examples/ball.png")
                    :ball {:xdelt 20, :ydelt 20})
   (extended-object (object :bot 0 160 "src/examples/paddle_blue.png")
                    :bot {})
   (extended-object (object :bat 760 160 "src/examples/paddle_red.png")
                    :bat {})])

(defmethod act :ball [ball field]
  (let [[cx cy] (centre ball)
        ball (cond
               (not (< 0 cx 800)) (assoc ball :x 380 :y 230)
               (not (< 0 cy 500)) (update-in ball [:ydelt] -)
               (get (collision-map field) ball) (update-in ball [:xdelt] -)
               :else ball)]
      (assoc ball
        :x (+ (:x ball) (:xdelt ball))
        :y (+ (:y ball) (:ydelt ball)))))

(defmethod act :bat [bat _]
  (cond
    (clojure.core/contains? @pressed 38) (update-in bat [:y] - 15)
    (clojure.core/contains? @pressed 40) (update-in bat [:y] + 15)
    :else bat))

(defmethod act :bot [bot field]
  (let [index (into {} field)
        ball (:ball index)]
    (assoc bot :y (- (:y ball) 40))))
    
(game 800 500 field)
