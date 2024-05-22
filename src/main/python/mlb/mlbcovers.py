from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import pandas as pd
import numpy
import json
import requests
from bs4 import BeautifulSoup as soup
import time
import re

url = 'https://www.covers.com/sports/mlb/matchups'
page = requests.get(url)
soup = soup(page.content, 'html.parser')
# matchup_pattern = re.compile("mlb-matchup-link")
# matchups = soup.find_all('a', {'data-linkcont': matchup_pattern})
matchup_sections = soup.find_all('div',{'class': 'cmg_matchup_game_box cmg_game_data'})
for matchup_section in matchup_sections:
    probables = matchup_section.find('div',{'class': 'cmg_l_row'})
    probable_a = probables.find_all('a')
    for probable in probable_a:
        probable_href = probable.attrs.get('href')
        probable_page = pd.read_html(probable_href)
        previous_avg = probable_page[5]
        ip = previous_avg['IP'][len(previous_avg)-1]
        # away_pitcher_ip = previous_avg['IP'][5]
        print(probable_href)
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
    
    matchup_soup = soup(page.content, 'html.parser')    
    pitchers = soup.find_all('a', {'class':"anchor-with-border"})
    matchup = pd.read_html(matchup_url)

    away_pitcher_last5 = matchup[10]    
    print(away_pitcher_last5)
    away_pitcher_ip = away_pitcher_last5['IP'][5]
    away_pitcher_ip_str = str(away_pitcher_ip)
    away_outs = away_pitcher_ip_str.split(".")
    away_outs_int = int(away_outs[0])*3+int(away_outs[1])
    away_pitcher_hits = away_pitcher_last5['H'][5]
    away_pitcher_walks = away_pitcher_last5['BB'][5]
    away_pitcher_runs = away_pitcher_last5['R'][5]
    away_pitcher_so = away_pitcher_last5['SO'][5]
    away_pitcher_hr = away_pitcher_last5['HR'][5]
    
    home_pitcher_last5 = matchup[12]
    print(home_pitcher_last5)
    home_pitcher_ip = home_pitcher_last5['IP'][5]
    home_pitcher_ip_str = str(home_pitcher_ip)
    home_outs = home_pitcher_ip_str.split(".")
    home_outs_int = int(home_outs[0])*3+int(home_outs[1])
    home_pitcher_hits = home_pitcher_last5['H'][5]
    home_pitcher_walks = home_pitcher_last5['BB'][5]
    home_pitcher_runs = home_pitcher_last5['R'][5]
    home_pitcher_so = home_pitcher_last5['SO'][5]
    home_pitcher_hr = home_pitcher_last5['HR'][5]
    
    time.sleep(5)
    pitching_url_last10 = 'https://www.covers.com/'+href+'/stats-analysis/pitching/last10'
    hitting_url_last10 = 'https://www.covers.com/'+href+'/stats-analysis/hitting/last10'
    hitting_throwhand_url_last10 = 'https://www.covers.com/'+href+'/stats-analysis/hittingvsstarterthrowhand/last10'
    pitching_last10 = pd.read_html(pitching_url_last10)
    time.sleep(5)
    hitting_last10 = pd.read_html(hitting_url_last10)
    time.sleep(5)
    hitting_throwhand_url_last10 = pd.read_html(hitting_throwhand_url_last10)
    relievers = pitching_url_last10[2]
    
# with open("covers.json","w") as jsonFile:
#     json.dump(data,jsonFile,indent=4)