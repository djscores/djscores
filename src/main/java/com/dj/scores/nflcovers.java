package com.dj.scores;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class nflcovers {
    static ArrayList<String[]> matchupList = new ArrayList<String[]>();
    static HashMap<String,Double> srsTotals = new HashMap<String,Double>();
    static HashMap<String,Double> teamOffensePoints = new HashMap<String,Double>();
    static HashMap<String,Double> teamDefensePoints = new HashMap<String,Double>();
    static double ppgLeagueAverage;
    static DecimalFormat df = new DecimalFormat("0.00");
    
    public static void getnfl() {
        getsrs();
        String url = "https://www.covers.com/sports/nfl/matchups";
        // String url = "https://www.covers.com/sports/ncaaf/matchups";
        // String url = "https://www.covers.com/sports/nfl/matchups?selectedDate=2023-12-07";
        try {
            Document doc = Jsoup.connect(url).get();
            
            Elements games = doc.getElementsByClass("cmg_game_data cmg_matchup_game_box");

            for (Element game : games) {
                Elements matchupNames = game.getElementsByClass("cmg_matchup_header_team_names");
                // System.out.println(matchupNames);
                for (int i = 0; i < matchupNames.size(); i++) { 
                    String[] matchupName = matchupNames.get(i).text().split(" at ");
                    if (matchupName.length == 1)
                        matchupName = matchupNames.get(i).text().split(" vs ");
                    if (matchupName[0].contains("N.Y.") || 
                        matchupName[0].contains("L.A.")) matchupName[0] = matchupName[0].split(" ")[1];
                    if (matchupName[1].contains("N.Y.") || 
                        matchupName[1].contains("L.A.")) matchupName[1] = matchupName[1].split(" ")[1];
                    if (matchupName[0].contains("Chi.")) matchupName[0] = matchupName[0].split("Chi. ")[1];
                    if (matchupName[1].contains("Chi.")) matchupName[1] = matchupName[1].split("Chi. ")[1];
                    matchupName[1] = matchupName[1].split(" NEUTRAL")[0];
                    matchupList.add(matchupName);
                    System.out.printf("%-20s",matchupNames.get(i).text());
                }
                System.out.println();
                Elements matchup = game.getElementsByAttributeValueContaining("href","matchup");
                String matchupHref = "";
                // if (matchup.size() > 3)
                //     matchupHref = matchup.get(3).attr("href");
                // else
                    matchupHref = matchup.get(4).attr("href");
                // System.out.println(matchupHref);
                String matchupurl = "https://www.covers.com"+matchupHref;
                Document matchupdoc = Jsoup.connect(matchupurl).get();

                // https://www.covers.com/sport/football/nfl/matchup/284808/stats-analysis/false/overall
                // true is home team
                String matchupurlhome = "https://www.covers.com"+matchupHref+"/stats-analysis/true/overall";
                Document matchupdochome = Jsoup.connect(matchupurlhome).get();

                Elements pointstableaway = matchupdoc.getElementsByClass("stats-table football-stats-table");
                String awayOffStr = pointstableaway.get(2).select("tr").get(1).select("td").get(0).text();
                String homeDefStr = pointstableaway.get(2).select("tr").get(1).select("td").get(4).text();

                Elements pointstablehome = matchupdochome.getElementsByClass("stats-table football-stats-table");
                String homeOffStr = pointstablehome.get(2).select("tr").get(1).select("td").get(0).text();
                String awayDefStr = pointstablehome.get(2).select("tr").get(1).select("td").get(4).text();

                Element leagueAveragePoints = matchupdoc.getElementsByClass("average-table").get(2);
                String averagePointsString = leagueAveragePoints.select("tr").get(1).select("td").get(0).text();

                String matchupurlhomelast3 = "https://www.covers.com"+matchupHref+"/stats-analysis/true/last3";
                Document matchupdochomelast3 = Jsoup.connect(matchupurlhomelast3).get();

                String matchupurlawaylast3 = "https://www.covers.com"+matchupHref+"/stats-analysis/false/last3";
                Document matchupdocawaylast3 = Jsoup.connect(matchupurlawaylast3).get();

                Elements pointstableawaylast3 = matchupdocawaylast3.getElementsByClass("stats-table football-stats-table");
                Elements pointstablehomelast3 = matchupdochomelast3.getElementsByClass("stats-table football-stats-table");
                
                String awayOffStrlast3 = pointstableawaylast3.get(2).select("tr").get(1).select("td").get(0).text();
                String homeDefStrlast3 = pointstablehomelast3.get(2).select("tr").get(1).select("td").get(4).text();

                String homeOffStrlast3 = pointstablehomelast3.get(2).select("tr").get(1).select("td").get(0).text();
                String awayDefStrlast3 = pointstableawaylast3.get(2).select("tr").get(1).select("td").get(4).text();

                Double awayOff = Double.parseDouble(awayOffStr);
                Double homeDef = Double.parseDouble(homeDefStr);
                Double homeOff = Double.parseDouble(homeOffStr);
                Double awayDef = Double.parseDouble(awayDefStr);
                Double average = Double.parseDouble(averagePointsString);

                Double awayOfflast3 = Double.parseDouble(awayOffStrlast3);
                Double homeDeflast3 = Double.parseDouble(homeDefStrlast3);
                Double homeOfflast3 = Double.parseDouble(homeOffStrlast3);
                Double awayDeflast3 = Double.parseDouble(awayDefStrlast3);

                Double awayOffRating = awayOff/average;
                Double homeDefRating = homeDef/average;
                Double homeOffRating = homeOff/average;
                Double awayDefRating = awayDef/average;

                Double awayPerfRating = (awayOffRating + homeDefRating)/2;
                Double homePerfRating = (homeOffRating + awayDefRating)/2;
                
                Double awayBaseOff = (awayOff + homeDef)/2;
                Double homeBaseOff = (homeOff + awayDef)/2;

                Double awayProjected = awayBaseOff * awayPerfRating;
                Double homeProjected = (homeBaseOff * homePerfRating);// + 2.5;
                
                // System.out.println();
                // System.out.println(awayOff + "\tat\t" + homeOff);
                System.out.println("awayProjected - \t" + df.format(awayProjected) + "\t homeProjected - \t" + df.format(homeProjected));
                
                Double away3OffRating = awayOfflast3/average;
                Double home3DefRating = homeDeflast3/average;
                Double home3OffRating = homeOfflast3/average;
                Double away3DefRating = awayDeflast3/average;

                Double away3PerfRating = (away3OffRating + home3DefRating)/2;
                Double home3PerfRating = (home3OffRating + away3DefRating)/2;
                 
                Double away3BaseOff = (awayOfflast3 + homeDeflast3)/2;
                Double home3BaseOff = (homeOfflast3 + awayDeflast3)/2;

                Double away3Projected = away3BaseOff * away3PerfRating;
                Double home3Projected = (home3BaseOff * home3PerfRating);// + 2.5;

                System.out.println("awayLast3Projected - \t" + df.format(away3Projected) + "\t homeLast3Projected - \t" + df.format(home3Projected));
                Thread.sleep(500L);

                String gameid = matchupHref.substring(matchupHref.lastIndexOf("/")+1);
                System.out.println(gameid);

                // getMatchupProps(gameid);

                System.out.println();
                matchupList = new ArrayList<String[]>();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getsrs() {
        String url = "https://www.pro-football-reference.com/years/2023/";
        try {
            Document doc = Jsoup.connect(url).get();
            Element afc = doc.getElementById("AFC");
            Element nfc = doc.getElementById("NFC");
            Element teamScoring = doc.getElementById("all_team_scoring");
            String teamScoringstr = teamScoring.toString();
            String ppg = teamScoringstr.substring(teamScoringstr.lastIndexOf("<td class=\"right \" data-stat=\"scoring\" >"));
            Document ppgdoc = Jsoup.parse(ppg);
            ppgLeagueAverage = Double.parseDouble(ppgdoc.text().split(" ")[0]);
            String docstr = doc.toString();
            String table = docstr.substring(docstr.indexOf("<table class=\"per_match_toggle sortable stats_table\" id=\"team_stats\""));
            table = table.substring(0,table.indexOf("</table>"));
            Document statsdoc = Jsoup.parse(table);
            
            Elements teamOffStatstrs = statsdoc.select("tr");
            for (Element teamOffStatstr : teamOffStatstrs) {
                Elements teamOffStatstrtds = teamOffStatstr.select("td");
                if (teamOffStatstrtds.size() > 0) {
                    String team = teamOffStatstrtds.get(0).text();
                    if (!"".equals(teamOffStatstrtds.get(1).text())) {
                        Double games = Double.parseDouble(teamOffStatstrtds.get(1).text());
                        Double pts = Double.parseDouble(teamOffStatstrtds.get(2).text());
                        teamOffensePoints.put(team,pts/games);
                    }
                    else
                    if ("Avg Tm/G".equals(team))
                        teamOffensePoints.put("Average",Double.parseDouble(teamOffStatstrtds.get(2).text()));
                }
            }
            // ppg = ppg.substring(0,ppg.indexOf("</td>"));
            // Document detailedStandingsDoc = Jsoup.parse(detailedStandings+"</table>");
            Elements afcTeams = afc.select("tr");
            Elements nfcTeams = nfc.select("tr");

            // System.out.println(advancedTeamRows.size());
            for (int i = 1; i < afcTeams.size(); i++) { 
                Element afcrow = afcTeams.get(i);
                Element nfcrow = nfcTeams.get(i);
                Elements afcTeamNameHeader = afcrow.select("th"); 
                Elements nfcTeamNameHeader = nfcrow.select("th"); 
                if (afcTeamNameHeader.size() == 1 || nfcTeamNameHeader.size() == 1) {
                    Elements afcTeam = afcrow.select("td"); 
                    Elements nfcTeam = nfcrow.select("td"); 
                    String afcteamName = afcTeamNameHeader.size() > 0 ? afcTeamNameHeader.get(0).text() : "";
                    String nfcteamName = nfcTeamNameHeader.size() > 0 ? nfcTeamNameHeader.get(0).text() : "";
                    String afcsrs = afcTeam.get(9).text();
                    String nfcsrs = nfcTeam.get(9).text();
                    // String wins = teamTds.get(1).text();
                    // String losses = teamTds.get(2).text();
                    // String runs = teamTds.get(5).text();
                    // String runsAllowed = teamTds.get(6).text();
                    // System.out.println(srs + " " + teamName + " " + runs + " " + runsAllowed);
                    // srsTotal[2] = runs + " " + runsAllowed;
                    srsTotals.put(afcteamName,Double.parseDouble(afcsrs));
                    
                    srsTotals.put(nfcteamName,Double.parseDouble(nfcsrs));
                }
            }
            url = "https://www.pro-football-reference.com/years/2023/opp.htm";
            doc = Jsoup.connect(url).get();
            Element teamDefense = doc.getElementById("team_stats");
            Elements teamDefensetr = teamDefense.select("tr");
            for (Element tr : teamDefensetr) {
                Elements tds = tr.select("td");
                if (tds.size() > 0) {
                    String team = tds.get(0).text();
                    if (!"".equals(tds.get(1).text())) {
                        Double games = Double.parseDouble(tds.get(1).text());
                        Double pts = Double.parseDouble(tds.get(2).text());
                        teamDefensePoints.put(team,pts/games);
                    }
                    else
                    if ("Avg Tm/G".equals(team))
                        teamDefensePoints.put("Average",Double.parseDouble(tds.get(2).text()));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Double getTeamOffenseAverage(String team) {
        for (String name : teamOffensePoints.keySet()) {
            if (name.contains(team)) {
                return teamOffensePoints.get(name);
            }
        }
        return null;
    }
    public static Double getTeamDefenseAverage(String team) {
        for (String name : teamDefensePoints.keySet()) {
            if (name.contains(team)) {
                return teamDefensePoints.get(name);
            }
        }
        return null;
    }
    
    public static void getMatchupProps(String gameid) throws IOException {
        String props = "https://www.covers.com/sport/props/gamepropsbrick?gameId="+gameid;
        Document propsdoc = Jsoup.connect(props).get();
        Elements propstables = propsdoc.select("table");

        for (Element propstable : propstables) {
            Elements propstableths = propstable.select("th");
            for (Element propstableth : propstableths) {
                System.out.print(propstableth.text()+"\t\t");
            }
            System.out.println();
            Elements propstablebody = propstable.select("tbody");
            Elements propstabletrs = propstablebody.select("tr");
            for (Element propstabletr : propstabletrs) {
                Elements propstabletds = propstabletr.select("td");
                for (int i = 0; i < propstabletds.size(); i++) {
                    Element propstabletd = propstabletds.get(i);
                    if (i == 0) {
                        Elements propstabletda = propstabletd.select("a");
                        System.out.println(propstabletda.text()+"\t\t");
                    }
                    else {
                        Elements propstabletdas = propstabletd.select("a");
                        for (Element propstabletda : propstabletdas) {
                            Elements propstabletdaimg = propstabletda.select("img");
                            if (propstabletdaimg.size() > 0) {
                                String book = propstabletdaimg.get(0).attr("src");
                                if (book.contains("fanduel") || book.contains("draftkings") || book.contains("mgm")) {
                                    if (book.contains("fanduel")) System.out.print("fanduel\t");
                                    if (book.contains("draftkings")) System.out.print("draftkings\t");
                                    if (book.contains("mgm")) System.out.print("mgm\t");
                                    Elements propstabletdadiv = propstabletda.select("div");
                                    System.out.print(propstabletdadiv.text());
                                    System.out.println();
                                }
                            }
                        }
                        System.out.println();
                    }
                }
                
            }
        }
    }
}
