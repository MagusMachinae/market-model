(ns market-model.tree-parser
  (:require [libpython-clj.require :refer [require-python]]
            [libpython-clj.python :as py :refer [as-python
                                                 as-jvm
                                                 ->python
                                                 ->jvm
                                                 get-attr
                                                 call-attr]]))

(require-python '[pandas :as pan])
(require-python '[numpy :as np])
(require-python '[sklearn.datasets])
(require-python '[sklearn.ensemble])
(require-python '[sklearn.model_selection :as model-selection])
(require-python '[sklearn.inspection])
(require-python '[sklearn.metrics])
(require-python '[sklearn.tree])
(require-python '[])

(defn get-feature-names [tree]
  "Returns a coll of feature names from the decision tree."
  (get-attr tree :feature))

(defn walk-tree [node depth]
  "Walks over the tree, constructing the if statemen representing the decision tree"
  (let [name ()
        threshold ()]
    (if (not= (<= name threshold))
      '(if '(<= name threshold)
         (walk-tree node (inc depth))
         (walk-tree node (inc depth)))
      (py/get-item (py/get-attr tree :value) node))))

(defn decision-tree->s-exps
  "Converts a decision tree into a clojure function by recursing over its nodes."
  [tree ]
  (let [tree (py/get-attr tree :_tree)]
    '(defn (name-tree )
       )))
