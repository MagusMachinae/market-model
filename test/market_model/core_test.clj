(ns market-model.core-test
  (:require [clojure.test :refer :all]
            [market-model.market-price :refer :all]
            [market-model.util :as mm-util]
            [clojure-csv.core :as csv]))

(def test-battery (map rest (csv/parse-csv (slurp "ext/validate_output.csv"))))
(def feature-names (map symbol (take 13 (first test-battery))))
(def inputs (rest (map (fn [x] (map read-string (take 13 x))) test-battery)))
(def expected-results (map (partial mm-util/truncate-sf 4) (mapv (comp read-string last) (rest test-battery))))

(deftest a-test
  (testing "Validate generated function against battery of results from python code."
    (is (= expected-results
           (map (fn [x] (apply derived-market-price x)) inputs)))))
(map (fn [x] (apply + x)) inputs)
