# market-model

A Clojure library designed to parse in decision trees from a Python machine
learning model and compute results in Clojure. The namespace ```tree-parser.clj``` contains the logic to extract the generated regression from a Python object and convenience functions for manipulating the regression model.

To use this library, require it in your project.clj, place the python file or pickle containing the model and feature names of the data
into the project,  call ``` (generate-trees! (un-pickle "path/to/your-file-name.pickle") feature-names output-file-string precision``` with your ensemble to generate the trees and place them in an output file in a directory specified in the file-string.

The symbol ```feature-names``` refers to the vector of symbols representing the variables in the decision tree and can be generated either by using ``` (features-from-file file) ``` to slurp them in from a ```.txt``` file or directly from a python dataset via interop with ```(get-feature-names python-dataset)```.

The generated trees will have precision in signifcant figures set by the integer value passed as ```precision```.
Any raw text file extension can be supplied, though ```.edn``` is advised for outputting Clojure data structures.


## name-spaces:
market-model.tree-parser: contains function to parse in a given set of decision trees.

market-model.market-price: houses the logic for running the reduction over the models.

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
