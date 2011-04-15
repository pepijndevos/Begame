(ns examples.platform
  (:use [begame core object collision event util]))

(defn random-color []
  (java.awt.Color. (rand-int 0xffffff)))

(def width 800)
(def height 800)
(def ch-spr {:standing (sprite "src/examples/char.gif")
             :walking (sprite "src/examples/char_walk.gif")})

(defrecord block [x y width height color]
  visible
  (paint [_ g _]
    (.setColor g color)
    (.fillRect g x y width height))
  (layer [_] 1)
  solid
  (rectangle [_]
    (java.awt.Rectangle. x y width height)))

(def field (reify
             visible
             (paint [_ g _]
                    (.setColor ^java.awt.Graphics g java.awt.Color/BLACK)
                    (.fillRect ^java.awt.Graphics g 0 0 width height))
             (layer [_] 0)))

(defrecord character [x y velx vely]
  actor
  (act [ch id world]
    (let [velx (cond
                 (contains? @pressed :left)  (- velx 2)
                 (contains? @pressed :right) (+ velx 2)
                 :else velx)
          nch (character.
                (+ x velx)
                (+ y vely)
                (int (/ velx 1.2))
                (+ vely 3))
          nworld (assoc world id nch)]
      (if-let [col (collider nworld nch)]
        (assoc world id
               (assoc nch 
                      :y (if (< (+ y 30) (:y col))
                           (- (:y col) 30)
                           y)
                      :vely (if (contains? @pressed :up)
                              -20
                              0)))
        nworld)))
  visible
  (paint [ch g can]
    (.drawImage
      ^java.awt.Graphics g
      (if (= 0 velx)
        (:standing ch-spr)
        (:walking ch-spr))
      x y can))
  (layer [_] 2)
  solid
  (rectangle [_] (java.awt.Rectangle. x y 15 30)))

(def field
  (assoc
    (idseq
      (repeatedly
        100
        #(block. (rand-int width)
                 (rand-int height)
                 (rand-int 100)
                 16
                 (random-color))))
    :ch (character. 400 0 0 0)
    :bg field))

(game width height field :transition 100)
