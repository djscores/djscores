package com.dj.scores;
import java.text.DecimalFormat;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.util.*;

public class nfl {
    public static void getNfl(String team1, String team2, String week) {
		try
		{
            int weekInt = Integer.parseInt(week);
			String url = "https://www.pro-football-reference.com/years/2022/games.htm";
			Document doc = Jsoup.connect(url).get();
			Element table = doc.select("table").get(0);
			Elements rows = table.select("tr");
			HashMap<String,Double[]> teams = new HashMap<String,Double[]>();
			HashMap<String,ArrayList<String>> allOpponents = new HashMap<String,ArrayList<String>>();
			ArrayList<String[]> games = new ArrayList<String[]>();
			// String[][] games = new String[rows.size()][6];
			for (int i = 1; i < rows.size(); i++) { 
				Element row = rows.get(i);
				Elements gameWeek = row.select("th");
				String gameweek = gameWeek.text();
				int gameweekInt = 1;
				try {
					gameweekInt = Integer.parseInt(gameweek);
				}
				catch(NumberFormatException e) {
					continue;
				}
				Elements cols = row.select("td");
				if (cols.size() > 2) {
					String winner = cols.get(3).text();
					String loser = cols.get(5).text();
					String winScore = cols.get(7).text();
					String loseScore = cols.get(8).text();
					String winYards = cols.get(9).text();
					String loseYards = cols.get(11).text();
					if (winner != null) {
						String[] game = new String[7];
						game[0] = winner;
						game[1] = loser;
						game[2] = winScore;
						game[3] = loseScore;
						game[4] = winYards;
						game[5] = loseYards;
						game[6] = gameweek;
						if (gameweekInt <= weekInt) {
						// if (true) {	
							games.add(game);

							// System.out.println(winner + "\t" + loser + "\t" + winScore + "\t"+ loseScore);
							if (winner.toLowerCase().contains(team1.toLowerCase())) {
								team1 = winner;
							}
							if (winner.toLowerCase().contains(team2.toLowerCase())) {
								team2 = winner;
							}
							Util.addToMap(teams,winner,winScore);
							Util.addToMap(teams,loser,loseScore);
							
							ArrayList<String> opponentsWinner = allOpponents.get(winner);
							if (opponentsWinner == null)
							opponentsWinner = new ArrayList<String>();
							opponentsWinner.add(loser);
							allOpponents.put(winner,opponentsWinner);
							ArrayList<String> opponentsLoser = allOpponents.get(loser);
							if (opponentsLoser == null)
							opponentsLoser = new ArrayList<String>();
							opponentsLoser.add(winner);
							allOpponents.put(loser,opponentsLoser);
						}
						else {
							break;
						}
					}
				}
			}
			// Util.printMap(teams);
			// Util.printOpponents(allOpponents, team1);
			// Util.printOpponents(allOpponents, team2);
			// Util.getOpponentAveragePoints(allOpponents, teams, team1);
			Double team1average = 0D;
			Double team1gaveup = 0D;
			Double team1oppAvg = 0D;
			Double team1oppGaveup = 0D;
			// System.out.println("**********");
			// System.out.println("**********"+team1+"**********");
			int i=0;
			for(String key : allOpponents.keySet()) {
				i++;
				if (key.toLowerCase().contains(team1.toLowerCase())) {
					ArrayList<String> opponents = allOpponents.get(key);
					team1average += teams.get(key)[0]/opponents.size();
					// team1gaveup = Util.getOpponentAveragePoints(allOpponents, teams, key);
					team1gaveup = Util.getOpponentAveragePoints(key,games);
					// System.out.println(key + "\t" + team1average + "\t" + team1gaveup);
					int j=0;
					team1oppAvg = 0D;
					team1oppGaveup = 0D;
					if (opponents != null)
					for (String opponent : opponents) {
						j++;
						Double opponentPoints = teams.get(opponent)[0]/allOpponents.get(opponent).size();
						// System.out.println(opponent + "\t" + opponentPoints + "\t" + team1oppGave);
						team1oppAvg += opponentPoints;
						// team1oppGaveup += Util.getOpponentAveragePoints(allOpponents, teams, opponent);
						team1oppGaveup += Util.getOpponentAveragePoints(opponent,games);
						// System.out.println(Util.getOpponentAveragePoints(allOpponents, teams, opponent));
						// System.out.println(j + " " + opponent + " \t average " + "\t" + opponentPoints + " \t gave up \t"+Util.getOpponentAveragePoints(opponent,games));
						// System.out.println(Util.getOpponentAveragePoints(allOpponents, teams, opponent));
					}
					team1oppAvg = team1oppAvg/j;
					team1oppGaveup = team1oppGaveup/j;
				}
			}
			// team1average = team1average/i;
			// System.out.println("team1oppAvg \t"+team1oppAvg + "\t team1oppGaveup \t" + team1oppGaveup);
			
			Double team2average = 0D;
			Double team2gaveup = 0D;
			Double team2oppAvg = 0D;
			Double team2oppGaveup = 0D;
			// System.out.println();
			// System.out.println("**********"+team2+"**********");
			i=0;
			for(String key : allOpponents.keySet()) {
				i++;
				if (key.toLowerCase().contains(team2.toLowerCase())) {
					ArrayList<String> opponents = allOpponents.get(key);
					team2average += teams.get(key)[0]/opponents.size();
					team2gaveup = Util.getOpponentAveragePoints(key,games);
					// System.out.println(key + "\t" + team2average + "\t" + team2gaveup);
					int j=0;
					team2oppAvg = 0D;
					team2oppGaveup = 0D;
					if (opponents != null)
					for (String opponent : opponents) {
						j++;
						Double opponentPoints = teams.get(opponent)[0]/allOpponents.get(opponent).size();
						// System.out.println(opponent + "\t" + opponentPoints + "\t" + team1oppGave);
						team2oppAvg += opponentPoints;
						team2oppGaveup += Util.getOpponentAveragePoints(opponent,games);
						// team2oppGaveup += Util.getOpponentAveragePoints(allOpponents, teams, opponent);
						// System.out.println(Util.getOpponentAveragePoints(opponent,games));
						// System.out.println(j + " " + opponent + " \t average " + "\t" + opponentPoints + " \t gave up \t"+Util.getOpponentAveragePoints(opponent,games));
						// System.out.println(opponentPoints);
					}
					team2oppAvg = team2oppAvg/j;
					team2oppGaveup = team2oppGaveup/j;
				}
			}
			
			// System.out.println("team2oppAvg \t"+team2oppAvg + "\t team2oppGaveup \t" + team2oppGaveup);
			// System.out.println();

			Double team1offensePercentage = team1average / team1oppGaveup;
			// System.out.println(team1 + " offensive percentage - average points scored / opposing teams average allowed");
			// System.out.println("team1average / team1oppGaveup \t" + team1offensePercentage);
			Double team1defensePercentage = team1gaveup / team1oppAvg;
			// System.out.println(team1 + " defensive percentage - average points gave up / average points scored by opposition");
			// System.out.println("team1gaveup / team1oppAvg \t" + team1defensePercentage);
			// System.out.println();
			
			Double team2offensePercentage = team2average / team2oppGaveup;
			// System.out.println(team2 + " offensive percentage - average points scored / opposing teams average allowed");
			// System.out.println("team2average / team2oppGaveup \t" + team2offensePercentage);
			Double team2defensePercentage = team2gaveup / team2oppAvg;
			// System.out.println(team2 + " defensive percentage - average points gave up / average points scored by opposition");
			// System.out.println("team2gaveup / team2oppAvg \t" + team2defensePercentage);
			// System.out.println();
			
			Double team1performance = (team1offensePercentage + team2defensePercentage) / 2;
			// System.out.println(team1 + " performance percentage " + team1performance);
			Double team2performance = (team2offensePercentage + team1defensePercentage) / 2;
			// System.out.println(team2 + " performance percentage " + team2performance);
			// System.out.println();

			Double team1baseOffensive = (team1average + team2gaveup) / 2;
			// System.out.println(team1 + " base offensive number " + team1baseOffensive);
			Double team2baseOffensive = (team2average + team1gaveup) / 2;
			// System.out.println(team2 + " base offensive number " + team2baseOffensive);
			// System.out.println();

			//assuming team1 is away and team2 is home
			DecimalFormat df = new DecimalFormat("0.00");
			Double team1PredictedPoints =  (team1baseOffensive * team1performance) - 1.5;
			System.out.printf("%-20s%-20s\n",team1,df.format(team1PredictedPoints));
			// System.out.println(team1 + " predicted points " + team1PredictedPoints);
			Double team2PredictedPoints = (team2baseOffensive * team2performance) + 1.5;
			System.out.printf("%-20s%-20s\n",team2,df.format(team2PredictedPoints));
			// System.out.println(team2 + " predicted points " + df.format(team2PredictedPoints));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }
}
