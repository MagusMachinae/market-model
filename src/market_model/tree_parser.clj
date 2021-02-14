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
                                                 call-kw]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [tech.v3.datatype :as dtype]))

(initialize!)

(require-python '[pandas :as pan]
                '[numpy :as np]
                '[numpy :as np]
                '[sklearn.datasets :as ds]
                '[sklearn.ensemble]
                '[sklearn.model_selection :as model-selection]
                '[sklearn.inspection]
                '[sklearn.metrics]
                '[pickle :as pick]
                '[io :as py-io]
                '[builtins :as bi]
                '[sklearn.tree :as skltree])
(def tree0-features (map (fn [x] (get-tree-feature x tree0)) (range 214)))
(def tree0 (first (for [x (range (py/get-item (py/get-attr (py/get-attr mm/model :estimators_) :shape) 0))
                        y (range  (py/get-item (py/get-attr (py/get-attr mm/model :estimators_) :shape) 1))
                        :let  [tree (py/get-item (py/get-attr mm/model :estimators_) [x y])]]
                    (py/get-attr tree :tree_))))


(defn features-from-file
  "Creates a vector of feature names from a string representing a file path."
  [path]
  (mapv symbol (str/split-lines (slurp path))))

(require-python 'os)
(np/add [1 2 3] [2 2 2])
(os/getcwd)

(do (os/chdir "/home/magusmachinae/Documents/Programming/market-model/src/market_model")
  (require-python '[model :as mm])
  (os/chdir "/home/magusmachinae/Documents/Programming/market-model"))

(def boston (ds/load_boston))

(defn un-pickle
  "Takes a string of the relative location of the python file containing the model and returns the python object stored in pickle."
  [file]
  (pick/load (py-io/open file "rb")))

(un-pickle "ext/gbm_model.pickle")

(defn get-feature-names
  "Returns a collection of feature names from the data set as Clojure symbols.
  Intended for interop use to get collection of variable names used to build name nodes in regression tree."
  [tree]
  (map symbol (py/get-attr tree :feature_names)))

(defn get-threshold
  "Gets threshold at node in regression tree."
  [node tree]
  (py/get-item (py/get-attr tree :threshold) node))

(defn get-children-left
  "Gets left-child of node in regression tree."
  [node tree]
  (bi/int (py/get-item (py/get-attr tree :children_left) node)))

(defn get-children-right
  "Gets right-child of node in regression tree."
  [node tree]
  (bi/int (py/get-item (py/get-attr tree :children_right) node)))

(defn get-tree-feature
  "Gets feature (scalar value representing either the name of the variable being checked, or the node type) at node in regression tree."
  [node tree]
  (bi/int (py/get-item (py/get-attr tree :feature) node)))


(class (first (drop  100 (py/get-attr boston :target))))

(defn walk-tree
  "Walks over the tree, constructing the if statement representing the node in the decision tree.
  Every node is checked for whether the value is undefined (ie. the value of the node is -2). If it is,
  the value is returned for that node. Otherwise, walk-tree is recursively called."
  [node tree feature-names]
  (if (not= (get-tree-feature node tree)
            -2)
            (let [name (nth feature-names (bi/int (get-tree-feature node tree)))
                  threshold  (get-threshold node tree)]
              `(if (~'<= ~name ~threshold)
                  ~(walk-tree (get-children-left node tree)
                              tree
                              feature-names)
                  ~(walk-tree (get-children-right node tree)
                              tree
                              feature-names)))
    (py/get-item (py/get-attr tree :value) node)))

(defn decision-tree->s-exps
  "Converts a decision tree into a clojure function definition by recursing over its nodes."
  [tree feature-names]
  (let [tree (py/get-attr tree :tree_)]
    `(defn ~'decision-tree ~@feature-names
       ~(walk-tree 0 tree feature-names))))

(defn model->clj
  "loops over estimators to build up the decision tree, then converts it into Clojure symbolic-expressions"
  [model feature-names]
  (for [x (range (py/get-item (py/get-attr (py/get-attr model :estimators_) :shape) 0))
        y (range (py/get-item (py/get-attr (py/get-attr model :estimators_) :shape) 1))
        :let [tree (py/get-item (py/get-attr model :estimators_) [x y])]]
    (decision-tree->s-exps tree feature-names)))

(comment
 (model->clj (un-pickle "ext/gbm_model.pickle") (get-feature-names boston))
 (bi/help (py/$..   skltree/_tree :Tree))



 (for [x (range (py/get-item   (py/get-attr (py/get-attr mm/model :estimators_) :shape) 0))
                     y (range  (py/get-item (py/get-attr (py/get-attr mm/model :estimators_) :shape) 1))
                     :let  [tree (py/get-item (py/get-attr mm/model :estimators_) [x y])]]
                 ((fn [x] (if
                            (= x -2) (py/get-item (py/get-attr (py/get-attr tree :tree_) :value) 1))
                             (walk-tree x (py/get-attr tree :tree_) (get-feature-names boston)))
                  (bi/int (get-tree-feature 1 (py/get-attr tree :tree_)))))
                  ((fn [x] (if
                             (= x -2) (py/get-item (py/get-attr (py/get-attr tree :tree_) :value) 1))
                              (walk-tree x (py/get-attr tree :tree_) (get-feature-names boston)))
                   nil)
 (filter #(= -2 %))
 (py/->jvm )
 (bi/int (get-tree-feature 0 tree0))
 (get-children-left 0 tree0)
 (filter (fn [x] (= -1 x)) (map bi/int (flatten (first (for [x (range (py/get-item (py/get-attr (py/get-attr mm/model :estimators_) :shape) 0))
                                                             y (range  (py/get-item (py/get-attr (py/get-attr mm/model :estimators_) :shape) 1))
                                                             :let  [tree (py/get-item (py/get-attr mm/model :estimators_) [x y])]]
                                                         (map (fn [x] (get-tree-feature x (py/get-attr tree :tree_))) (range)))))))

 (range 2)
 (map (fn [x] (get-tree-feature x tree0)) (range 213))
 (py/get-attr tree0 :node_count)
(walk-tree 0 tree0 (get-feature-names boston))
 (model->clj mm/model (get-feature-names boston))

 (let [tree (mapv (fn [x] (py/get-item (py/get-attr mm/model :estimators_) [x 0])) (range 500))]
   (mapv (fn [x]  (get-tree-feature 0 x)) (mapv #(py/get-attr % :tree_) tree)))

 (walk-tree 0 (py/get-item (py/get-attr mm/model :estimators_) [0 0]))

 (defn generate-trees!
   "Builds trees.clj from a source-file"
   [model feature-names]
   (spit "src/trees.clj"
         (str '(ns trees) "\n\n"
              ~(model->clj model feature-names))))

 (defn gen-if [name threshold]
   `(if (~'<= ~name ~threshold)
      ~(+ 1 0)
      (+ 1 1)))

 (spit "src/trees.clj"
       (str '(ns trees) "\n\n"
            `(~'defn ~'tree-0 ~'[foo bar baz]
               ~(gen-if 1 'foo))))
 (symbol "foo")

 (gen-if 'foo 2)

 `('+ 2 2))
