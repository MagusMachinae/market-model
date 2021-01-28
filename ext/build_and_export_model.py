# Imports
from sklearn.datasets import load_boston
import pandas as pd
from sklearn.ensemble import GradientBoostingRegressor
import pickle

# Load Data
boston = load_boston()

X = boston["data"][100:]
y = boston["target"][100:]

Xeval = boston["data"][:100]
yeval = boston["target"][:100]

feat_names = boston["feature_names"]

# create and fit the model.
settings = {"max_depth": 9, "n_estimators": 500, "subsample": 0.5}
reg = GradientBoostingRegressor(random_state=0, **settings)
reg.fit(X, y)

with open("gbm_model.pickle", "wb") as w:
    pickle.dump(reg, w)

with open("feature_names.txt", "w") as w:
    print("\n".join(feat_names), file=w)

