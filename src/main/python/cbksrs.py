import time
import requests
from selenium import webdriver
from bs4 import BeautifulSoup
import pandas 
from datetime import datetime

url = 'https://www.sports-reference.com/cbb/seasons/men/2024-ratings.html'
page = requests.get(url)
soup = BeautifulSoup(page.content, 'html.parser')
rows = soup.find_all('tr')
nrows = len(rows)
print(nrows)
cbk_ratings = pandas.read_html(url, header=1)[0]
print(cbk_ratings)
# top_ratings = cbk_ratings.sort_values('SRS', ascending=False).reset_index()[['team', 'rating']]
cbk_ratings = cbk_ratings.query('Rk' != 'Rk')
cbk_ratings = cbk_ratings.query('SRS' != 'SRS')
top100_ratings = cbk_ratings.loc[:200]
print(top100_ratings)

for i in range(1,nrows):
    cells = rows[i].findAll('td')
    if len(cells) > 0:
        team_a = cells[0].find('a')
        href = team_a.attrs.get('href')
        team_url = 'https://www.sports-reference.com'+href.replace(".html","-schedule.html")
        print(team_url)
        team_page = requests.get(team_url)
        team_soup = BeautifulSoup(team_page.content, 'html.parser')
        team_schedule = pandas.read_html(team_url, header=0)[1]
        print(team_schedule.head())
        print(len(team_schedule))
        print(team_schedule['Opponent'])
        d1_schedule = team_schedule['Opponent'].isin(cbk_ratings['School'])
        print(len(d1_schedule))
        time.sleep(8)
