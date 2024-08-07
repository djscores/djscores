from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import pandas
import numpy
import json
import requests
from bs4 import BeautifulSoup, SoupStrainer
import time
import re
import warnings
import sys
import traceback
# from mlbespn import todays_games

warnings.filterwarnings("ignore")
url = 'https://www.covers.com/sports/mlb/matchups?selectedDate=2024-07-08'
page = requests.get(url)
soup = BeautifulSoup(page.content, 'html.parser')
# matchup_pattern = re.compile("mlb-matchup-link")
# matchups = BeautifulSoup.find_all('a', {'data-linkcont': matchup_pattern})
matchup_sections = soup.find_all('div',{'class': 'cmg_matchup_game_box cmg_game_data'})
# espn_games = todays_games()
pitchers = []
pitchersdf = pandas.DataFrame(columns=['away_name','away_last5_score','away_last5_outs','away_last5_so','away_last5_hits','away_last5_walks','away_last5_runs','away_last5_hr',
                                       'home_name','home_last5_score','home_last5_outs','home_last5_so','home_last5_hits','home_last5_walks','home_last5_runs','home_last5_hr',
                                       'away_last10_outs','away_last10_so','away_last10_hits','away_last10_walks','away_last10_runs','away_last10_hr',
                                       'home_last10_outs','home_last10_so','home_last10_hits','home_last10_walks','home_last10_runs','home_last10_hr'])
try:
    for matchup_section in matchup_sections:
        # teams = matchup_section.find('div',{'class': 'cmg_matchup_header_team_names'})
        # team_list = teams.text.split(" at ")
        # print(teams.text.strip())
        teams = matchup_section.select("a[href*=teams]")
        away_team = teams[0]['href'].split("/")[6].replace("-"," ")
        home_team = teams[1]['href'].split("/")[6].replace("-"," ")
        print(away_team + ' at ' + home_team)
        probables = matchup_section.find_all('div',{'class': 'cmg_l_col cmg_l_span_6 cmg_team_starting_pitcher'})
        
        # https://www.covers.com/sport/player-props/matchup/300239?propEvent=MLB_GAME_PLAYER_PITCHER_STRIKEOUTS&countryCode=US&stateProv=Ohio&isLeagueVersion=False
        
        matchup = matchup_section.select("a[data-linkcont*=score-mlb-matchup-link]")
        if matchup:
            href = matchup[0].attrs.get('href')
        matchup_url = 'https://www.covers.com'+href
        print(matchup_url)
        matchup_id = matchup_url.split('/')[7]
        pitcher_strikeouts_url = 'https://www.covers.com/sport/player-props/matchup/' + matchup_id + '?propEvent=MLB_GAME_PLAYER_PITCHER_STRIKEOUTS&countryCode=US&stateProv=Ohio&isLeagueVersion=False'
        pitcher_strikeouts_page = requests.get(pitcher_strikeouts_url)
        soup = BeautifulSoup(pitcher_strikeouts_page.content, 'lxml')
        pitcher_a = soup.select('a[class*=player-link-modal]')

        away_probable = ''
        home_probable = ''
        away_probable_name = ''
        home_probable_name = ''
        away_probable_stats = ''
        home_probable_stats = ''
        away_probable_odds = ''
        home_probable_odds = ''
        away_probable_gameScore = ''
        home_probable_gameScore = ''
        away_probable_outs_odds = ''
        away_probable_so_odds = ''
        away_probable_hits_odds = ''
        away_probable_walks_odds = ''
        away_probable_runs_odds = ''
        home_probable_outs_odds = ''
        home_probable_so_odds = ''
        home_probable_hits_odds = ''
        home_probable_walks_odds = ''
        home_probable_runs_odds = ''
                
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
                away_probable_page = requests.get(away_probable_href)
                away_probable_soup = BeautifulSoup(away_probable_page.content, 'lxml')
                away_probable_tables = pandas.read_html(away_probable_page.text, header=0)
                away_probable_id = away_probable_href.split('/')[7]
            if len(away_probable_tables) > 2:
                previous_avg = away_probable_tables[0]
                for table in away_probable_tables:
                    if 'IP' in table.columns:
                        previous_avg = table
                        break
                
                probable_len = len(previous_avg)-1
                away_probable_pitcher_outs = previous_avg['Outs'][probable_len]
                try:
                    outs_div = away_probable_soup.find('section', {'id':"outs-recorded"})
                    if outs_div:
                        odds = outs_div.find_all('div', {'class':"odds upper-block"})
                        odds_over = odds[0].text.split(' ')
                        odds_over_float = float(odds_over[0][2:])
                        outs_odds_over = previous_avg.query('Outs > ' + str(odds_over_float))
                        outs_odds_over = outs_odds_over[~outs_odds_over['Date'].str.contains('Last')]
                        away_probable_outs_odds = str(odds_over_float) + ' ' + str(len(outs_odds_over))
                except Exception as e:
                    message = e
                    # print(e)

                away_probable_pitcher_so = previous_avg['SO'][probable_len]
                so_div = away_probable_soup.find('section', {'id':"strikeouts-thrown"})
                try:
                    if so_div:
                        odds = so_div.find_all('div', {'class':"odds upper-block"})
                        odds_over = odds[0].text.split(' ')
                        odds_over_float = float(odds_over[0][2:])
                        so_odds_over = previous_avg.query('SO > ' + str(odds_over_float))
                        so_odds_over = so_odds_over[~so_odds_over['Date'].str.contains('Last')]
                        away_probable_so_odds = str(odds_over_float) + ' ' + str(len(so_odds_over))
                except Exception as e:
                    message = e
                    # print(e)

                away_probable_pitcher_hits = previous_avg['H'][probable_len]
                hits_div = away_probable_soup.find('section', {'id':"hits-allowed"})
                
                try:
                    if hits_div:
                        hits = hits_div.find_all('div', {'class':"odds upper-block"})
                        odds_over = hits[0].text.split(' ')
                        odds_over_float = float(odds_over[0][2:])
                        hits_odds_over = previous_avg.query('H > ' + str(odds_over_float))
                        hits_odds_over = hits_odds_over[~hits_odds_over['Date'].str.contains('Last')]
                        away_probable_hits_odds = str(odds_over_float) + ' ' + str(len(hits_odds_over))
                except Exception as e:
                    message = e
                    # print(e)
                

                away_probable_pitcher_walks = previous_avg['BB'][probable_len]
                walks_div = away_probable_soup.find('section', {'id':"walks-allowed"})
                
                try:
                    if walks_div:
                        walks = walks_div.find_all('div', {'class':"odds upper-block"})
                        odds_over = walks[0].text.split(' ')
                        odds_over_float = float(odds_over[0][2:])
                        walks_odds_over = previous_avg.query('BB > ' + str(odds_over_float))
                        walks_odds_over = walks_odds_over[~walks_odds_over['Date'].str.contains('Last')]
                        away_probable_walks_odds = str(odds_over_float) + ' ' + str(len(walks_odds_over))
                except Exception as e:
                    message = e
                
                away_probable_pitcher_runs = previous_avg['ER'][probable_len]
                runs_div = away_probable_soup.find('section', {'id':"earned-runs-allowed"})
                
                try:
                    if runs_div:
                        runs = runs_div.find_all('div', {'class':"odds upper-block"})
                        odds_over = runs[0].text.split(' ')
                        odds_over_float = float(odds_over[0][2:])
                        runs_odds_over = previous_avg.query('ER > ' + str(odds_over_float))
                        runs_odds_over = runs_odds_over[~runs_odds_over['Date'].str.contains('Last')]
                        away_probable_runs_odds = str(odds_over_float) + ' ' + str(len(runs_odds_over))
                except Exception as e:
                    message = e
                
                away_probable_pitcher_hr = previous_avg['HR'][probable_len]
                away_probable_pitcher_lastavg = previous_avg['Date'][probable_len]
                away_gameScore = 47.5 + away_probable_pitcher_so + (away_probable_pitcher_outs*1.5) - (away_probable_pitcher_walks*2) - (away_probable_pitcher_hits*2) - (away_probable_pitcher_runs*3) - (away_probable_pitcher_hr * 4)
                away_probable_gameScore = str("{0:.1f}".format(away_gameScore))
                away_probable_stats =  away_probable_pitcher_lastavg + ' outs ' + str(away_probable_pitcher_outs) + ' so ' + str(away_probable_pitcher_so) + ' hits ' +  str(away_probable_pitcher_hits) + ' walks ' + str(away_probable_pitcher_walks) + ' runs ' + str(away_probable_pitcher_runs) + ' hr ' + str(away_probable_pitcher_hr)
                away_probable_odds = away_probable_pitcher_lastavg + ' odds - outs ' + away_probable_outs_odds + ' so ' + away_probable_so_odds + ' hits ' + away_probable_hits_odds + ' walks ' + away_probable_walks_odds + ' runs ' + away_probable_runs_odds
            else:
                print('TBD')
                # for game in espn_games:
                #     if team_list[0] in game[0].lower():
                #         espn_pitcher_href = game[4]
                #         print(espn_pitcher_href)
            if home_probable:
                home_probable_href = home_probable.attrs.get('href')
                home_probable_page = requests.get(home_probable_href)
                home_probable_soup = BeautifulSoup(home_probable_page.content, 'lxml')
                home_probable_page = pandas.read_html(home_probable_page.text, header=0)
                
                if len(home_probable_page) > 2:
                    home_previous_avg = home_probable_page[0]
                    for table in home_probable_page:
                        if 'IP' in table.columns:
                            home_previous_avg = table
                            break

                home_probable_len = len(home_previous_avg)-1
                try:
                    home_probable_pitcher_outs = home_previous_avg['Outs'][home_probable_len]
                
                    try:
                        outs_div = home_probable_soup.find('section', {'id':"outs-recorded"})
                        if outs_div:
                            odds = outs_div.find_all('div', {'class':"odds upper-block"})
                            odds_over = odds[0].text.split(' ')
                            odds_over_float = float(odds_over[0][2:])
                            outs_odds_over = home_previous_avg.query('Outs > ' + str(odds_over_float))
                            outs_odds_over = outs_odds_over[~outs_odds_over['Date'].str.contains('Last')]
                            home_probable_outs_odds = str(odds_over_float) + ' ' + str(len(outs_odds_over))
                    except Exception as e:
                        message = e
                        # print(e)

                    home_probable_pitcher_so = home_previous_avg['SO'][home_probable_len]
                    so_div = home_probable_soup.find('section', {'id':"strikeouts-thrown"})
                    
                    try:
                        if so_div:
                            odds = so_div.find_all('div', {'class':"odds upper-block"})
                            odds_over = odds[0].text.split(' ')
                            odds_over_float = float(odds_over[0][2:])
                            so_odds_over = home_previous_avg.query('SO > ' + str(odds_over_float))
                            so_odds_over = so_odds_over[~so_odds_over['Date'].str.contains('Last')]
                            home_probable_so_odds = str(odds_over_float) + ' ' + str(len(so_odds_over))
                    except Exception as e:
                        message = e
                        # print(e)

                    home_probable_pitcher_hits = home_previous_avg['H'][home_probable_len]
                    hits_div = home_probable_soup.find('section', {'id':"hits-allowed"})
                    
                    try:
                        if hits_div:
                            hits = hits_div.find_all('div', {'class':"odds upper-block"})
                            odds_over = hits[0].text.split(' ')
                            odds_over_float = float(odds_over[0][2:])
                            hits_odds_over = home_previous_avg.query('H > ' + str(odds_over_float))
                            hits_odds_over = hits_odds_over[~hits_odds_over['Date'].str.contains('Last')]
                            home_probable_hits_odds = str(odds_over_float) + ' ' + str(len(hits_odds_over))
                    except Exception as e:
                        message = e
                        # print(e)
                    
                    home_probable_pitcher_walks = home_previous_avg['BB'][home_probable_len]
                    walks_div = home_probable_soup.find('section', {'id':"walks-allowed"})
                    
                    try:
                        if walks_div:
                            walks = walks_div.find_all('div', {'class':"odds upper-block"})
                            odds_over = walks[0].text.split(' ')
                            odds_over_float = float(odds_over[0][2:])
                            walks_odds_over = home_previous_avg.query('BB > ' + str(odds_over_float))
                            walks_odds_over = walks_odds_over[~walks_odds_over['Date'].str.contains('Last')]
                            home_probable_walks_odds = str(odds_over_float) + ' ' + str(len(walks_odds_over))
                    except Exception as e:
                        message = e
                    
                    home_probable_pitcher_runs = home_previous_avg['ER'][home_probable_len]
                    runs_div = home_probable_soup.find('section', {'id':"earned-runs-allowed"})
                    
                    try:
                        if runs_div:
                            runs = runs_div.find_all('div', {'class':"odds upper-block"})
                            odds_over = runs[0].text.split(' ')
                            odds_over_float = float(odds_over[0][2:])
                            runs_odds_over = home_previous_avg.query('ER > ' + str(odds_over_float))
                            runs_odds_over = runs_odds_over[~runs_odds_over['Date'].str.contains('Last')]
                            home_probable_runs_odds = str(odds_over_float) + ' ' + str(len(runs_odds_over))
                    except Exception as e:
                        message = e
                    
                    home_probable_pitcher_hr = home_previous_avg['HR'][home_probable_len]

                    if 'Date' in table.columns:
                        probable_pitcher_lastavg = home_previous_avg['Date'][home_probable_len]
                    gameScore = 47.5 + home_probable_pitcher_so + (home_probable_pitcher_outs*1.5) - (home_probable_pitcher_walks*2) - (home_probable_pitcher_hits*2) - (home_probable_pitcher_runs*3) - (home_probable_pitcher_hr * 4)
                    home_probable_gameScore = str("{0:.1f}".format(gameScore))
                    home_probable_stats = probable_pitcher_lastavg + ' outs ' + str(home_probable_pitcher_outs) + ' so ' + str(home_probable_pitcher_so) + ' hits ' +  str(home_probable_pitcher_hits) + ' walks ' + str(home_probable_pitcher_walks) + ' runs ' + str(home_probable_pitcher_runs) + ' hr ' + str(home_probable_pitcher_hr)
                    home_probable_odds = probable_pitcher_lastavg + ' odds - outs ' + home_probable_outs_odds + ' so ' + home_probable_so_odds + ' hits ' + home_probable_hits_odds + ' walks ' + home_probable_walks_odds + ' runs ' + home_probable_runs_odds
                except Exception as e:
                    message = e
        headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36',
        "Content-Type": "application/x-www-form-urlencoded"}
    
        matchup_props_url = matchup_url + '/props'
        # matchup_props_page = requests.get(matchup_props_url, headers=headers)
        # matchup_props = BeautifulSoup(matchup_props_page.content, 'html.parser')
        
        
        # https://www.covers.com/sport/player-props/matchup/300239?propEvent=MLB_GAME_PLAYER_RBIS&countryCode=US&stateProv=Ohio&isLeagueVersion=Falsef

        # page = requests.get(url)
        # driver.get(matchup_url)
        time.sleep(2)
        # driver.execute_script("window.scrollTo(0, 1000);")
        
        # matchup_BeautifulSoup = BeautifulSoup(page.content, 'html.parser')    
        
        # baseball_reference = 'https://www.baseball-reference.com/leagues/MLB-standings.shtml#expanded_standings_overall'
        # baseball_ref_previews = 'https://www.baseball-reference.com/previews/'

        # baseball_reference_page = requests.get(baseball_ref_previews)
        # baseball_reference = BeautifulSoup(baseball_reference_page.content, 'html.parser')
       
        
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
        print(away_probable_gameScore + ' ' + away_probable_stats)
        print(away_probable_odds)
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
        print(home_probable_gameScore + ' ' + home_probable_stats)
        print(home_probable_odds)
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
        
        pitching_last10 = pandas.read_html(pitching_url_last10)
        away_relievers_last10 = pitching_last10[2]['Runs/9'][0]
        home_relievers_last10 = pitching_last10[2]['Runs/9'][1]
        time.sleep(2)
        hitting_last10 = pandas.read_html(hitting_url_last10)
        away_hitting_last10 = hitting_last10[0]['Runs/9'][0]
        home_hitting_last10 = hitting_last10[0]['Runs/9'][1]
        time.sleep(2)
        hitting_throwhand_url_last10 = pandas.read_html(hitting_throwhand_url_last10)
        away_hitting_throwhand_url_last10 = hitting_throwhand_url_last10[0]['Runs/9'][0]
        home_hitting_throwhand_url_last10 = hitting_throwhand_url_last10[0]['Runs/9'][1]
        time.sleep(2)
        
        pitching_last5 = pandas.read_html(pitching_url_last5)
        away_relievers_last5 = pitching_last5[2]['Runs/9'][0]
        home_relievers_last5 = pitching_last5[2]['Runs/9'][1]
        time.sleep(2)
        hitting_last5 = pandas.read_html(hitting_url_last5)
        away_hitting_last5 = hitting_last5[0]['Runs/9'][0]
        home_hitting_last5 = hitting_last5[0]['Runs/9'][1]
        time.sleep(2)
        hitting_throwhand_url_last5 = pandas.read_html(hitting_throwhand_url_last5)
        away_hitting_throwhand_url_last5 = hitting_throwhand_url_last5[0]['Runs/9'][0]
        home_hitting_throwhand_url_last5 = hitting_throwhand_url_last5[0]['Runs/9'][1]
        time.sleep(2)

        pitching_overall = pandas.read_html(pitching_url_overall)
        away_relievers_overall = pitching_overall[2]['Runs/9'][0]
        home_relievers_overall = pitching_overall[2]['Runs/9'][1]
        time.sleep(2)
        hitting_overall = pandas.read_html(hitting_url_overall)
        away_hitting_overall = hitting_overall[0]['Runs/9'][0]
        home_hitting_overall = hitting_overall[0]['Runs/9'][1]
        time.sleep(2)
        hitting_throwhand_overall = pandas.read_html(hitting_throwhand_url_overall)
        away_hitting_throwhand_overall = hitting_throwhand_overall[0]['Runs/9'][0]
        home_hitting_throwhand_overall = hitting_throwhand_overall[0]['Runs/9'][1]
        
        # time.sleep(2)

        print()

        
    # with open("covers.json","w") as jsonFile:
    #     json.dump(data,jsonFile,indent=4)
except Exception as e:
    traceback.print_exc()
    print(e)
    pass