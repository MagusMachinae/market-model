(ns market-model.market-price
  (:require [clojure.core.reducers :as r]
            [market-model.core-test :as t]
            [market-model.util :as mm-util]))

(def funcs (read-string (slurp "ext/trees.edn")))

(defn model-vector
  "Returns a vector representing the model contained in a file"
  [path]
  (read-string (slurp path)))

(def model (model-vector "ext/trees.edn"))

(defn derived-market-price
  "Runs regression models over input. Takes a filepath of the location of trees.edn"
  [[[learning-rate raw-prediction] & trees] data-set]
  (mm-util/truncate-sf
    5
    (+ raw-prediction
      (* learning-rate
         (r/fold + (pmap (fn [f] (apply (eval f) data-set))
                         trees))))))

(time (derived-market-price model (first t/inputs)))
(r/fold +
        (r/map (fn [f] (apply (eval f) (first t/inputs)))
         funcs))

;;;difference between py-predict and clj-predictions
(map - (pmap (partial derived-market-price model) (pmap (fn [coll] (into [] coll)) t/inputs))
     t/expected-results)
