import time
import requests
from bs4 import BeautifulSoup
import pandas

baseball_reference = 'https://www.baseball-reference.com/leagues/MLB-standings.shtml#expanded_standings_overall'
baseball_ref_previews = 'https://www.baseball-reference.com/previews/'

previews_page = requests.get(baseball_ref_previews)
time.sleep(2)
previews_page_soup = BeautifulSoup(previews_page.content, 'html.parser')

summaries = previews_page_soup.select("div[class*=game_summary]")

for summary in summaries:
    summary_tables = summary.select("table")
    teams = summary_tables[0]
    team_links = teams.select("a[href*=teams]")
    preview = teams.select("a[href*=preview]")[0]['href']
    away_link = team_links[0]['href']
    home_link = team_links[1]['href']
    away_team_abbr = away_link.split('/')[2]
    home_team_abbr = home_link.split('/')[2]
    year = away_link.split('/')[3].split('.')[0]

    pitchers_table = summary_tables[1]
    starters = pitchers_table.select("a[href*=players]")
    away_starter = starters[0].text
    home_starter = starters[1].text
    away_starter_id = starters[0]['href'].split('/')[5].split('.')[0]
    home_starter_id = starters[1]['href'].split('/')[5].split('.')[0]

    #https://www.baseball-reference.com/players/gl.fcgi?id=cannigr01&t=p&year=2024

    away_starter_log = 'https://www.baseball-reference.com/players/gl.fcgi?id=' + away_starter_id + '&t=p&year=' + year
    home_starter_log = 'https://www.baseball-reference.com/players/gl.fcgi?id=' + home_starter_id + '&t=p&year=' + year

    away_starter_table = pandas.read_html(away_starter_log)[0]
    time.sleep(2)
    # away_starter_table = away_starter_table.query('Tm != "Tm"')
    away_starter_len = len(away_starter_table)
    away_starter_gamescore = away_starter_table['GSc'][away_starter_len-1]
    
    home_starter_table = pandas.read_html(home_starter_log)[0]
    time.sleep(2)
    # home_starter_table = home_starter_table.query('Tm != "Tm"')
    home_starter_len = len(home_starter_table['GSc'])
    home_starter_gamescore = home_starter_table['GSc'][home_starter_len-1]
    
    #https://www.baseball-reference.com/teams/tgl.cgi?team=CHW&t=b&year=2024
    
    away_batting_gamelog = 'https://www.baseball-reference.com/teams/tgl.cgi?team=' + away_team_abbr + '&t=b&year=' + year
    home_batting_gamelog = 'https://www.baseball-reference.com/teams/tgl.cgi?team=' + home_team_abbr + '&t=b&year=' + year
    
    away_batting = pandas.read_html(away_batting_gamelog)[0]
    time.sleep(2)
    away_batting = away_batting.query('Opp != "Opp"')
    away_batting_len = len(away_batting)-1
    
    home_batting = pandas.read_html(home_batting_gamelog)[0]
    time.sleep(2)
    home_batting = home_batting.query('Opp != "Opp"')
    home_batting_len = len(home_batting)-1
    
    preview_html = requests.get('https://www.baseball-reference.com/'+preview).text
    preview_html = preview_html.replace('<!--','')
    preview_df = pandas.read_html(preview_html)
    away_split = preview_df[2]
    away_last7 = away_split.query('Split == "Last 7 GS"')
    home_split = preview_df[5]
    home_last7 = home_split.query('Split == "Last 7 GS"')
    away_last7_outs = int(str(float(away_last7['IP'])).split('.')[0])*3/7 + int(str(float(away_last7['IP'])).split('.')[1])
    home_last7_outs = int(str(float(home_last7['IP'])).split('.')[0])*3/7 + int(str(float(home_last7['IP'])).split('.')[1])
    away_last7_hits = float(away_last7['H'])/7
    home_last7_hits = float(home_last7['H'])/7
    away_last7_runs = float(away_last7['R'])/7
    home_last7_runs = float(home_last7['R'])/7
    away_last7_walks = float(away_last7['BB'])/7
    home_last7_walks = float(home_last7['BB'])/7
    away_last7_so = float(away_last7['SO'])/7
    home_last7_so = float(home_last7['SO'])/7
    away_last7_hr = float(away_last7['HR'])/7
    home_last7_hr = float(home_last7['HR'])/7
    away_last7_gamescore = float(away_last7['GSc'])
    home_last7_gamescore = float(home_last7['GSc'])

    

    print()