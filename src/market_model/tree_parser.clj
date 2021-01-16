(ns market-model.tree-parser
  (:require [libpython-clj.require :refer [require-python]]
            [libpython-clj.python :as py :refer [as-python
                                                 as-jvm
                                                 ->python
                                                 ->jvm
                                                 get-attr
                                                 call-attr
                                                 get-item]]))

(require-python '[pandas :as pan])
(require-python '[numpy :as np])
(require-python '[sklearn.datasets])
(require-python '[sklearn.ensemble])
(require-python '[sklearn.model_selection :as model-selection])
(require-python '[sklearn.inspection])
(require-python '[sklearn.metrics])
(require-python '[sklearn.tree])
(require-python '[])

(defn fit-model-and-predict [x y x-held-out y-held-out])

(defn get-feature-names [tree]
  "Returns a coll of feature names from the decision tree."
  (py/get-attr tree :feature))

(defn get-feature-name [node]
  )

(defn get-threshold [node tree]
  (py/get-item (py/get-attr tree :threshold) node))

(defn get-children-left [node tree]
  (py/get-item (py/get-attr tree :children_left) node))

(defn get-children-right [node tree]
  (py/get-item (py/get-attr tree :children_right) node))

(defn walk-tree [node depth tree]
  "Walks over the tree, constructing the if statemen representing the decision tree"
  (let [name ()
        threshold (get-threshold node tree)]
    (if (not= (<= name threshold))
      '(if '(<= name threshold)
         (walk-tree (get-children-left node tree)
                    (inc depth)
                    tree)
         (walk-tree (get-children-right node tree)
                    (inc depth)
                    tree))
      (py/get-item (py/get-attr tree :value) node))))

(defn decision-tree->s-exps
  "Converts a decision tree into a clojure function by recursing over its nodes."
  [tree feature-names]
  (let [tree (py/get-attr tree :_tree)]
    '(defn (name-tree )
       (walk-tree ))))
