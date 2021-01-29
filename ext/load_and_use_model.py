import pickle
from sklearn.datasets import load_boston
import numpy as np
import pandas as pd

with open("gbm_model.pickle", "rb") as r:
    gbm_model = pickle.load(r)

with open("feature_names.txt") as r:
    feature_names = [s.strip() for s in r.readlines()]

# generate some test data:
boston = load_boston()

# just use 20 rows for validation
X = boston["data"][:20]

qfactor = np.random.random(X.shape)

v = (qfactor - 0.5) / 10

X_perturbed = np.round(np.power(X, v + 1), 3)
y = gbm_model.predict(X_perturbed)

df = pd.DataFrame(X_perturbed, columns=feature_names)
df["y"] = y
df.to_csv("validate_output.csv", index=False)