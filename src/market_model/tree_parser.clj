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
(require-python 'os)
(require-python '[model :as m])
(os/getcwd)
(os/chdir "/home/magusmachinae/Documents/Programming/market-model/src/market_model")

(defn fit-model-and-predict [x y x-held-out y-held-out])

(defn get-feature-names
  "Returns a coll of feature names from the data set."
  [tree]
  (py/get-attr tree :feature_names))

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
    '(defn ~'decision-tree ~feature-names
       ~(walk-tree 0 tree feature-names))))

(defn model->clj [model feature-names]
  "loops over estimators to build up the decision tree, then converts it into s-exps"
  (for [x (range (py/get-item (py/get-attr (py/get-attr model :estimators_) :shape) 0))
        y (py/get-item (py/get-attr (py/get-attr model :estimators_) :shape) 0)
        :let [tree (py/get-item (py/get-attr model :estimators_) x y)]]
    (decision-tree->s-exps tree feature-names)))

(defn generate-trees! [src]
  "Builds trees.clj from a source-file"
  (spit "src/trees.clj"
    (str '(ns trees) "\n\n"
          `(~'defn ~'decision-tree ~feature-names
            ~(model->clj model feature-names)))))

(spit "src/trees.clj"
      (str '(ns trees) "\n\n"
           `(~'defn ~'tree-0 ~'[foo bar baz]
              ~(gen-if 1 'foo))))

(defn gen-if [name threshold]
  `(if (~'<= ~name ~threshold)
     ~(+ 1 0)
     ~(+ 1 1)))
(gen-if 1 9)
