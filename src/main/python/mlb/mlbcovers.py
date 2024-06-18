from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import pandas
import numpy
import json
import requests
from bs4 import BeautifulSoup as soup
import time
import re
# from mlbespn import todays_games

url = 'https://www.covers.com/sports/mlb/matchups?selectedDate=2024-06-18'
page = requests.get(url)
soup = soup(page.content, 'html.parser')
# matchup_pattern = re.compile("mlb-matchup-link")
# matchups = soup.find_all('a', {'data-linkcont': matchup_pattern})
matchup_sections = soup.find_all('div',{'class': 'cmg_matchup_game_box cmg_game_data'})
# espn_games = todays_games()
pitchers = []
pitchersdf = pandas.DataFrame(columns=['away_name','away_last5_score','away_last5_outs','away_last5_so','away_last5_hits','away_last5_walks','away_last5_runs','away_last5_hr',
                                       'home_name','home_last5_score','home_last5_outs','home_last5_so','home_last5_hits','home_last5_walks','home_last5_runs','home_last5_hr',
                                       'away_last10_outs','away_last10_so','away_last10_hits','away_last10_walks','away_last10_runs','away_last10_hr',
                                       'home_last10_outs','home_last10_so','home_last10_hits','home_last10_walks','home_last10_runs','home_last10_hr'])
for matchup_section in matchup_sections:
    teams = matchup_section.find('div',{'class': 'cmg_matchup_header_team_names'})
    team_list = teams.text.split(" at ")
    print(teams.text.strip())
    probables = matchup_section.find_all('div',{'class': 'cmg_l_col cmg_l_span_6 cmg_team_starting_pitcher'})
    
    away_probable = ''
    home_probable = ''
    away_probable_name = ''
    home_probable_name = ''
    away_probable_stats = ''
    home_probable_stats = ''

    if len(probables) > 0:
        away_probable = probables[0].find('a')
        home_probable = probables[1].find('a')

        if away_probable:
            away_probable_name = away_probable.text.strip()
        else:
            away_probable = ''
        if home_probable:
            home_probable_name = home_probable.text.strip()
        else:
            home_probable = ''
    
        if away_probable:
            away_probable_href = away_probable.attrs.get('href')
            
            try:
                away_probable_page = pandas.read_html(away_probable_href)
                away_probable_id = away_probable_href.split('/')[7]

            except:
                print(away_probable_name)
            if len(away_probable_page) > 2:
                previous_avg = away_probable_page[0]
                for table in away_probable_page:
                    if 'IP' in table.columns:
                        previous_avg = table
                        break
            
            probable_len = len(previous_avg)-1
            probable_ip = previous_avg['IP'][len(previous_avg)-1]
            probable_ip_str = str(probable_ip)
            away_probable_outs = probable_ip_str.split(".")
            try:
                away_probable_outs_float = float(probable_outs[0])*3+int(probable_outs[1])
            except:
                no_games = 'No games played so far this season'
            away_probable_pitcher_hits = previous_avg['H'][probable_len]
            away_probable_pitcher_walks = previous_avg['BB'][probable_len]
            away_probable_pitcher_runs = previous_avg['R'][probable_len]
            away_probable_pitcher_so = previous_avg['SO'][probable_len]
            away_probable_pitcher_hr = previous_avg['HR'][probable_len]
            away_probable_pitcher_lastavg = previous_avg['Date'][probable_len]
            away_gameScore = 47.5 + away_probable_pitcher_so + (away_probable_outs_float*1.5) - (away_probable_pitcher_walks*2) - (away_probable_pitcher_hits*2) - (away_probable_pitcher_runs*3) - (away_probable_pitcher_hr * 4)
            away_probable = str("{0:.1f}".format(away_gameScore))
            away_probable_stats =  away_probable_pitcher_lastavg + ' outs ' + str(away_probable_outs_float) + ' so ' + str(away_probable_pitcher_so) + ' hits ' +  str(away_probable_pitcher_hits) + ' walks ' + str(away_probable_pitcher_walks) + ' runs ' + str(away_probable_pitcher_runs) + ' hr ' + str(away_probable_pitcher_hr)
        else:
            print('TBD')
            # for game in espn_games:
            #     if team_list[0] in game[0].lower():
            #         espn_pitcher_href = game[4]
            #         print(espn_pitcher_href)
        if home_probable:
            home_probable_href = home_probable.attrs.get('href')
            home_probable_page = pandas.read_html(home_probable_href)
            
            if len(home_probable_page) > 2:
                previous_avg = home_probable_page[0]
                for table in home_probable_page:
                    if 'IP' in table.columns:
                        previous_avg = table
                        break

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
            
            if 'Date' in table.columns:
                probable_pitcher_lastavg = previous_avg['Date'][probable_len]
            gameScore = 47.5 + probable_pitcher_so + (probable_outs_float*1.5) - (probable_pitcher_walks*2) - (probable_pitcher_hits*2) - (probable_pitcher_runs*3) - (probable_pitcher_hr * 4)
            home_probable = str("{0:.1f}".format(gameScore))
            home_probable_stats = probable_pitcher_lastavg + ' outs ' + str(probable_outs_float) + ' so ' + str(probable_pitcher_so) + ' hits ' +  str(probable_pitcher_hits) + ' walks ' + str(probable_pitcher_walks) + ' runs ' + str(probable_pitcher_runs) + ' hr ' + str(probable_pitcher_hr)

    matchup = matchup_section.select("a[data-linkcont*=score-mlb-matchup-link]")
    if matchup:
        href = matchup[0].attrs.get('href')
    matchup_url = 'https://www.covers.com'+href
    print(matchup_url)
    
    headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36',
    "Content-Type": "application/x-www-form-urlencoded"}
 
    matchup_props_url = matchup_url + '/props'
    matchup_id = matchup_url.split('/')[7]
    # matchup_props_page = requests.get(matchup_props_url, headers=headers)
    # matchup_props = soup(matchup_props_page.content, 'html.parser')
    
    # https://www.covers.com/sport/player-props/matchup/300239?propEvent=MLB_GAME_PLAYER_PITCHER_STRIKEOUTS&countryCode=US&stateProv=Ohio&isLeagueVersion=False
    
    pitcher_strikeouts_url = 'https://www.covers.com/sport/player-props/matchup/' + matchup_id + '?propEvent=MLB_GAME_PLAYER_PITCHER_STRIKEOUTS&countryCode=US&stateProv=Ohio&isLeagueVersion=False'
    pitcher_strikeouts_page = requests.get(pitcher_strikeouts_url, headers=headers)
    pitcher_strikeouts_page_text = '<html><head></head><body>' + pitcher_strikeouts_page.text + '</body></html>'
    pitcher_strikeouts = soup(pitcher_strikeouts_page_text, 'html.parser')
    # driver = webdriver.Chrome()
    # driver.get(pitcher_strikeouts_url)
    # time.sleep(5)
    # html = driver.page_source
    # pitcher_strikeouts = soup(html, 'html.parser')
    # driver.quit()
    # pitchers = pitcher_strikeouts.find_all('a', {'class':"player-link"})

    # https://www.covers.com/sport/player-props/matchup/300239?propEvent=MLB_GAME_PLAYER_RBIS&countryCode=US&stateProv=Ohio&isLeagueVersion=Falsef

    # page = requests.get(url)
    # driver.get(matchup_url)
    time.sleep(5)
    # driver.execute_script("window.scrollTo(0, 1000);")
    
    # matchup_soup = soup(page.content, 'html.parser')    
    
    matchup = pandas.read_html(matchup_url)

    away_table_index = 0
    away_pitcher_last5 = matchup[0]  
    for table in matchup:
        if 'IP' in table.columns:
            away_pitcher_last5 = table
            break
        away_table_index = away_table_index + 1
    
    # print(away_pitcher_last5)
    
    print(away_probable_name)
    print(away_probable + ' ' + away_probable_stats)
    if 'H' in away_pitcher_last5.columns:
        away_last5_len = len(away_pitcher_last5)-1
        away_last5_ip = away_pitcher_last5['IP'][away_last5_len]
        away_last5_ip_str = str(away_last5_ip)
        away_last5_outs = away_last5_ip_str.split(".")
        if len(away_last5_outs[1]) > 0:
            away_last5_outs_float = float(away_last5_outs[0])*3+int(away_last5_outs[1])
            away_last5_hits = away_pitcher_last5['H'][away_last5_len]
            away_last5_walks = away_pitcher_last5['BB'][away_last5_len]
            away_last5_runs = away_pitcher_last5['R'][away_last5_len]
            away_last5_so = away_pitcher_last5['SO'][away_last5_len]
            away_last5_hr = away_pitcher_last5['HR'][away_last5_len]
            away_last5_gameScore = 47.5 + away_last5_so + (away_last5_outs_float*1.5) - (away_last5_walks*2) - (away_last5_hits*2) - (away_last5_runs*3) - (away_last5_hr * 4)
            
            # print("{:.1f}".format(away_last5_gameScore))
            away_last5_text = str("{0:.1f}".format(away_last5_gameScore))
            print(away_last5_text + ' Last 5 Avg ' + ' outs ' + str(away_last5_outs_float) + ' so ' + str(away_last5_so) + ' hits ' +  str(away_last5_hits) + ' walks ' + str(away_last5_walks) + ' runs ' + str(away_last5_runs) + ' hr ' + str(away_last5_hr))

    home_pitcher_last5 = matchup[away_table_index+2]
    # for table in matchup:
    #     if 'IP' in table.columns:
    #         home_pitcher_last5 = table
    #         break

    # print(home_pitcher_last5)
    print(home_probable_name)
    print(home_probable + ' ' + home_probable_stats)
    if 'H' in home_pitcher_last5.columns:
        home_last5_len = len(home_pitcher_last5)-1
        home_last5_ip = home_pitcher_last5['IP'][home_last5_len]
        home_last5_ip_str = str(home_last5_ip)
        home_last5_outs = home_last5_ip_str.split(".")
        if len(home_last5_outs[1]) > 0:
            home_last5_outs_float = float(home_last5_outs[0])*3+int(home_last5_outs[1])
            home_last5_hits = home_pitcher_last5['H'][home_last5_len]
            home_last5_walks = home_pitcher_last5['BB'][home_last5_len]
            home_last5_runs = home_pitcher_last5['R'][home_last5_len]
            home_last5_so = home_pitcher_last5['SO'][home_last5_len]
            home_last5_hr = home_pitcher_last5['HR'][home_last5_len]
            home_last5_gameScore = 47.5 + home_last5_so + (home_last5_outs_float*1.5) - (home_last5_walks*2) - (home_last5_hits*2) - (home_last5_runs*3) - (home_last5_hr * 4)
            
            # print("{:.1f}".format(home_last5_gameScore))
            home_last5_text = str("{0:.1f}".format(home_last5_gameScore))
            print(home_last5_text + ' Last 5 Avg ' + ' outs ' + str(home_last5_outs_float) + ' so ' + str(home_last5_so) + ' hits ' +  str(home_last5_hits) + ' walks ' + str(home_last5_walks) + ' runs ' + str(home_last5_runs) + ' hr ' + str(home_last5_hr))
    print()                                                                                                                                        

    time.sleep(2)

    pitching_url_last10 = 'https://www.covers.com/'+href+'/stats-analysis/pitching/last10'
    hitting_url_last10 = 'https://www.covers.com/'+href+'/stats-analysis/hitting/last10'
    hitting_throwhand_url_last10 = 'https://www.covers.com/'+href+'/stats-analysis/hittingvsstarterthrowhand/last10'
    pitching_url_last5 = 'https://www.covers.com/'+href+'/stats-analysis/pitching/last5'
    hitting_url_last5 = 'https://www.covers.com/'+href+'/stats-analysis/hitting/last5'
    hitting_throwhand_url_last5 = 'https://www.covers.com/'+href+'/stats-analysis/hittingvsstarterthrowhand/last5'
    pitching_url_overall = 'https://www.covers.com/'+href+'/stats-analysis/pitching/overall'
    hitting_url_overall = 'https://www.covers.com/'+href+'/stats-analysis/hitting/overall'
    hitting_throwhand_url_overall = 'https://www.covers.com/'+href+'/stats-analysis/hittingvsstarterthrowhand/overall'
    baseball_reference = 'https://www.baseball-reference.com/leagues/MLB-standings.shtml'
    
    pitching_last10 = pandas.read_html(pitching_url_last10)
    away_relievers_last10 = pitching_last10[2]['Runs/9'][0]
    home_relievers_last10 = pitching_last10[2]['Runs/9'][1]
    time.sleep(2)
    hitting_last10 = pandas.read_html(hitting_url_last10)
    time.sleep(2)
    hitting_throwhand_url_last10 = pandas.read_html(hitting_throwhand_url_last10)
    time.sleep(2)
    


    # pitching_last5 = pandas.read_html(pitching_url_last10)
    # time.sleep(5)
    # hitting_last5 = pandas.read_html(hitting_url_last10)
    # time.sleep(5)
    # hitting_throwhand_url_last5 = pandas.read_html(hitting_throwhand_url_last10)
    # time.sleep(5)

    # pitching_overall = pandas.read_html(pitching_url_last10)
    # time.sleep(5)
    # hitting_overall = pandas.read_html(hitting_url_last10)
    # time.sleep(5)
    # hitting_throwhand_url_overall = pandas.read_html(hitting_throwhand_url_last10)
    # time.sleep(5)

    # baseball_reference_page = pandas.read_html(baseball_reference)

    # relievers_last10 = pitching_url_last10[2]

    
# with open("covers.json","w") as jsonFile:
#     json.dump(data,jsonFile,indent=4)