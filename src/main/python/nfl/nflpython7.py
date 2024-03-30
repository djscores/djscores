import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import statsmodels.formula.api as smf
import numpy as np
import os

chap_7_all_py_file = "chap_7_all_py_file.csv"

if os.path.isfile(chap_7_all_py_file):
    draft_py = pd.read_csv(chap_7_all_py_file)
else:
    draft_py = pd.DataFrame()
    for i in range(2000, 2022 + 1):
        url = "https://www.pro-football-reference.com/years/" + str(i) + "/draft.htm"
        web_data = pd.read_html(url, header=1)[0]
        web_data["Season"] = i
        web_data = web_data.query('Tm != "Tm"')
        draft_py = pd.concat([draft_py, web_data])
    draft_py.to_csv(chap_7_all_py_file)

conditions = [
    (draft_py.Tm == "SDG"),
    (draft_py.Tm == "OAK"),
    (draft_py.Tm == "STL"),
]
choices = ["LAC", "LVR", "LAR"]
draft_py["Tm"] = np.select(conditions, choices, default=draft_py.Tm)
draft_py.loc[draft_py["DrAV"].isnull(), "DrAV"] = 0
draft_py.to_csv("data_py.csv", index=False)

# print(draft_py.head())
# print(draft_py.columns)
draft_py_use = draft_py[["Season", "Pick", "Tm", "Player", "Pos", "wAV", "DrAV"]]

print(draft_py_use)
sns.set_theme(style="whitegrid", palette="colorblind")

draft_py_use_pre2019 = draft_py_use.query("Season <= 2019")

## format columns as numeric or integers
draft_py_use_pre2019 = draft_py_use_pre2019.astype({"Pick": int, "DrAV": float})

sns.regplot(
    data=draft_py_use_pre2019,
    x="Pick",
    y="DrAV",
    line_kws={"color": "red"},
    scatter_kws={"alpha": 0.2},
)
plt.show()