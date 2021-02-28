(ns market-model.utils
  (:require [tick.alpha.api :as tick]))

(defn time-since)

(defn furthest-value-from
  ([val-a val-b]
   (if (> val-a val-b)
     val-a
     val-b))
  ([val-a val-b offset]
    (if (apply > (map (fn [x] (- x offset)) '(val-a val-b)))
      val-a
      val-b)))
