(ns market-model.util
  (:require [tick.alpha.api :as tick]
            [clojure.math.numeric-tower :as math]
            [clojure.pprint :refer [cl-format]]
            [clojure-csv.core :as csv]))

(defn truncate-sf
  "Converts value to exponential notation and rounds it to number of decimal
places specified by precision."
  [precision val]
  (read-string (cl-format nil (str "~," (dec precision) "E") val)))

(defn truncate-float
  "Truncates decimal places."
  [precision val]
  (read-string (cl-format nil (str "~," precision "F") val)))

(defn time-quotient
  "Divides a time-duration by the appropriate scaling factor to return the
  (approximate for months and years) length of an interval of time."
  [date-1 date-2 scale]
  (cond (= scale :days)   (tick/divide
                            (tick/duration (tick/new-interval date-1 date-2))
                            (tick/new-duration 1 scale))
        (= scale :months) (->> (/ 365.24 12)
                               (/ (time-quotient date-1 date-2 :days) )
                               (truncate-float 2)
                               (read-string))
        (= scale :years)  (->> 365.24
                               (/ (time-quotient date-1 date-2 :days) )
                               (truncate-float 2)
                               (read-string))))

(defn time-since
  "Calculates the time between two dates. Takes a hash-map of options as its
  3rd argument. The hash-map has one mandatory key, :time-scale which can take
  one of three keyword-values; :day, :month, and :year. The optional key, :abs
  takes true or false and is used evaluate the absolute value of the interval."
  [date-1 date-2 options]
  (let [date-1 (tick/date date-1)
        date-2 (tick/date date-2)]
    (cond (false? (tick/< date-1 date-2)) (- (time-since date-2
                                                         date-1
                                                         options))
          (true? (:abs options)) (math/abs (time-since date-1
                                                       date-2
                                                       (update
                                                         options
                                                         :abs
                                                         (fn [_] false))))
      :else (time-quotient date-1 date-2 (:time-scale options)))))

(defn furthest-value-from
  "Returns the element of (rest args) that is furthest from the first value it
 receives as an argument."
  ([fixed-point val-a val-b]
   (if (apply > (map (fn [val] (math/abs (- val fixed-point))) [val-a val-b]))
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

(defn mean-absolute-error
  "Calculates mean absolute error between two datasets."
  [coll1 coll2]
  (let [n (if (<= (count coll1)
                 (count coll2))
            (count coll1)
            (count coll2))]
    (/ (reduce + (map (comp math/abs -) coll1 coll2)) n)))

(def test-battery (map rest (csv/parse-csv (slurp "ext/validate_output.csv"))))
(def feature-names (map symbol (take 13 (first test-battery))))
(def inputs (rest (map (fn [x] (map read-string (take 13 x))) test-battery)))
(def expected-results (map (partial truncate-sf 5) (mapv (comp read-string last) (rest test-battery))))

(comment (time-since "2000-08-01" "2021-01-01" {:abs false :time-scale :months})
         (furthest-value-from 2 5 -10 -2 -50 65)
         (map (fn [x] (math/abs (- x 2))) '(5 -10))
         (apply > (map (fn [x] (math/abs (- x 2))) '(5 -10))))
