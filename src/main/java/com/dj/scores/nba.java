package com.dj.scores;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class nba {
    static HashMap<String,String> teamOffense = new HashMap<String,String>();
    static HashMap<String,String> teamDefense = new HashMap<String,String>();
    static HashMap<String,String> teamUrl = new HashMap<String,String>();
    static ArrayList<String[]> matchupList = new ArrayList<String[]>();
    static ArrayList<String[]> srsTotals = new ArrayList<String[]>();
    static HashMap<String,Double> asrsList = new HashMap<String,Double>();
    static HashMap<String,Double> homeTeamProbabilities = new HashMap<String,Double>();

    public static void getnba() {
        String url = "https://www.covers.com/sports/nba/matchups?selectedDate=2023-06-09";
        try {
            Document doc = Jsoup.connect(url).get();
            
            Elements matchups = doc.getElementsByClass("cmg_btn cmg_btn_primary");
            Elements matchupNames = doc.getElementsByClass("cmg_matchup_header_team_names");

            for (int i = 0; i < matchupNames.size(); i++) { 
                String[] matchupName = matchupNames.get(i).text().split(" at ");
                if (matchupName[0].contains("L.A.")) matchupName[0] = matchupName[0].split(" ")[1];
                if (matchupName[1].contains("L.A.")) matchupName[1] = matchupName[1].split(" ")[1];
                matchupList.add(matchupName);
                System.out.println(matchupNames.get(i).text());
            }
            for (int i = 0; i < matchups.size(); i++) { 
                Element matchup = matchups.get(i);
                String matchupHref = matchup.attr("href");
                System.out.println(matchupHref);
                String matchupurl = "https://www.covers.com"+matchupHref;
                Document matchupdoc = Jsoup.connect(matchupurl).get();
                
                Elements last10 = matchupdoc.getElementsByClass("table covers-CoversMatchups-Table covers-CoversSticky-table have-2ndCol-sticky A-Look-Back-Table");
                Element awayLast10;
                Element homeLast10;
                if (last10.size() > 2) {
                    awayLast10 = last10.get(1);
                    homeLast10 = last10.get(3);
                }
                else {
                    awayLast10 = last10.get(0);
                    homeLast10 = last10.get(1);
                }
                Elements awayLast10tr = awayLast10.select("tr");
                Elements homeLast10tr = homeLast10.select("tr");
                Double awaylast5 = 0D;
                Double awaylast5opp = 0D;
                Double homelast5 = 0D;
                Double homelast5opp = 0D;
                Double awaylast10 = 0D;
                Double awaylast10opp = 0D;
                Double homelast10 = 0D;
                Double homelast10opp = 0D;
                for (int j = 2; j < 12; j ++) {
                    String awayLast10score = awayLast10tr.get(j).select("td").get(2).text().replace(" (OT)","");
                    String[] awayLast10GameScore = awayLast10score.substring(2, awayLast10score.length()).split(" - ");
                    String homeLast10score = homeLast10tr.get(j).select("td").get(2).text().replace(" (OT)","");
                    String[] homeLast10GameScore = homeLast10score.substring(2, homeLast10score.length()).split(" - ");
                    // System.out.println(j + " awayLast10score "+awayLast10score+"\thomeLast10score "+homeLast10score);
                    if (j < 7) {
                        awaylast5 += Integer.parseInt(awayLast10GameScore[0]);
                        awaylast5opp += Integer.parseInt(awayLast10GameScore[1]);
                        homelast5 += Integer.parseInt(homeLast10GameScore[0]);
                        homelast5opp += Integer.parseInt(homeLast10GameScore[1]);
                    }

                    awaylast10 += Integer.parseInt(awayLast10GameScore[0]);
                    awaylast10opp += Integer.parseInt(awayLast10GameScore[1]);
                    homelast10 += Integer.parseInt(homeLast10GameScore[0]);
                    homelast10opp += Integer.parseInt(homeLast10GameScore[1]);
                }
                Double awaylast5off = awaylast5 / 5;
                Double awaylast5def = awaylast5opp / 5;
                Double homelast5off = homelast5 / 5;
                Double homelast5def = homelast5opp / 5;
                Double awaylast10off = awaylast10 / 10;
                Double awaylast10def = awaylast10opp / 10;
                Double homelast10off = homelast10 / 10;
                Double homelast10def = homelast10opp / 10;
                System.out.println("last5avg - \t" + "away avg" + "\t"+"away def"+"\t"+"home avg"+"\t"+"home def");
                System.out.println("last5avg - \t" + awaylast5off + "\t\t"+awaylast5def+"\t\t"+homelast5off+"\t\t"+homelast5def);
                // System.out.println("last10avg - \t" + "awaylast10avg" + "\t"+"awaylast10oppavg"+"\t"+"homelast10avg"+"\t"+"homelast10oppavg");
                System.out.println("last10avg - \t" + awaylast10off + "\t\t"+awaylast10def+"\t\t"+homelast10off+"\t\t"+homelast10def);
                
                Elements shadedRowTable = matchupdoc.getElementsByClass("table covers-CoversMatchups-Table covers-CoversMatchups-saTable covers-CoversSticky-table");
                Elements seasonavg = shadedRowTable.get(0).select("tr").get(2).select("td");

                Double awayOff = Double.parseDouble(seasonavg.get(1).text());
                Double homeDef = Double.parseDouble(seasonavg.get(5).text());
                Double homeOff = Double.parseDouble(seasonavg.get(7).text());
                Double awayDef = Double.parseDouble(seasonavg.get(11).text());

                Double awayOffRating = Double.parseDouble(seasonavg.get(2).text().substring(0, seasonavg.get(2).text().length()-1))/100;
                Double homeDefRating = Double.parseDouble(seasonavg.get(4).text().substring(0, seasonavg.get(4).text().length()-1))/100;
                Double homeOffRating = Double.parseDouble(seasonavg.get(8).text().substring(0, seasonavg.get(8).text().length()-1))/100;
                Double awayDefRating = Double.parseDouble(seasonavg.get(10).text().substring(0, seasonavg.get(10).text().length()-1))/100;

                Double awayPerfRating = (awayOffRating + homeDefRating)/2;
                Double homePerfRating = (homeOffRating + awayDefRating)/2;
                
                Double awayBaseOff = (awayOff + homeDef)/2;
                Double homeBaseOff = (homeOff + awayDef)/2;

                Double awayProjected = awayBaseOff * awayPerfRating;
                Double homeProjected = (homeBaseOff * homePerfRating) + 2.5;

                System.out.println(matchupList.get(i)[0] + "\tat\t" + matchupList.get(i)[1]);
                
                DecimalFormat df = new DecimalFormat("0.00");
                System.out.println("awayProjected - \t" + df.format(awayProjected) + "\t homeProjected - \t" + df.format(homeProjected));
                
                Elements last5avg = shadedRowTable.get(0).select("tr").get(5).select("td");

                Double awayOffRatingLast5 = Double.parseDouble(last5avg.get(2).text().substring(0, last5avg.get(2).text().length()-1))/100;
                Double homeDefRatingLast5 = Double.parseDouble(last5avg.get(4).text().substring(0, last5avg.get(4).text().length()-1))/100;
                Double homeOffRatingLast5 = Double.parseDouble(last5avg.get(8).text().substring(0, last5avg.get(8).text().length()-1))/100;
                Double awayDefRatingLast5 = Double.parseDouble(last5avg.get(10).text().substring(0, last5avg.get(10).text().length()-1))/100;

                Double awayPerfRatingLast5 = (awayOffRatingLast5 + homeDefRatingLast5)/2;
                Double homePerfRatingLast5 = (homeOffRatingLast5 + awayDefRatingLast5)/2;
                
                Double awayBaseOffLast5 = (awaylast5off + homelast5def)/2;
                Double homeBaseOffLast5 = (homelast5off + awaylast5def)/2;

                Double awayLast5Projected = awayBaseOffLast5 * awayPerfRatingLast5;
                Double homeLast5Projected = (homeBaseOffLast5 * homePerfRatingLast5) + 2.5;

                System.out.println("awayLast5Projected - \t" + df.format(awayLast5Projected) + "\t homeLast5Projected - \t" + df.format(homeLast5Projected));
                System.out.println();
                // Thread.sleep(5000L);
            }
            Elements hrefs = doc.getElementsByAttributeValueContaining("href", "linemovement");
            for (int i = 0; i < hrefs.size(); i++) {
                String href = hrefs.get(i).attr("href");
                System.out.println(href);
                Document lineMovementDoc = Jsoup.connect(href).get();
                Elements lineTables = lineMovementDoc.getElementsByClass("table covers-CoversMatchups-Table covers-CoversOdds-lineMovementTable");
                
                Element draftkings = lineTables.get(1);
                Element mgm = lineTables.get(2);
                Element fanduel = lineTables.get(4);
                
                Element draftkingsSpread = lineTables.get(10);
                Element mgmSpread = lineTables.get(11);
                Element fanduelSpread = lineTables.get(13);
                
                Elements dktr = draftkings.select("tr");
                Elements mgmtr = mgm.select("tr");
                Elements fandueltr = fanduel.select("tr");
                
                Elements dkSpreadtr = draftkingsSpread.select("tr");
                Elements mgmSpreadtr = mgmSpread.select("tr");
                Elements fanduelSpreadtr = fanduelSpread.select("tr");
                
                System.out.println("draftkings" + "\t\t\t" + matchupList.get(i)[0] + "\t" + matchupList.get(i)[1]);
                
                Elements dktd = dktr.get(1).select("div");
                System.out.println(dktd.get(0).text() + "\t" + 
                                    dktd.get(1).text() + "\t" + 
                                    dktd.get(4).text());
                dktd = dktr.get(dktr.size()-1).select("div");
                System.out.println(dktd.get(0).text() + "\t" + 
                                    dktd.get(1).text() + "\t" + 
                                    dktd.get(4).text());
                
                System.out.println("mgm" + "\t\t\t\t" + matchupList.get(i)[0] + "\t" + matchupList.get(i)[1]);
                
                Elements mgmtd = mgmtr.get(1).select("div");
                System.out.println(mgmtd.get(0).text() + "\t" + 
                                    mgmtd.get(1).text() + "\t" + 
                                    mgmtd.get(4).text());
                mgmtd = mgmtr.get(mgmtr.size()-1).select("div");
                System.out.println(mgmtd.get(0).text() + "\t" + 
                                    mgmtd.get(1).text() + "\t" + 
                                    mgmtd.get(4).text());

                System.out.println("fanduel" + "\t\t\t\t" + matchupList.get(i)[0] + "\t" + matchupList.get(i)[1]);
                
                Elements fandueltd = fandueltr.get(1).select("div");
                System.out.println(fandueltd.get(0).text() + "\t" + 
                                    fandueltd.get(1).text() + "\t" + 
                                    fandueltd.get(4).text());
                fandueltd = fandueltr.get(fandueltr.size()-1).select("div");
                System.out.println(fandueltd.get(0).text() + "\t" + 
                                    fandueltd.get(1).text() + "\t" + 
                                    fandueltd.get(4).text());
                
                System.out.println("dkSpread" + "\t\t\t" + matchupList.get(i)[0] + "\t\t" + matchupList.get(i)[1]);
                
                dktd = dkSpreadtr.get(1).select("div");
                System.out.println(dktd.get(0).text() + "\t" + 
                                    dktd.get(1).text() + "\t" + 
                                    dktd.get(4).text());
                dktd = dkSpreadtr.get(dkSpreadtr.size()-1).select("div");
                System.out.println(dktd.get(0).text() + "\t" + 
                                    dktd.get(1).text() + "\t" + 
                                    dktd.get(4).text());
                
                System.out.println("mgmSpread" + "\t\t\t" + matchupList.get(i)[0] + "\t\t" + matchupList.get(i)[1]);
                
                mgmtd = mgmSpreadtr.get(1).select("div");
                System.out.println(mgmtd.get(0).text() + "\t" + 
                                    mgmtd.get(1).text() + "\t" + 
                                    mgmtd.get(4).text());
                mgmtd = mgmSpreadtr.get(mgmSpreadtr.size()-1).select("div");
                System.out.println(mgmtd.get(0).text() + "\t" + 
                                    mgmtd.get(1).text() + "\t" + 
                                    mgmtd.get(4).text());

                System.out.println("fanduelSpread" + "\t\t\t" + matchupList.get(i)[0] + "\t\t" + matchupList.get(i)[1]);
                
                fandueltd = fanduelSpreadtr.get(1).select("div");
                System.out.println(fandueltd.get(0).text() + "\t" + 
                                    fandueltd.get(1).text() + "\t" + 
                                    fandueltd.get(4).text());
                fandueltd = fanduelSpreadtr.get(fanduelSpreadtr.size()-1).select("div");
                System.out.println(fandueltd.get(0).text() + "\t" + 
                                    fandueltd.get(1).text() + "\t" + 
                                    fandueltd.get(4).text());

            }
            System.out.println();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getsrs() {
        String url = "https://www.basketball-reference.com/leagues/NBA_2023.html";
        try {
            Document doc = Jsoup.connect(url).get();
            Element advancedTeam = doc.getElementById("advanced-team");
            Elements advancedTeamRows = advancedTeam.select("tr");
            // System.out.println(advancedTeamRows.size());
            for (int i = 2; i < advancedTeamRows.size(); i++) { 
                Element row = advancedTeamRows.get(i);
                Elements teamTds = row.select("td"); 
                String teamName = teamTds.get(0).text();
                String srs = teamTds.get(8).text();
                String wins = teamTds.get(2).text();
                String losses = teamTds.get(3).text();
                // System.out.println(srs + " " + teamName + " " + wins + " " + losses);
                String[] srsTotal = new String[3];
                srsTotal[0] = teamName;
                srsTotal[1] = srs;
                srsTotal[2] = wins + " " + losses;
                srsTotals.add(srsTotal);
            }
            getPlayoffAverage();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getPlayoffAverage() {
        String url = "https://www.basketball-reference.com/playoffs/NBA_2023.html";
        try {
            Document doc = Jsoup.connect(url).get();
            Element advancedTeam = doc.getElementById("per_game-team");
            Elements advancedTeamRows = advancedTeam.select("tr");
            Element row = advancedTeamRows.get(advancedTeamRows.size()-1);
            Elements teamTds = row.select("td"); 
            String leagueAverage = teamTds.get(teamTds.size()-1).text();
            // System.out.println("leagueAverage - "+leagueAverage);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void spread() {
        asrs();
        probability();
        points();
    }
    public static void asrs() {
        Double homeasrs;
        Double awayasrs;
        
        for (String[] matchup : matchupList) {
            for (String[] total : srsTotals) {
                if (total[0].contains(matchup[1])) {
                    String[] record = total[2].split(" ");
                    int homeTeamGamesPlayed = Integer.parseInt(record[0]) + Integer.parseInt(record[1]);
                    homeasrs = homeTeamGamesPlayed * Double.parseDouble(total[1]) / (homeTeamGamesPlayed + 12);
                    // System.out.println(matchup[1] + " "  +  " " + homeasrs);
                    asrsList.put(matchup[1],homeasrs);
                }
                if (total[0].contains(matchup[0])) {
                    String[] record = total[2].split(" ");
                    int awayTeamGamesPlayed = Integer.parseInt(record[0]) + Integer.parseInt(record[1]);
                    awayasrs = awayTeamGamesPlayed * Double.parseDouble(total[1]) / (awayTeamGamesPlayed + 12);
                    // System.out.println(matchup[0] + " "  +  " " + awayasrs);
                    asrsList.put(matchup[0],awayasrs);
                }
            }
        }
    }
    public static void probability() {
        for (String[] matchup : matchupList) {
            // System.out.print(matchup[0] + " @ " + matchup[1] + " ");
            Double homeasrs = asrsList.get(matchup[1]);
            Double awayasrs = asrsList.get(matchup[0]);
            Double dsrs = homeasrs - awayasrs;
            Double exponent =-(0.613230+0.167546*dsrs);
            Double probabilityHome = 1 / (1 + Math.exp(exponent));
            // System.out.println(probabilityHome);
            homeTeamProbabilities.put(matchup[1],probabilityHome);
        }
    }
    public static void fteProbability() {
        String url = "https://projects.fivethirtyeight.com/2023-nba-predictions/games/";
        try {
            Document doc = Jsoup.connect(url).get();
            // Element dataDate = doc.getElementById("data-date");
            Elements aways = doc.getElementsByClass("tr team away");
            Elements homes = doc.getElementsByClass("tr team home");
            for (int i = 0; i < aways.size(); i++) {
                Element away = aways.get(i);
                Element home = homes.get(i);
                Elements awaytd = away.select("td");
                Elements hometd = home.select("td");
                // System.out.print(awaytd.get(2).text()+ "\t");
                // System.out.print(awaytd.get(4).attr("data-number")+ "\t");
                // System.out.print(hometd.get(2).text()+ "\t");
                // System.out.println(hometd.get(4).attr("data-number"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void points() {
        for (String homeTeam : homeTeamProbabilities.keySet()) {
            Double probability = homeTeamProbabilities.get(homeTeam);
            Double logbase = (1/probability) - 1;
            Double spread = Math.log(logbase) / .13959D;
            System.out.println(homeTeam + " " + spread.toString().substring(0,5));
        }
    }
}