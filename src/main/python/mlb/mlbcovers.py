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
pitcher_pattern = re.compile("starter-table")
matchups = soup.find_all('a', {'data-linkcont': matchup_pattern})
print('matchups - ')
print(len(matchups))
data = {}

for matchup in matchups:
    href = matchup.attrs.get('href')
    matchup_url = 'https://www.covers.com'+href
    print(matchup_url)
    driver = webdriver.Chrome()
    driver.get(matchup_url)
    matchup_html = driver.page_source
    matchup_soup = soup(matchup_html, 'html.parser')
    pitcher_table = matchup_soup.find_all('table', attrs={'class':"starter-table"})
    rows = pitcher_table.find_all('tr')
    print('pitchers - ')
    print(len(pitcher_table))
    time.sleep(8)
    

# with open("covers.json","w") as jsonFile:
#     json.dump(data,jsonFile,indent=4)