package com.dj.scores;
import java.util.*;

import com.dj.odds.fanduelmlb;
import com.dj.scores.model.espnfpi;
import com.dj.scores.model.predictiontrackernfl;

public class App
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("DjScores");

		Scanner input = new Scanner(System.in);

		// System.out.print("Enter away team: ");
//		String team1 = input.next();
		String team1 = "lions";
		// System.out.println("you entered "+team1);
		// System.out.print("Enter home team: ");
//		String team2 = input.next();
		String team2 = "packers";
		// System.out.println("you entered "+team2);
		// System.out.println("Enter the week" );/
		// String week = input.next();
		String week = "4";
		// System.out.println("you entered "+6);
		// nfl.getNfl(team1, team2, week);
		// nba.getnba();
		// nba.getsrs();
		// nba.spread();
		// nba.fteProbability();
		mlb.getmlb();

		// mlb.getBaseballReferenceMatchups();

		// BradleyTerry.runBradleyTerry();
		
		// Util.scrapeMlbRefGames();
		
		espnfpi.runespnfpi();
		// predictiontrackernfl.getnfl();
	}
}

