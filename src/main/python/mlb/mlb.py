from mlbespn import todays_games
from bs4 import BeautifulSoup as soup
import requests

espn_games = todays_games()
url = 'https://www.covers.com/sports/mlb/matchups'
page = requests.get(url)
soup = soup(page.content, 'html.parser')
matchup_sections = soup.find_all('div',{'class': 'cmg_matchup_game_box cmg_game_data'})

for matchup_section in matchup_sections:
    