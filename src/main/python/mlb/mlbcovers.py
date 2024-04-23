from selenium import webdriver
from selenium.webdriver.common.by import By
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
pitcher_pattern = re.compile("cmg_l_col cmg_l_span_6 cmg_team_starting_pitcher")
matchups = soup.find_all('a', {'data-linkcont': matchup_pattern})
pitchers = soup.find_all('div', {'class': pitcher_pattern})
print(len(matchups))
data = {}

for matchup in matchups:
    href = matchup.attrs.get('href')
    matchup_url = 'https://www.covers.com'+href
    print(matchup_url)
    matchup_page = requests.get(matchup_url)
    soup = soup(matchup_page.content, 'html.parser')
    time.sleep(8)
    

# with open("covers.json","w") as jsonFile:
#     json.dump(data,jsonFile,indent=4)