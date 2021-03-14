(ns market-model.market-price
  (:require [clojure.core.reducers :as r]
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

(comment (time (derived-market-price model (first mm-util/inputs)))

         ;;;difference between py-predict and clj-predictions

         (mm-util/mean-absolute-error (pmap (partial derived-market-price model) (pmap (fn [coll] (into [] coll)) mm-util/inputs))
             mm-util/expected-results)
         (def deviations-1 '(0.001 -0.1 -0.009 0.186 -0.308 0.099 -0.035 0.051 -0.003 0.005 -0.002 -0.062 -0.001 -0.003 0.001 -0.891 -0.001 -0.094 -0.022 0.052)))
