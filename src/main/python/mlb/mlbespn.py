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
            try:
                away_pitcher_href = pitcher_href[0]['href']
            except:
                away_pitcher_href = ''
            try:
                home_pitcher_href = pitcher_href[1]['href']
            except:
                home_pitcher_href = ''
            # away_pitcher_page = pandas.read_html(away_pitcher_href)
            # home_pitcher_page = pandas.read_html(away_pitcher_href)

            away_team = cols[0].find('a')['href'].split("/")[6]
            home_team = cols[1].find('a')['href'].split("/")[6]
            # print(away_team + ' ' + home_team + ' ' +pitchers[0] + ' ' + pitchers[1])
            game = [away_team.replace("-"," "),home_team.replace("-"," "),pitchers[0],pitchers[1], away_pitcher_href, home_pitcher_href]
            games.append(game)
            # pitching_matchup = cols[4].find_all('a')
            # print(away_team + ' ' + home_team)
            # for pitcher in pitching_matchup:
            # print(pitcher['href'])
    return games

def scrape_pitcher(href):
    pitcher_page = pandas.read_html(href)
    print(pitcher_page)