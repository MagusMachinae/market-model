(ns market-model.tree-parser
  (:require [libpython-clj.require :refer [require-python]]
            [libpython-clj.python :as py :refer [initialize!
                                                 get-attr
                                                 call-attr
                                                 get-item]]
            [clojure.string :as str]
            [market-model.util :as mm-util]))

(initialize!)

(require-python '[sklearn.datasets :as ds]
                '[pickle :as pick]
                '[io :as py-io]
                '[builtins :as python]
                '[sklearn.tree :as skltree]
                '[inspect :as gadget])

(defn features-from-file
  "Creates a vector of feature names from a string representing a file path."
  [path]
  (mapv symbol (map str/lower-case (str/split-lines (slurp path)))))

(def boston (ds/load_boston))

(defn un-pickle
  "Takes a string of the relative location of the python file containing the
model and returns the python object stored in pickle."
  [file]
  (pick/load (py-io/open file "rb")))

(un-pickle "ext/gbm_model.pickle")

(defn get-feature-names
  "Returns a collection of feature names from a python object (presumably a
dataset) as Clojure symbols. Intended for interop use to get collection of
variable names used to build named nodes in regression tree."
  [data-set]
  (map symbol (map str/lower-case (py/get-attr data-set :feature_names))))

(defn get-threshold
  "Gets threshold at node in regression tree."
  [node tree]
  (py/get-item (py/get-attr tree :threshold) node))

(defn get-children-left
  "Gets left-child of node in regression tree."
  [node tree]
  (python/int (py/get-item (py/get-attr tree :children_left) node)))

(defn get-children-right
  "Gets right-child of node in regression tree."
  [node tree]
  (python/int (py/get-item (py/get-attr tree :children_right) node)))

(defn get-tree-feature
  "Gets feature (scalar value representing either the name of the variable being
  checked, or the node type) at node in regression tree."
  [node tree]
  (python/int (py/get-item (py/get-attr tree :feature) node)))

(defn get-node-value
  "Called when a tree-feature in walk-tree returns a negative number to
grab the numpy array at that leaf and extract the float stored in it."
  [node tree]
  (first (first (py/get-item (py/get-attr tree :value) node))))

(defn get-raw-predict
  "Retrieves constant value of raw-predict from dummy regressor."
  [model]
  (first (first (py/get-attr (py/get-attr model "init_") "constant_"))))

(defn get-learning-rate
  "Retrieves learning rate from model"
  [model]
  (py/get-attr model "learning_rate"))

(defn walk-tree
  "Walks over the decision tree, constructing the if statement representing the
 node in the decision tree.Every node is checked for whether the value is
undefined (ie. the value of the node is -2). If it is, the value is returned for
 that node. Otherwise, walk-tree is recursively called."
  [node tree feature-names precision]
  (if (not= (get-tree-feature node tree)
            -2)
      (let [name (nth feature-names (get-tree-feature node tree))
            threshold  (mm-util/truncate-sf precision (get-threshold node tree))]
        `(if (~'<= ~name ~threshold)
            ~(walk-tree (get-children-left node tree)
                        tree
                        feature-names
                        precision)
            ~(walk-tree (get-children-right node tree)
                        tree
                        feature-names
                        precision)))
      (mm-util/truncate-sf precision (get-node-value node tree))))

(defn decision-tree->s-exps
  "Converts a decision tree into a clojure function definition by recursing over its nodes."
  [tree feature-names precision]
  (let [tree (py/get-attr tree :tree_)]
    `(~'fn [~@feature-names]
       ~(walk-tree 0 tree feature-names precision))))

(defn model->clj
  "loops over estimators to build up the decision tree, then converts it into Clojure symbolic-expressions"
  [model feature-names precision]
  (for [x (range (py/get-item (py/get-attr (py/get-attr model :estimators_) :shape) 0))
        y (range (py/get-item (py/get-attr (py/get-attr model :estimators_) :shape) 1))
        :let [tree (py/get-item (py/get-attr model :estimators_) [x y])]]
    (decision-tree->s-exps tree feature-names precision)))

(defn into-file-trees!
  [model feature-names path precision]
  (spit path (->> (model->clj model feature-names (dec precision))
                  (cons [(get-learning-rate model)
                         (mm-util/truncate-sf 5 (get-raw-predict model))])
                  (interpose "\n\n")
                  (apply str)
                  ((fn [data-string] (str "[" data-string "]"))))))

(comment
 (into-file-trees! (un-pickle "ext/gbm_model.pickle") (get-feature-names boston) "trees.edn" 3)
 (python/help (py/$..   skltree/_tree :Tree)))
(mm-util/truncate-sf 5 (get-raw-predict (un-pickle "ext/gbm_model.pickle")))

(python/help (py/get-attr (un-pickle "ext/gbm_model.pickle") :estimators_))
(py/att-type-map (un-pickle "ext/gbm_model.pickle"))
(py/call-attr (un-pickle "ext/gbm_model.pickle") "_raw_predict_init" [[0.006,19.873,2.374,0.0,0.528,6.449,61.017,3.991,1.0,225.757,13.848,439.029,4.861]])

(py/get-attr (un-pickle "ext/gbm_model.pickle") "init_"
 (py/att-type-map  (py/get-attr (un-pickle "ext/gbm_model.pickle") "init_")))
(py/get-attr (py/get-attr (un-pickle "ext/gbm_model.pickle") "init_") "constant_"

 (py/get-attr (un-pickle "ext/gbm_model.pickle") "learning_rate")
 (python/help (py/get-attr (un-pickle "ext/gbm_model.pickle") "init_"))
 (py/call-attr (py/get-attr (un-pickle "ext/gbm_model.pickle") "init_") "predict" [[0.00,9.873,2.374,0.0,0.528,6.449,61.017, 0.991,1.0,25.757,1.848,43.029,4.861]])

 (def trees (for [x (range (py/get-item (py/get-attr (py/get-attr (un-pickle "ext/gbm_model.pickle") :estimators_) :shape) 0))
                  y (range  (py/get-item (py/get-attr (py/get-attr (un-pickle "ext/gbm_model.pickle") :estimators_) :shape) 1))
                  :let  [tree (py/get-item (py/get-attr (un-pickle "ext/gbm_model.pickle") :estimators_) [x y])]]
              (decision-tree->s-exps tree (get-feature-names (ds/load_boston)) 2)))


 (def tree0 (first (for [x (range (py/get-item (py/get-attr (py/get-attr (un-pickle "ext/gbm_model.pickle") :estimators_) :shape) 0))
                         y (range  (py/get-item (py/get-attr (py/get-attr (un-pickle "ext/gbm_model.pickle") :estimators_) :shape) 1))
                         :let  [tree (py/get-item (py/get-attr (un-pickle "ext/gbm_model.pickle") :estimators_) [x y])]]
                      (py/get-attr tree :tree_))))
 (python/help tree0)
 (py/att-type-map tree0)


 (def tree0-features (map (fn [x] (get-tree-feature x tree0)) (range 214)))


 ((fn [x] (if
            (= x -2) (py/get-item (py/get-attr (py/get-attr tree :tree_) :value) 1))
    (walk-tree x (py/get-attr tree :tree_) (get-feature-names boston)))
  nil)
 (map (fn [x] (get-tree-feature x tree0)) (range 213))

 (let [tree (mapv (fn [x] (py/get-item (py/get-attr (un-pickle "ext/gbm_model.pickle") :estimators_) [x 0])) (range 500))]
   (mapv (fn [x]  (get-tree-feature 0 x)) (mapv #(py/get-attr % :tree_) tree)))

 (defn gen-if [name threshold]
   `(if (~'<= ~name ~threshold)
      ~(+ 1 0)
      (+ 1 1)))

 (spit "src/trees.clj"
       (str '(ns trees) "\n\n"
            `(~'defn ~'tree-0 ~'[foo bar baz]
               ~(gen-if 1 'foo))))

 (gen-if 'foo 2)

 `('+ 2 2))
