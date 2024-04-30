from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import pandas as pd
import json
import requests
from bs4 import BeautifulSoup as soup
import time
import re

url = 'https://www.covers.com/sports/mlb/matchups'
page = requests.get(url)
soup = soup(page.content, 'html.parser')
matchup_pattern = re.compile("mlb-matchup-link")
matchups = soup.find_all('a', {'data-linkcont': matchup_pattern})
print('matchups - ')
print(len(matchups))
data = {}
# driver = webdriver.Chrome()
for matchup in matchups:
    href = matchup.attrs.get('href')
    matchup_url = 'https://www.covers.com'+href
    print(matchup_url)
    page = requests.get(url)
    # driver.get(matchup_url)
    time.sleep(5)
    # driver.execute_script("window.scrollTo(0, 1000);")
    time.sleep(5)
    matchup_soup = soup(page.content, 'html.parser')    
    pitchers = soup.find_all('a', {'class':"anchor-with-border"})
    matchup = pd.read_html(matchup_url)
    away_pitcher_last5 = matchup[11]
    print(away_pitcher_last5)
    home_pitcher_last5 = matchup[13]
    print(home_pitcher_last5)
    time.sleep(8)
    

# with open("covers.json","w") as jsonFile:
#     json.dump(data,jsonFile,indent=4)