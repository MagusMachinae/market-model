(defproject market-model "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clojure-csv/clojure-csv "2.0.1"]
                 [clojupyter "0.3.2"]
                 [clj-python/libpython-clj "2.00-beta-12"]
                 [tick "0.4.30-alpha"]
                 [org.clojure/math.numeric-tower "0.0.4"]]

  :repl-options {:init-ns market-model.tree-parser})
