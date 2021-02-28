(ns market-model.utils
  (:require [tick.alpha.api :as tick]
            [clojure.math.numeric-tower :as math]))

(defn time-since)

(defn furthest-value-from
  [val-a val-b fixed-point]
  (if (apply > (map (fn [x] (math/abs (- x fixed-point))) '(val-a val-b)))
    val-a
    val-b))
