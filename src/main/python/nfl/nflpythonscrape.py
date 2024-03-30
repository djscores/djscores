import pandas as pd
import seaborn as sns
import os.path
import matplotlib.pyplot as plt
import statsmodels.formula.api as smf
import numpy as np
import os

url = "https://www.basketball-reference.com/leagues/NBA_2024.html"

chap_7_2022_py_file = "chap_7_2022_py_file.csv"

# if os.path.isfile(chap_7_2022_py_file):
#     draft_py = pd.read_csv(chap_7_2022_py_file)
# else:
draft_py = pd.read_html(url, header=1)[10]
draft_py.to_csv(chap_7_2022_py_file)

# draft_py.loc[draft_py["DrAV"].isnull(), "DrAV"] = 0
draft_py_use = draft_py[["Team", "SRS"]]
print(draft_py_use)

sns.set_theme(style="whitegrid", palette="colorblind")
sns.scatterplot(data=draft_py_use, x="Team", y="SRS")
plt.show()