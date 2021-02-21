(ns market-model.market-price
  (:require [clojure.core.reducers :as r]
            [clojure.repl :as repl]
            [market-model.core-test :as t]))

(def funcs (read-string (slurp "ext/trees.edn")))

(defn derive-market-price
  "Runs regression models over input. Takes a filepath of the location of trees.edn"
  [path [data-set]]
  (r/fold + (r/map (fn [f] (f data-set)))))




(time (doall  (pmap (fn [args] (r/fold + (r/map (fn [f] (apply (eval f) args))
                                                (read-string (slurp "ext/trees.edn") ))))
                    (pmap (fn [coll] (into [] coll)) t/inputs))))

(into [] (r/map (fn [f] (apply (eval f) (into [] (first t/inputs))))
                (read-string (str "[" (slurp "ext/trees.edn") "]"))))

(time (r/fold + (r/map (fn [f] (apply (eval f) (into [] (first t/inputs))))
                       (read-string (slurp "ext/trees.edn") ))))

(time (reduce + (map (fn [x] (* 2 x)) (into [] (range 100000)))))
(time (reduce +
              (map
               (fn [f] (apply (eval f) (into [] (first t/inputs))))
               (read-string (slurp "ext/trees.edn")))))
(time (r/reduce +
                          (pmap
                           (fn [f] (apply (eval f)  (first t/inputs)))
                           funcs)))
(time (read-string (slurp "ext/trees.edn")))

(time (doall (pmap
         (fn [f] (apply (eval f)  (first t/inputs)))
         (read-string (slurp "ext/trees.edn")))))

(def func (first (read-string (str "[" (slurp "ext/trees.edn") "]"))))
(class (fn [x] (+ x x)))
(eval func)

(apply (first (read-string (str "[" (slurp "ext/trees.edn") "]"))) (first t/inputs))
