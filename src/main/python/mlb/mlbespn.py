import time
import requests
from selenium import webdriver
from bs4 import BeautifulSoup
import pandas 
from datetime import date 
import numpy
import os

def todays_games():
    today = date.today().strftime('%Y%m%d') 
    url = 'https://www.espn.com/mlb/schedule/_/date/'+today
    driver = webdriver.Chrome()
    driver.get(url)
    time.sleep(5)
    html = driver.page_source
    soup = BeautifulSoup(html, 'html.parser')
    driver.quit()
    table = soup.find_all('table')
    rows = table[0].find_all('tr')
    games = []
    for row in rows:
        cols = row.find_all('td')
        if len(cols) > 0:
            pitchers = cols[4].text.split(" vs ")
            pitcher_href = cols[4].find_all('a')
            away_team = cols[0].find('a')['href'].split("/")[6]
            home_team = cols[1].find('a')['href'].split("/")[6]
            print(away_team.replace("-"," ") + ' ' + home_team.replace("-"," "))

            try:
                away_pitcher_href = pitcher_href[0]['href']
            except:
                away_pitcher_href = ''
            try:
                home_pitcher_href = pitcher_href[1]['href']
            except:
                home_pitcher_href = ''
            print(pitchers[0])
            away_pitcher_last5 = scrape_pitcher(away_pitcher_href)
            time.sleep(5)
            print(pitchers[1])
            home_pitcher_last5 = scrape_pitcher(home_pitcher_href)
            time.sleep(5)
            print()
            game = [away_team,home_team,pitchers[0],pitchers[1], away_pitcher_last5, home_pitcher_last5]
            games.append(game)
            # pitching_matchup = cols[4].find_all('a')
            # print(away_team + ' ' + home_team)
            # for pitcher in pitching_matchup:
            # print(pitcher['href'])
    # print(games)
    return games

def scrape_pitcher(href):
    pitcher_page = pandas.read_html(href)
    pitcher_last5 = pitcher_page[3]
    pitcher_last5['ip_str'] = pitcher_last5['IP'].astype(str)
    pitcher_last5[['ip_inning','ip_inning_part']] = pitcher_last5['ip_str'].str.split('.', expand=True)
    pitcher_last5['outs'] = pitcher_last5['ip_inning'].astype(int)*3+pitcher_last5['ip_inning_part'].astype(int)
    outs = pitcher_last5['outs'].mean()
    hits = pitcher_last5['H'].mean()
    walks = pitcher_last5['BB'].mean()
    runs = pitcher_last5['R'].mean()
    so = pitcher_last5['K'].mean()
    hr = pitcher_last5['HR'].mean()
    gameScore = 47.5 + so + (outs*1.5) - (walks*2) - (hits*2) - (runs*3) - (hr * 4)
    gameScore = str("{0:.1f}".format(gameScore))
    last5_text = str(gameScore) + ' Last 5 Avg ' + ' outs ' + str(outs) + ' so ' + str(so) + ' hits ' +  str(hits) + ' walks ' + str(walks) + ' runs ' + str(runs) + ' hr ' + str(hr)
    print(last5_text)

    pitcher_year = pitcher_page[2]
    gp = int(pitcher_year['GP'][0])
    ip = pitcher_year['IP'][0]
    ip_arr = str(ip).split('.')
    outs = int(ip_arr[0])/gp*3+int(ip_arr[1])
    hits = pitcher_year['H'][0]/gp
    walks = pitcher_year['BB'][0]/gp
    runs = pitcher_year['R'][0]/gp
    so = pitcher_year['K'][0]/gp
    hr = pitcher_year['HR'][0]/gp
    gameScore = 47.5 + so + (outs*1.5) - (walks*2) - (hits*2) - (runs*3) - (hr * 4)
    gameScore = str("{0:.1f}".format(gameScore))
    outs = str("{0:.1f}".format(outs))
    hits = str("{0:.1f}".format(hits))
    walks = str("{0:.1f}".format(walks))
    runs = str("{0:.1f}".format(runs))
    so = str("{0:.1f}".format(so))
    hr = str("{0:.1f}".format(hr))
    pitcher_year_text = str(gameScore) + ' Last ' + str(gp) + ' Avg ' + ' outs ' + str(outs) + ' so ' + str(so) + ' hits ' +  str(hits) + ' walks ' + str(walks) + ' runs ' + str(runs) + ' hr ' + str(hr)
    print(pitcher_year_text)
    return last5_text + ' - ' + pitcher_year_text

todays_games()