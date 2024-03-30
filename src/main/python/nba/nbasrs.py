import time
import requests
from selenium import webdriver
from bs4 import BeautifulSoup
import pandas 
from datetime import datetime
from basketballreference import month_urls
import numpy
import os

# browser = webdriver.Chrome()
current_time = datetime.now()
current_month = current_time.strftime('%B').lower()
print(current_month)
current_month_abbr = current_time.strftime('%b')
current_day = current_time.strftime('%d')
current_year = current_time.strftime('%Y')
urls = month_urls()
scoresdf = pandas.DataFrame(columns=['team','spread','opponent'])
nba_file = "nba_file.csv"


for url in urls:
    page = requests.get(url)
    soup = BeautifulSoup(page.content, 'html.parser')
    rows = soup.findAll('tr')
    nrows = len(rows)
    # print(nrows)

    for i in range(1,nrows):
        row_cells = rows[i].findAll('td')
        home_points = row_cells[4].get_text()
        away_points = row_cells[2].get_text()
        home_team = row_cells[3].get_text()
        away_team = row_cells[1].get_text()
        if str.isdigit(home_points):
            if str.isdigit(away_points):
                spread = int(home_points) - int(away_points)
                if spread > 20: spread = 20
                if spread < -20: spread = -20
                j = len(scoresdf.index)+1
                scoresdf.at[j,'spread'] = spread
                scoresdf.at[j,'team'] = home_team
                scoresdf.at[j,'opponent'] = away_team
                # print(home_team + "\t" + home_points + "\t" + away_team + "\t" + away_points)
    time.sleep(8)
    print(len(scoresdf.index))
# print(scoresdf.count)

spreads = scoresdf.groupby('team').spread.mean()
# print(spreads)

terms = []
solutions = []

for team in spreads.keys():
    row = []
    # get a list of team opponents
    opps = list(scoresdf[scoresdf['team'] == team]['opponent'])

    for opp in spreads.keys():
        if opp == team:
        	# coefficient for the team should be 1
            row.append(1)
        elif opp in opps:
        	# coefficient for opponents should be 1 over the number of opponents
            row.append(-1.0/len(opps))
        else:
        	# teams not faced get a coefficient of 0
            row.append(0)

    terms.append(row)

    # average game spread on the other side of the equation
    solutions.append(spreads[team])

solutions = numpy.linalg.solve(numpy.array(terms), numpy.array(solutions))
solutions

ratings = list(zip( spreads.keys(), solutions ))
srs = pandas.DataFrame(ratings, columns=['team', 'rating'])
srs.head()

rankings = srs.sort_values('rating', ascending=False).reset_index()[['team', 'rating']]
print(rankings)
# rankings.loc[:24]