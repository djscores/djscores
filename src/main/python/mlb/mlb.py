# from mlbespn import todays_games
from bs4 import BeautifulSoup as soup
import requests
import re

# espn_games = todays_games()
url = 'https://www.covers.com/sports/mlb/matchups'
page = requests.get(url)
soup = soup(page.content, 'html.parser')
matchup_sections = soup.find_all('div',{'class': 'cmg_matchup_game_box cmg_game_data'})

for matchup_section in matchup_sections:
    matchup_pattern = re.compile("mlb-matchup-link")
    matchup_a = matchup_section.find('a', {'data-linkcont': matchup_pattern})
    matchup_href = matchup_a.attrs.get('href')
    print(matchup_href)
    matchup_pattern = re.compile("mlb/teams/main")
    matchup_teams = matchup_section.find_all('a', {'href': matchup_pattern})
    print(matchup_teams)
    