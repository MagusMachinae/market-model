(ns market-model.core-test
  (:require [clojure.test :refer :all]
            [market-model.market-price :as mp]
            [market-model.util :as mm-util]))

(deftest
  error-test
  (testing "Validate generated function against battery of results from
            python code. Checks if mean-absolute-error is within tolerance."
    (is (> 0.1
           (->> mm-util/inputs
                (pmap (fn [coll] (into [] coll)))
                (pmap (partial mp/derived-market-price mp/model))
                (mm-util/mean-absolute-error mm-util/expected-results)
                (mm-util/truncate-sf 5))))))
