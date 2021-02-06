(ns market-model.core-test
  (:require [clojure.test :refer :all]
            ;[market-model.market-price :refer :all]
            [clojure-csv.core :as csv]))

(def test-battery (map rest (csv/parse-csv (slurp "ext/validate_output.csv"))))
(def feature-names (take 13 (first test-battery)))
(def inputs (rest (map (fn [x] (take 13 x)) test-battery)))
(def expected-results (map last (rest test-battery)))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
