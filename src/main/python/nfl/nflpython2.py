import pandas as pd
import numpy as np
import nfl_data_py as nfl

pbp_py = nfl.import_pbp_data([2023])

filter_crit = 'play_type == "pass" & air_yards.notnull()'

pbp_py_p = (
    pbp_py.query(filter_crit)
    .groupby(["passer_id", "passer"])
    .agg({"air_yards": ["count", "mean"]})
)
# print(
#     pbp_py_p.columns.values.tolist()
# )
pbp_py_p.columns = list(map("_".join, pbp_py_p.columns.values))
sort_crit = "air_yards_count > 100"
# print(
#     pbp_py_p.query(sort_crit)\
#     .sort_values(by="air_yards_mean", ascending=[False])\
#     . to_string()
# )

# seasons = range(2016, 2022 + 1)
seasons = range(2023,2024)
pbp_py = nfl.import_pbp_data(seasons)

pbp_py_p = \
    pbp_py\
    .query("play_type == 'pass' & air_yards.notnull()")\
    . reset_index()

pbp_py_p["pass_length_air_yards"] = np.where(
    pbp_py_p["air_yards"] >= 20, "long", "short"
)

pbp_py_p["passing_yards"] = \
np.where(
    pbp_py_p["passing_yards"].isnull(), 0, pbp_py_p["passing_yards"]
)

pbp_py_p["passing_yards"].describe()

print(
pbp_py_p\
    .query('pass_length_air_yards == "short"')["passing_yards"]\
    .describe()
)

import seaborn as sns
import matplotlib.pyplot as plt

sns.displot(data=pbp_py, x="passing_yards")
# plt.show()

pbp_py_p_short = \
    pbp_py_p\
    .query('pass_length_air_yards == "short"')

pbp_py_hist_short = \
    sns.displot(data=pbp_py_p_short,
                binwidth=1,
                x="passing_yards")
pbp_py_hist_short\
    .set_axis_labels(
        "Yards gained (or lost) during a passing play", "Count"
    )
# plt.show()

pbp_py_p_s = \
    pbp_py_p \
    .groupby(["passer_id", "passer", "season"]) \
    .agg({"passing_yards": ["mean", "count"]})

pbp_py_p_s.columns = list(map("_".join, pbp_py_p_s.columns.values))

pbp_py_p_s \
    .rename(columns={'passing_yards_mean': 'ypa',
    'passing_yards_count': 'n'},
    inplace=True)

print (
    pbp_py_p_s \
        .sort_values(by=["ypa"], ascending=False) \
        .head()
)

pbp_py_p_s_100 = \
    pbp_py_p_s \
    .query("n >= 100") \
    .sort_values(by=["ypa"], ascending=False)

print (
    pbp_py_p_s_100.head()
)

