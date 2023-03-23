import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("lab4.csv", delimiter=";", encoding='utf-8')
df.boxplot()
plt.xticks(rotation=90, fontsize=5)
plt.show()