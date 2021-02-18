(ns market-model.market-price
  (:require [clojure.core.reducers :as r]
            [trees :as trees-ns]
            [clojure.repl :as repl]
            [market-model.core-test :as t]))


(defn derive-market-price
  "Runs regression models over input. Takes a filepath of the location of trees.edn"
  [path [data-set]]
  (r/fold + (r/map (fn [f] (f data-set)))))
(keys (ns-publics 'trees))



(time (r/fold + (r/map (fn [x] (* 2 x)) (into [] (range 100000)))))
(time (reduce + (map (fn [x] (* 2 x)) (into [] (range 100000)))))
(time (reduce + (map (fn [f] (apply (eval  f) (into [] (first t/inputs)))) (read-string (str "[" (slurp "trees.edn") "]")))))
(def func (first (read-string (str "[" (slurp "trees.edn") "]"))))
(class (fn [x] (+ x x)))
(eval func)
(apply (partial func) (first t/inputs))
(apply (first (read-string (str "[" (slurp "trees.edn") "]"))) (first t/inputs))

((fn [foo] (+ 2 foo)) 1 2 3 4)
