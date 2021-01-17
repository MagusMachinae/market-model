(ns market-model.tree-parser
  (:require [libpython-clj.require :refer [require-python]]
            [libpython-clj.python :as py :refer [as-python
                                                 as-jvm
                                                 ->python
                                                 ->jvm
                                                 get-attr
                                                 call-attr
                                                 get-item
                                                 initialize!]]))
(initialize!)
(require-python '[pandas :as pan])
(require-python '[numpy :as np])
(require-python '[sklearn.datasets])
(require-python '[sklearn.ensemble])
(require-python '[sklearn.model_selection :as model-selection])
(require-python '[sklearn.inspection])
(require-python '[sklearn.metrics])
(require-python '[sklearn.tree :as skltree])
;(require-python '[])

(defn fit-model-and-predict [x y x-held-out y-held-out])

(defn get-feature-names
  "Returns a coll of feature names from the decision tree."
  [tree]
  (py/get-attr tree :feature))

(defn get-feature-name [node]
  (py/get-item ))

(defn get-threshold
  "Gets threshold at node in tree."
  [node tree]
  (py/get-item (py/get-attr tree :threshold) node))

(defn get-children-left
  "Gets left-child of node in tree."
  [node tree]
  (py/get-item (py/get-attr tree :children_left) node))

(defn get-children-right
  "Gets right-child of node in tree."
  [node tree]
  (py/get-item (py/get-attr tree :children_right) node))

(defn get-tree-feature
  "Gets feature at node in tree."
  [node tree]
  (py/get-item (py/get-attr tree :feature) node))

(defn walk-tree
  "Walks over the tree, constructing the if statement representing the decision tree"
  [node tree feature-names]
  (let [name (nth node feature-names)
        threshold (get-threshold node tree)]
    (if (not= (get-tree-feature node tree)
              (py/get-attr skltree/_tree :TREE_UNDEFINED))
      `(if (~'<= ~name ~threshold)
         ~(walk-tree (get-children-left node tree)
                    tree
                    feature-names)
         ~(walk-tree (get-children-right node tree)
                    tree
                    feature-names))
      (py/get-item (py/get-attr tree :value) node))))

(defn decision-tree->s-exps
  "Converts a decision tree into a clojure function by recursing over its nodes."
  [tree feature-names]
  (let [tree (py/get-attr tree :_tree)]
    '(defn (name-tree ) ~feature-names
       ~(walk-tree 0 tree feature-names))))

(for [x (range (py/get-item (py/get-attr model :estimators_) 0))
      y (py/get-item (py/get-attr model:estimators_) 0)
      :let [tree (py/get-item (py/get-attr model :estimators_) x y)]]
  (decision-tree->s-exps tree feature-names))

(defn generate-trees! [src]
  "Builds trees.clj"
  (spit "src/trees.clj"
        (str '(ns trees) "\n\n"
         `(~'defn ~'tree-0 ~'[foo bar baz]
            ~(gen-if 1 9)))))

(defn gen-if [name threshold]
  `(if (~'<= ~name ~threshold)
     ~(+ 1 0)
     ~(+ 1 1)))
(gen-if 1 9)
