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
    teams = matchup_section.find('div',{'class': 'cmg_matchup_header_team_names'})
    print(teams.text.strip())
    probables = matchup_section.find('div',{'class': 'cmg_l_row'})
    probable_a = probables.find_all('a')
    if len(probable_a) > 0:
        away_probable = probable_a[0]
        if len(probable_a) > 1:
            home_probable = probable_a[1]

        away_probable_href = away_probable.attrs.get('href')
        away_probable_page = pd.read_html(away_probable_href)
        if len(away_probable_page) > 5:
            previous_avg = away_probable_page[5]
        for table in away_probable_page:
            if 'IP' in table.index:
                previous_avg = table
        # previous_avg = away_probable_page[5]
        probable_len = len(previous_avg)-1
        probable_ip = previous_avg['IP'][len(previous_avg)-1]
        probable_ip_str = str(probable_ip)
        probable_outs = probable_ip_str.split(".")
        probable_outs_float = float(probable_outs[0])*3+int(probable_outs[1])
        probable_pitcher_hits = previous_avg['H'][probable_len]
        probable_pitcher_walks = previous_avg['BB'][probable_len]
        probable_pitcher_runs = previous_avg['R'][probable_len]
        probable_pitcher_so = previous_avg['SO'][probable_len]
        probable_pitcher_hr = previous_avg['HR'][probable_len]
        probable_pitcher_lastavg = previous_avg['Date'][probable_len]
        gameScore = 47.5 + probable_pitcher_so + (probable_outs_float*1.5) - (probable_pitcher_walks*2) - (probable_pitcher_hits*2) - (probable_pitcher_runs*3) - (probable_pitcher_hr * 4)
        away_pitcher = away_probable.text.strip() + ' ' + str("{0:.1f}".format(gameScore))
        print(away_pitcher)
        print(probable_pitcher_lastavg + ' outs ' + str(probable_outs_float) + ' so ' + str(probable_pitcher_so) + ' hits ' +  str(probable_pitcher_hits) + ' walks ' + str(probable_pitcher_walks) + ' runs ' + str(probable_pitcher_runs) + ' hr ' + str(probable_pitcher_hr))

        home_probable_href = home_probable.attrs.get('href')
        home_probable_page = pd.read_html(home_probable_href)
        if len(home_probable_page) > 5:
            previous_avg = home_probable_page[5]
        for table in home_probable_page:
            if 'IP' in table.index:
                previous_avg = table
        probable_len = len(previous_avg)-1
        probable_ip = previous_avg['IP'][len(previous_avg)-1]
        probable_ip_str = str(probable_ip)
        probable_outs = probable_ip_str.split(".")
        probable_outs_float = float(probable_outs[0])*3+int(probable_outs[1])
        probable_pitcher_hits = previous_avg['H'][probable_len]
        probable_pitcher_walks = previous_avg['BB'][probable_len]
        probable_pitcher_runs = previous_avg['R'][probable_len]
        probable_pitcher_so = previous_avg['SO'][probable_len]
        probable_pitcher_hr = previous_avg['HR'][probable_len]
        probable_pitcher_lastavg = previous_avg['Date'][probable_len]
        gameScore = 47.5 + probable_pitcher_so + (probable_outs_float*1.5) - (probable_pitcher_walks*2) - (probable_pitcher_hits*2) - (probable_pitcher_runs*3) - (probable_pitcher_hr * 4)
        home_pitcher = home_probable.text.strip() + ' ' + str("{0:.1f}".format(gameScore))
        print(home_pitcher)
        print(probable_pitcher_lastavg + ' outs ' + str(probable_outs_float) + ' so ' + str(probable_pitcher_so) + ' hits ' +  str(probable_pitcher_hits) + ' walks ' + str(probable_pitcher_walks) + ' runs ' + str(probable_pitcher_runs) + ' hr ' + str(probable_pitcher_hr))

    matchup = matchup_section.select("a[data-linkcont*=score-mlb-matchup-link]")
    if matchup:
        href = matchup[0].attrs.get('href')
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
    # if 'IP' not in away_pitcher_last5.index:
    #     away_pitcher_last5 = matchup[11]
    
    # print(away_pitcher_last5)
    away_last5_len = len(away_pitcher_last5)-1
    away_last5_ip = away_pitcher_last5['IP'][away_last5_len]
    away_last5_ip_str = str(away_last5_ip)
    away_last5_outs = away_last5_ip_str.split(".")
    away_last5_outs_float = float(away_last5_outs[0])*3+int(away_last5_outs[1])
    away_last5_hits = away_pitcher_last5['H'][away_last5_len]
    away_last5_walks = away_pitcher_last5['BB'][away_last5_len]
    away_last5_runs = away_pitcher_last5['R'][away_last5_len]
    away_last5_so = away_pitcher_last5['SO'][away_last5_len]
    away_last5_hr = away_pitcher_last5['HR'][away_last5_len]
    away_last5_gameScore = 47.5 + away_last5_so + (away_last5_outs_float*1.5) - (away_last5_walks*2) - (away_last5_hits*2) - (away_last5_runs*3) - (away_last5_hr * 4)
    # print(away_last5_gameScore)
    # print("{:.1f}".format(away_last5_gameScore))
    
    away_last5_text = 'Away pitcher ' + str("{0:.1f}".format(away_last5_gameScore))
    print(away_last5_text)
    print('Last 5 Avg ' + ' outs ' + str(away_last5_outs_float) + ' so ' + str(away_last5_so) + ' hits ' +  str(away_last5_hits) + ' walks ' + str(away_last5_walks) + ' runs ' + str(away_last5_runs) + ' hr ' + str(away_last5_hr))

    home_pitcher_last5 = matchup[12]
    # if 'IP' not in home_pitcher_last5.index:
    #     home_pitcher_last5 = matchup[13]
    
    # print(home_pitcher_last5)
    home_last5_len = len(home_pitcher_last5)-1
    home_last5_ip = home_pitcher_last5['IP'][home_last5_len]
    home_last5_ip_str = str(home_last5_ip)
    home_last5_outs = home_last5_ip_str.split(".")
    home_last5_outs_float = float(home_last5_outs[0])*3+int(home_last5_outs[1])
    home_last5_hits = home_pitcher_last5['H'][home_last5_len]
    home_last5_walks = home_pitcher_last5['BB'][home_last5_len]
    home_last5_runs = home_pitcher_last5['R'][home_last5_len]
    home_last5_so = home_pitcher_last5['SO'][home_last5_len]
    home_last5_hr = home_pitcher_last5['HR'][home_last5_len]
    home_last5_gameScore = 47.5 + home_last5_so + (home_last5_outs_float*1.5) - (home_last5_walks*2) - (home_last5_hits*2) - (home_last5_runs*3) - (home_last5_hr * 4)
    # print(home_last5_gameScore)
    # print("{:.1f}".format(home_last5_gameScore))
    home_last5_text = 'Home pitcher ' + str("{0:.1f}".format(home_last5_gameScore))
    print(home_last5_text)
    print('Last 5 Avg ' + ' outs ' + str(home_last5_outs_float) + ' so ' + str(home_last5_so) + ' hits ' +  str(home_last5_hits) + ' walks ' + str(home_last5_walks) + ' runs ' + str(home_last5_runs) + ' hr ' + str(home_last5_hr))

    # time.sleep(5)
    # pitching_url_last10 = 'https://www.covers.com/'+href+'/stats-analysis/pitching/last10'
    # hitting_url_last10 = 'https://www.covers.com/'+href+'/stats-analysis/hitting/last10'
    # hitting_throwhand_url_last10 = 'https://www.covers.com/'+href+'/stats-analysis/hittingvsstarterthrowhand/last10'
    # pitching_last10 = pd.read_html(pitching_url_last10)
    # time.sleep(5)
    # hitting_last10 = pd.read_html(hitting_url_last10)
    # time.sleep(5)
    # hitting_throwhand_url_last10 = pd.read_html(hitting_throwhand_url_last10)
    # relievers = pitching_url_last10[2]
    
# with open("covers.json","w") as jsonFile:
#     json.dump(data,jsonFile,indent=4)