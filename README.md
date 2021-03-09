# market-model

A Clojure library designed to parse in decision trees from a Python machine
learning model and compute results in Clojure. The namespace ```tree-parser.clj``` contains the logic to extract the generated regression from a Python object and convenience functions for manipulating the regression model.

To use this library, require it in your project.clj with ```[com.github.github-account/fork-name "git-tag"]```, place the python file or pickle containing the model and feature names of the data
into the project,  call ``` (tree-parser/generate-trees! (tree-parser/un-pickle "path/to/your-file-name.pickle") feature-names output-file-string precision``` with your ensemble to generate the trees and place them in an output file in a directory specified in the file-string. If a python file defining a model is used, [libpython-clj][] can be used to directly load the file and the var referencing the model as the first argument to ```generate-trees!```.

The symbol ```feature-names``` refers to the vector of symbols representing the variables in the decision tree and can be generated either by using ``` (features-from-file file) ``` to slurp them in from a ```.txt``` file or directly from a python dataset via interop with ```(get-feature-names python-dataset)```.

The generated trees will have precision in signifcant figures set by the integer value passed as ```precision```.
Any raw text file extension can be supplied for the target path, though ```.edn``` is advised for outputting Clojure data structures.

The output of a successful parsing will be a vector containing the functions to
be used in the prediction, and a vector containing the learning rate and the
initial estimate to which the reduction over the tree outputs is added.

The namespace ```market-price``` contains ```derived-market-price``` which runs
the regression over the input vector.


## name-spaces:
market-model.tree-parser: contains function to parse in a given set of decision trees and helpers for interop access.

market-model.market-price: houses the logic for running the reduction over the models.

market-model.util: contains generic helper and utility functions.

## Usage

FIXME

## License

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

[libpython-clj]: https://github.com/clj-python/libpython-clj "libpython-clj"
