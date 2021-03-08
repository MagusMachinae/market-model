(ns market-model.market-price
  (:require [clojure.core.reducers :as r]
            [market-model.core-test :as t]
            [market-model.util :as mm-util]))

(def funcs (read-string (slurp "ext/trees.edn")))

(defn model-vector
  "Returns a vector representing the model contained in a file"
  [path]
  (read-string (slurp path)))

(def model (model-vector "trees.edn"))

  "Runs regression models over input. Takes a filepath of the location of trees.edn"
  [path data-set]
  (r/fold + (r/map (fn [f] (apply (eval f) datum)))))




(time (doall  (pmap (fn [args] (+ 22.58793103 (* 0.1 (r/fold +
                                                             (r/map (fn [f] (apply (eval f) args)
                                                                      )
                                                                    funcs)))))
                    (pmap (fn [coll] (into [] coll)) t/inputs))))

(into [] (r/map (fn [f] (apply (eval f) (into [] (first t/inputs))))
                (read-string  (slurp "ext/trees.edn"))))

(time (+ 22.58793103 (* 0.1 (r/fold + (r/map (fn [f] (apply (eval f) (into [] (first t/inputs))))
                                             (read-string (slurp "ext/trees.edn")))))))

(time (reduce + (map (fn [x] (* 2 x)) (into [] (range 100000)))))
(time (reduce +
              (map (fn [x] (* 0.1 x)) )(map
                                        (fn [f] (apply (eval f) (into [] (first t/inputs))))
                                        (read-string (slurp "trees.edn")))))
(time (/ (r/reduce +
                   (pmap
                    (fn [f] (apply (eval f)  (first t/inputs)))
                    funcs))
         500))
(time (read-string (slurp "ext/trees.edn")))

(time (doall (pmap
         (fn [f] (apply (eval f)  (first t/inputs)))
         (read-string (slurp "ext/trees.edn")))))

(apply (first (read-string (slurp "ext/trees.edn"))) (first t/inputs))
