(ns market-model.core-test
  (:require [clojure.test :refer :all]
            ;[market-model.market-price :refer :all]
            [clojure-csv.core :as csv]))

(def test-battery (map rest (csv/parse-csv (slurp "ext/validate_output.csv"))))
(def feature-names (map symbol (take 13 (first test-battery))))
(def inputs (rest (map (fn [x] (map read-string (take 13 x))) test-battery)))
(def expected-results (map read-string (map last (rest test-battery))))

(comment (deftest a-test
           (testing "Validate generated function against battery of results from python code."
             (is (= expected-results
                    (map (fn [x] (apply derive-market-price x)) inputs)))))
         (map (fn [x] (apply + x)) inputs))
