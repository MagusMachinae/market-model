(ns market-model.utils
  (:require [tick.alpha.api :as tick]
            [clojure.math.numeric-tower :as math]))

(defn time-since
  [])

(defn furthest-value-from
  ([fixed-point val-a val-b]
   (if (apply > (map (fn [x] (math/abs (- x fixed-point))) '(val-a val-b)))
     val-a
     val-b))
  ([fixed-point val-a val-b & values]
   (loop [arg-1 val-a
          arg-2 val-b
          stack values]
     (if (next stack)
       (recur (furthest-value-from fixed-point arg-1 arg-2)
              (first stack)
              (rest stack))
       (furthest-value-from fixed-point arg-1 arg-2)))))
