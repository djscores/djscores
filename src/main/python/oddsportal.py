from selenium import webdriver
from selenium.webdriver.common.by import By
import pandas as pd
from time import sleep
import json

driver = webdriver.Chrome()
driver.get("https://www.oddsportal.com/basketball/usa/nba/")
sleep(5)
gameRows = driver.find_elements(By.XPATH,'//div[@class="group flex"]')
print(len(gameRows))
rowData = [row.text.split("\n") for row in gameRows]
print(len(rowData))
data = {}
for row in rowData:
    if row[2] == "\u2013":
        try:
            data.update({row[1]+" vs. "+row[3]:{
                                        "Team1_Odds":row[4],
                                        "Team2_Odds":row[5],
                                        "No. of Bookmakers":row[6]
                                        }})
        except:
            continue
    else:
        try:
            data.update({row[1]+" vs. "+row[5]:{
                                        "Team1_Odds":row[6],
                                        "Team2_Odds":row[7],
                                        "No. of Bookmakers":row[8]
                                        }})
        except:
            continue


with open("NBAodds.json","w") as jsonFile:
    json.dump(data,jsonFile,indent=4)