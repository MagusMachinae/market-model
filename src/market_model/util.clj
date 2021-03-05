(ns market-model.util
  (:require [tick.alpha.api :as tick]
            [clojure.math.numeric-tower :as math]
            [clojure.pprint :refer [cl-format]]))

(defn truncate
  "Converts value to exponential notation and rounds it to number of decimal
places specified by precision."
  [val precision]
  (cl-format nil (str "~," precision "E") val))


(defn time-quotient
  [date-1 date-2 scale]
  (cond (= scale :days)   (tick/divide
                            (tick/duration (tick/new-interval date-1 date-2))
                            (tick/new-duration 1 scale))
        (= scale :months) (float (/ (time-quotient date-1 date-2 :days) (/ 365.24 12)))
        (= scale :years)  (float (/ (time-quotient date-1 date-2 :days) 365.24))))

(defn time-since
  [date-1 date-2 options]
  (let [date-1 (tick/date date-1)
        date-2 (tick/date date-2)]
    (cond (false? (tick/< [date-1 date-2])) (- (time-since date-2
                                                           date-1
                                                           options))
          (true? (:abs options)) (math/abs (time-since date-1
                                                       date-2
                                                       (update
                                                         options
                                                         :abs
                                                         (fn [_] false))))
      :else (time-quotient date-1 date-2 (:time-scale options)))))

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

(comment (furthest-value-from 2 5 -10 -2 -50 65)
         (map (fn [x] (math/abs (- x 2))) '(5 -10))
         (apply > (map (fn [x] (math/abs (- x 2))) '(5 -10))))
