(ns market-model.utils
  (:require [tick.alpha.api :as tick]
            [clojure.math.numeric-tower :as math]))

(defn time-since
  [date options])

(tick/divide )
(tick/new-interval (tick/now) (tick/<< (tick/now) (tick/new-period 1 :years)))
(tick/year 2020)

(defn furthest-value-from
  ([fixed-point val-a val-b]
   (if (apply > (map (fn [x] (math/abs (- x fixed-point))) [val-a val-b]))
     val-a
     val-b))
  ([fixed-point val-a val-b & values]
   (loop [arg-1 val-a
          arg-2 val-b
          stack values]
     (if (seq stack)
       (recur (furthest-value-from fixed-point arg-1 arg-2)
              (first stack)
              (rest stack))
       (furthest-value-from fixed-point arg-1 arg-2)))))

(furthest-value-from 2 5 -10 -2 -100)
(seq '())
(if nil
  1
  2)
(map (fn [x] (math/abs (- x 2))) '(5 -10))
(apply > (map (fn [x] (math/abs (- x 2))) '(5 -10)))
