(ns market-model.core-test
  (:require [clojure.test :refer :all]
            [market-model.market-price :as mp]
            [market-model.util :as mm-util]))

(deftest
  a-test
  (testing "Validate generated function against battery of results from python code."
    (is (= mm-util/expected-results
           (map (fn [x] (apply mp/derived-market-price x)) mm-util/inputs)))))
