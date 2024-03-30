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

for i in range(1,nrows):
    cells = rows[i].findAll('td')
    if len(cells) > 0:
        team_a = cells[0].find('a')
        href = team_a.attrs.get('href')
        print(href)
        time.sleep(8)
