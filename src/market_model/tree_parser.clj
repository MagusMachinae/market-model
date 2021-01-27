(ns market-model.tree-parser
  (:require [libpython-clj.require :refer [require-python]]
            [libpython-clj.python :as py :refer [as-python
                                                 as-jvm
                                                 ->python
                                                 ->jvm
                                                 get-attr
                                                 call-attr
                                                 get-item
                                                 initialize!
                                                 call-kw]]))
(initialize!)

(require-python '[pandas :as pan]
                '[numpy :as np]
                '[numpy :as np]
                '[sklearn.datasets :as ds]
                '[sklearn.ensemble]
                '[sklearn.model_selection :as model-selection]
                '[sklearn.inspection]
                '[sklearn.metrics])

(m/model)
(require-python 'os)
(np/add 1 2)
(os/getcwd)
(m/f)
(do (os/chdir "/home/magusmachinae/Documents/Programming/market-model/src/market_model")
  (require-python '[model :as mm])
  (os/chdir "/home/magusmachinae/Documents/Programming/market-model"))

(def boston (ds/load_boston))

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
(call-)
(fit)
(def boston-model-prediction (modelo/fit-model-and-predict
                                      (drop  100 (py/get-attr boston :data))
                                      (drop  100 (py/get-attr boston :target))
                                      (take 100 (py/get-attr boston :data))
                                      (take 100 (py/get-attr boston :target))
                                      (get-feature-names boston)
                                      {:max_depth 9 :n_estimators 500 :subsample 0.5}))

(model->clj mm/model (as-jvm (get-feature-names boston)))

(defn walk-tree
  "Walks over the tree, constructing the if statement representing the decision tree"
  [node tree feature-names]
  (let [name (nth feature-names node)
        threshold (get-threshold node tree)]
    (if (not= (get-tree-feature node tree)
              -2)
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
  (let [tree (py/get-attr tree :tree_)]
    `(defn ~'decision-tree ~@feature-names
       ~(walk-tree 0 tree feature-names))))

(defn model->clj
  "loops over estimators to build up the decision tree, then converts it into s-exps"
  [model feature-names]
  (for [x (range (py/get-item (py/get-attr (py/get-attr model :estimators_) :shape) 0))
        y (range (py/get-item (py/get-attr (py/get-attr model :estimators_) :shape) 1))
        :let [tree (py/get-item (py/get-attr model :estimators_) [x y])]]
    (decision-tree->s-exps tree feature-names)))

(defn generate-trees! [model feature-names]
  "Builds trees.clj from a source-file"
  (spit "src/trees.clj"
    (str '(ns trees) "\n\n"
          `(~'defn ~'decision-tree ~`feature-names
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
