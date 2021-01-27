import pandas as pd
from pandas.api.types import is_numeric_dtype
from pathlib import Path
import numpy as np



# Imports
from sklearn.datasets import load_boston
import pandas as pd

# Load Data
boston = load_boston()



import matplotlib.pyplot as plt
import seaborn as sns



import json
from hashlib import sha256



from sklearn.datasets import make_regression
from sklearn.ensemble import GradientBoostingRegressor
from sklearn.model_selection import train_test_split
from sklearn.inspection import permutation_importance



from sklearn.inspection import permutation_importance
from sklearn.metrics import mean_squared_error



def fit_model_and_predict(X, y, X_held_out, y_held_out, Xnames, **kwargs):

    """
    This function serves as a wrapper to GBoost- allows us to investigate model
    specifications to optimise
    """

    # create and fit the model.
    reg = GBR(random_state=0, **kwargs)
    reg.fit(X, y)
    params = dict(kwargs)

    # use the learnt model to predict premiums for the test set.
    prediction_test = reg.predict(X_held_out)

    # use the learnt model to predict premiums for the training set.
    prediction_train = reg.predict(X)


    # code to produce the performance plot.
    test_score = np.zeros((params['n_estimators'],), dtype=np.float64)
    for i, y_pred in enumerate(reg.staged_predict(X_held_out)):
        test_score[i] = reg.loss_(y_held_out, y_pred)

    fig, axes = plt.subplots(ncols=2, figsize=(8, 4))
    ax1 = axes[0]
    ax2 = axes[1]

    ax1.set_title('Deviance')
    ax1.plot(
        np.arange(params["n_estimators"]) + 1,
        reg.train_score_,
        'b-',
        label='Training Set Deviance')
    ax1.plot(
        np.arange(params["n_estimators"]) + 1,
        test_score,
        'r-',
        label='Test Set Deviance')

    ax1.legend(loc='upper right')
    ax1.set_xlabel('Boosting Iterations')
    ax1.set_ylabel('Deviance')

#     fig.tight_layout()
#     plt.show()

    # code to permute importances.
    #feature_importance = reg.feature_importances_
    #sorted_idx = np.argsort(feature_importance)
    #pos = np.arange(sorted_idx.shape[0]) + .5

    #plt.barh(pos, feature_importance[sorted_idx], align='center')
    #plt.yticks(pos, np.array(diabetes.feature_names)[sorted_idx])
    #plt.title('Feature Importance (MDI)')

    result = permutation_importance(
        reg, X_held_out, y_held_out, n_repeats=10, random_state=42, n_jobs=2)

    sorted_idx = result.importances_mean.argsort()[::-1]

    ax2.boxplot(
        result.importances[sorted_idx[:10]].T,
        vert=False,
        labels=Xnames[sorted_idx[:10]])

    ax2.set_title("Permutation Importance (test set)")

    fig.tight_layout()

    importance_frame = pd.DataFrame(result.importances, index=Xnames).mean(axis=1)
    importance_frame.name = "permutation_importance"

    return prediction_test, prediction_train, importance_frame, reg






X = boston["data"][100:]
y = boston["target"][100:]

Xeval = boston["data"][:100]
yeval = boston["target"][:100]



GBR = GradientBoostingRegressor



settings = {"max_depth": 9, "n_estimators": 500, "subsample": 0.5}

y_prime, train_estimate_y, frame, model = fit_model_and_predict(
    X, y, Xeval, yeval, Xnames=boston["feature_names"], **settings)

mean_squared_error(y_prime, yeval)



def create_performance_plot(
    xvalues,
    yvalues,
    xlabel="observed_values",
    ylabel="model_prediction",
    downsample_n=2000,
    axes_limit=0.8):

    if downsample_n is not None:
        ix = np.random.choice(xvalues.shape[0], downsample_n, replace=False)
        xvalues = np.take(xvalues, ix)
        yvalues = np.take(yvalues, ix)

    f, ax = plt.subplots(figsize=(7, 7))
    ax.scatter(xvalues, yvalues, alpha=0.2)
    sns.despine(ax=ax)

    maxv = max(np.max(xvalues), np.max(yvalues))
    print(maxv)

    ax.grid(True)
    #ax.plot([0, maxv], [0, maxv], 'k--')
    xv = np.arange(0, maxv, 1000)
    ax.plot(xv, xv*1.1, 'k--', lw=1)
    ax.plot(xv, xv*0.9, 'k--', lw=1)
    ax.plot(xv, xv*1.2, 'k--', lw=.5)
    ax.plot(xv, xv*0.8, 'k--', lw=.5)


    ax.set_ylim((0, maxv * axes_limit))
    ax.set_xlim((0, maxv * axes_limit))

    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)

    return f
settings = {"max_depth": 9, "n_estimators": 500, "subsample": 0.5}

y_prime, train_estimate_y, frame, model = fit_model_and_predict(
    X, y, Xeval, yeval, Xnames=boston["feature_names"], **settings)
