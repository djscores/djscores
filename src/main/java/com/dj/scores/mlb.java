package com.dj.scores;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class mlb {
    static ArrayList<String[]> matchupList = new ArrayList<String[]>();
    static ArrayList<String[]> srsTotals = new ArrayList<String[]>();
    static DecimalFormat df = new DecimalFormat("0.00");
    
    public static void getmlb() {
        // String url = "https://www.covers.com/sports/mlb/matchups";
        String url = "https://www.covers.com/sports/mlb/matchups?selectedDate=2024-06-13";
        try {
            Document doc = Jsoup.connect(url).get();
            
            Elements games = doc.getElementsByClass("cmg_matchup_game_box cmg_game_data");

            for (Element game : games) {
                Elements matchupNames = game.getElementsByClass("cmg_matchup_header_team_names");
    
                for (int i = 0; i < matchupNames.size(); i++) { 
                    String[] matchupName = matchupNames.get(i).text().split(" at ");
                    if (matchupName.length == 1)
                        matchupName = matchupNames.get(i).text().split(" vs ");
                    if (matchupName[0].contains("NY") || 
                        matchupName[0].contains("LA")) matchupName[0] = matchupName[0].split(" ")[1];
                    if (matchupName[1].contains("NY") || 
                        matchupName[1].contains("LA")) matchupName[1] = matchupName[1].split(" ")[1];
                    if (matchupName[0].contains("Chi.")) matchupName[0] = matchupName[0].split("Chi. ")[1];
                    if (matchupName[1].contains("Chi.")) matchupName[1] = matchupName[1].split("Chi. ")[1];
                    matchupList.add(matchupName);
                    System.out.printf("%-20s",matchupNames.get(i).text());
                }
                System.out.println();
                Elements matchup = game.getElementsByClass("cmg_btn cmg_btn_primary");
            
                getsrs();
            
                String matchupHref = matchup.attr("href");
                Elements pitchers = game.getElementsByClass("cmg_l_col cmg_l_span_6 cmg_team_starting_pitcher");
                
                if (pitchers.size() > 0 ) {
                    for (int j = 0; j < pitchers.size(); j++) { 
                        Elements probable = pitchers.get(j).select("a");
                        String probableName = "";
                        if (probable.size() < 1)
                            probableName = "TBD";
                        else
                            probableName = probable.text();
                        String pitcherHref = probable.attr("href");
                        System.out.print(probableName + " ");
                        
                        if (!"TBD".equals(probableName)) {
                            // System.out.println(pitcherHref);
                            Double pitcherScore = getPitcherScore(pitcherHref);
                            // System.out.print(df.format(pitcherScore) + "\t\t");
                        }
                        // else
                            // System.out.println();
                    }
                }
                else {
                    
                }
                // System.out.println(matchupHref);
                String matchupurl = "https://www.covers.com"+matchupHref;
                Document matchupdoc = Jsoup.connect(matchupurl).get();
                
                Elements last10 = matchupdoc.getElementsByClass("table covers-CoversMatchups-Table");
                if (last10.size() == 0) {
                    last10 = matchupdoc.getElementsByClass("last-10-table");
                }
                Element awayLast10 = new Element("table");
                Element homeLast10 = new Element("table");
                if (last10.size() > 3) {
                    awayLast10 = last10.get(1);
                    homeLast10 = last10.get(3);
                }
                else {
                    if (last10.size() > 0) {
                        awayLast10 = last10.get(0);
                        homeLast10 = last10.get(1);
                    }
                }
                Elements awayLast10tr = awayLast10.select("tr");
                Elements homeLast10tr = homeLast10.select("tr");
                
                Double awaylast10 = 0D;
                Double awaylast10opp = 0D;
                Double homelast10 = 0D;
                Double homelast10opp = 0D;
                for (int j = 1; j < awayLast10tr.size(); j ++) {
                    if (awayLast10tr.size() > 0 && homeLast10tr.size() > 0) { 
                        String[] awayLast10GameScore = {"0","0"};
                        String[] homeLast10GameScore = {"0","0"};
                        if (awayLast10tr.get(j).select("td").size() > 0) {
                            String awayLast10score = awayLast10tr.get(j).select("td").get(2).text().replace(" (OT)","");
                                awayLast10GameScore = awayLast10score.substring(2, awayLast10score.length()).split(" - ");
                            String homeLast10score = homeLast10tr.get(j).select("td").get(2).text().replace(" (OT)","");
                                homeLast10GameScore = homeLast10score.substring(2, homeLast10score.length()).split(" - ");
                                
                                awaylast10 += Integer.parseInt(awayLast10GameScore[0].replaceAll("- ", ""));
                                awaylast10opp += Integer.parseInt(awayLast10GameScore[1].replaceAll("- ", ""));
                                homelast10 += Integer.parseInt(homeLast10GameScore[0].replaceAll("- ", ""));
                                homelast10opp += Integer.parseInt(homeLast10GameScore[1].replaceAll("- ", ""));
                                
                        }
                    }
                }

                Double awaylast10off = awaylast10 / 10;
                Double awaylast10def = awaylast10opp / 10;
                Double homelast10off = homelast10 / 10;
                Double homelast10def = homelast10opp / 10;
                
                String[] awayTeamAverage = getTeamAverage(matchupList.get(0)[0]);
                String[] homeTeamAverage = getTeamAverage(matchupList.get(0)[1]);
                String[] average = getTeamAverage("Average");

                Double awayOff = Double.parseDouble(awayTeamAverage[2].split(" ")[0]);
                Double homeDef = Double.parseDouble(homeTeamAverage[2].split(" ")[1]);
                Double homeOff = Double.parseDouble(homeTeamAverage[2].split(" ")[0]);
                Double awayDef = Double.parseDouble(awayTeamAverage[2].split(" ")[1]);

                Double awayOffRating = awayOff/Double.parseDouble(average[2].split(" ")[0]);
                Double homeDefRating = homeDef/Double.parseDouble(average[2].split(" ")[1]);
                Double homeOffRating = homeOff/Double.parseDouble(average[2].split(" ")[0]);
                Double awayDefRating = awayDef/Double.parseDouble(average[2].split(" ")[1]);

                Double awayPerfRating = (awayOffRating + homeDefRating)/2;
                Double homePerfRating = (homeOffRating + awayDefRating)/2;
                
                Double awayBaseOff = (awayOff + homeDef)/2;
                Double homeBaseOff = (homeOff + awayDef)/2;

                Double awayProjected = awayBaseOff * awayPerfRating;
                Double homeProjected = (homeBaseOff * homePerfRating);
                
                // System.out.println();
                System.out.println(awayTeamAverage[1] + "\tat\t" + homeTeamAverage[1]);
                System.out.println("awayProjected - \t" + df.format(awayProjected) + "\t homeProjected - \t" + df.format(homeProjected));
                
                Double away10OffRating = awaylast10off/Double.parseDouble(average[2].split(" ")[0]);
                Double home10DefRating = homelast10def/Double.parseDouble(average[2].split(" ")[1]);
                Double home10OffRating = homelast10off/Double.parseDouble(average[2].split(" ")[0]);
                Double away10DefRating = awaylast10def/Double.parseDouble(average[2].split(" ")[1]);

                Double away10PerfRating = (away10OffRating + home10DefRating)/2;
                Double home10PerfRating = (home10OffRating + away10DefRating)/2;
                 
                Double away10BaseOff = (awaylast10off + homelast10def)/2;
                Double home10BaseOff = (homelast10off + awaylast10def)/2;

                Double away10Projected = away10BaseOff * away10PerfRating;
                Double home10Projected = (home10BaseOff * home10PerfRating);

                System.out.println("awayLast10Projected - \t" + df.format(away10Projected) + "\t homeLast10Projected - \t" + df.format(home10Projected));
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
        String url = "https://www.baseball-reference.com/leagues/MLB-standings.shtml";
        try {
            Document doc = Jsoup.connect(url).get();
            String docstr = doc.toString();
            String detailedStandings = docstr.substring(docstr.lastIndexOf("<table "), docstr.lastIndexOf("</table>"));
            Document detailedStandingsDoc = Jsoup.parse(detailedStandings+"</table>");
            Elements advancedTeamRows = detailedStandingsDoc.select("tr");
            // System.out.println(advancedTeamRows.size());
            for (int i = 1; i < advancedTeamRows.size(); i++) { 
                Element row = advancedTeamRows.get(i);
                Elements teamTds = row.select("td"); 
                String teamName = teamTds.get(0).text();
                String srs = teamTds.get(9).text();
                // String wins = teamTds.get(1).text();
                // String losses = teamTds.get(2).text();
                String runs = teamTds.get(5).text();
                String runsAllowed = teamTds.get(6).text();
                // System.out.println(srs + " " + teamName + " " + runs + " " + runsAllowed);
                String[] srsTotal = new String[3];
                srsTotal[0] = teamName;
                srsTotal[1] = srs;
                srsTotal[2] = runs + " " + runsAllowed;
                srsTotals.add(srsTotal);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Double getPitcherScore(String pitcherUrl) {
        Double rollingPitcherScore = 47.5D;
        String averages = "";
        try {
            Document doc = Jsoup.connect(pitcherUrl).get();
            Elements stats = doc.getElementsByClass("table-responsive covers-CoversMatchups-responsiveTableContainer border-radius-4px");
            Double pitcherScore = 0D;
            int i = 0;
            if (stats.size() > 0) {
                Elements statsTr = stats.get(0).select("tr");
                for (Element gametr : statsTr) {
                    Elements gametd = gametr.select("td");
                    if (gametd.size() > 1) {
                        if (!"".equals(gametd.get(5).text())) {
                            double so = Double.parseDouble(gametd.get(5).text());
                            double outs = Double.parseDouble(gametd.get(6).text());
                            double hits = Double.parseDouble(gametd.get(8).text());
                            double walks = Double.parseDouble(gametd.get(9).text());
                            double runs = Double.parseDouble(gametd.get(11).text());
                            double hr = Double.parseDouble(gametd.get(12).text());
                            Double gameScore = 47.5D + so + (outs*1.5) - (walks*2) - (hits*2) - (runs*3) - (hr * 4);
                            pitcherScore = pitcherScore + gameScore;
                            i++;
                        }
                        else {
                            if (14 == gametd.size()) {
                                double so = Double.parseDouble(gametd.get(3).text());
                                double hits = Double.parseDouble(gametd.get(6).text());
                                double walks = Double.parseDouble(gametd.get(7).text());
                                double runs = Double.parseDouble(gametd.get(9).text());
                                double hr = Double.parseDouble(gametd.get(10).text());
                                averages = "so "+so + " hits " + hits + " walks "+walks + " runs " +runs + " hr " + hr;
                            }
                        }
                    }
                }
            }
            rollingPitcherScore = pitcherScore / i;
            System.out.println(df.format(rollingPitcherScore) + " " + averages);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rollingPitcherScore;
    }
    public static String[] getTeamAverage(String team) {
        for (String[] teamNameMatch : srsTotals) {
            if (teamNameMatch[0].contains(team)) {
                return teamNameMatch;
            }
        }
        return null;
    }
    public static void getBaseballReferenceMatchups() {
        try {
            Document doc = Jsoup.connect("https://www.baseball-reference.com/previews/").get();
            Elements games = doc.getElementsByClass("game_summary nohover ");
            getsrs();
            // System.out.println(games.size());
            for (Element game : games) {
                Elements teams = game.getElementsByAttributeValueMatching("href","/teams/");
                for (Element team : teams) {
                    System.out.printf("%-20s",team.text());
                }
                System.out.println();
                Elements previews = game.getElementsByAttributeValueMatching("href", "/previews/2023/");
                for (Element preview : previews) {
                    String href = preview.attr("href");
                    // System.out.print(preview.attr("href"));
                    Document previewdoc = Jsoup.connect("https://www.baseball-reference.com"+href).get();
                    String previewdocstr = previewdoc.toString();
                    previewdocstr = previewdocstr.replace("<!--","");
                    previewdocstr = previewdocstr.replace("-->","");
                    Document previewclean = Jsoup.parse(previewdocstr);

                    Elements last10s = previewclean.getElementsByAttributeValueStarting("id","last10_");
                    Elements last10tables = last10s.select("table");
                    Double awaylast10 = 0D;
                    Double awaylast10opp = 0D;
                    Double homelast10 = 0D;
                    Double homelast10opp = 0D;
                    
                    Elements awaylast10trs = last10tables.get(0).select("tr");
                    for (Element awaylast10tr : awaylast10trs) {
                        if (awaylast10tr.select("td").size() > 0) {
                            String[] awaygamescores = awaylast10tr.select("td").get(4).text().split("-");
                            awaylast10 += Integer.parseInt(awaygamescores[0]);
                            if (awaygamescores[1].contains("("))
                                awaygamescores[1] = awaygamescores[1].substring(0,awaygamescores[1].indexOf(" "));
                            awaylast10opp += Integer.parseInt(awaygamescores[1]);
                        }
                    }
                    
                    Elements homelast10trs = last10tables.get(1).select("tr");
                    for (Element homelast10tr : homelast10trs) {
                        if (homelast10tr.select("td").size() > 0) {
                            String[] homegamescores = homelast10tr.select("td").get(4).text().split("-");
                            if (homegamescores[1].contains("("))
                                homegamescores[1] = homegamescores[1].substring(0,homegamescores[1].indexOf(" "));
                            homelast10 += Integer.parseInt(homegamescores[0]);
                            homelast10opp += Integer.parseInt(homegamescores[1]);
                        }
                    }
                    
                    Double awaylast10off = awaylast10 / 10;
                    Double awaylast10def = awaylast10opp / 10;
                    Double homelast10off = homelast10 / 10;
                    Double homelast10def = homelast10opp / 10;

                    String[] average = getTeamAverage("Average");
                    Double away10OffRating = awaylast10off/Double.parseDouble(average[2].split(" ")[0]);
                    Double home10DefRating = homelast10def/Double.parseDouble(average[2].split(" ")[1]);
                    Double home10OffRating = homelast10off/Double.parseDouble(average[2].split(" ")[0]);
                    Double away10DefRating = awaylast10def/Double.parseDouble(average[2].split(" ")[1]);
    
                    Double away10PerfRating = (away10OffRating + home10DefRating)/2;
                    Double home10PerfRating = (home10OffRating + away10DefRating)/2;
                    
                    Double away10BaseOff = (awaylast10off + homelast10def)/2;
                    Double home10BaseOff = (homelast10off + awaylast10def)/2;
    
                    Double away10Projected = away10BaseOff * away10PerfRating;
                    Double home10Projected = (home10BaseOff * home10PerfRating);
    
                    System.out.printf("%-20s%-20s",df.format(away10Projected) , df.format(home10Projected));
                    System.out.println();
                    Elements pitchers = game.getElementsByAttributeValueMatching("href","/players/");   
                    for (Element pitcher : pitchers) {
                        System.out.printf("%-20s",pitcher.text());
                    }
                    System.out.println();
                    Elements splits = previewclean.getElementsByAttributeValueStarting("id","sp_");
                    Elements splitsTables = splits.select("table");
                    for (Element splitsTable : splitsTables) {
                        Elements splitstrs = splitsTable.select("tr");
                        for (Element splitstr : splitstrs) {
                            String splitstrstr = splitstr.text();
                            if (splitstrstr.contains("Last 7 GS")) {
                                Elements gamescoretd = splitstr.select("td");
                                String gamescore = gamescoretd.get(10).text();
                                System.out.printf("%-20s",gamescore);
                            }
                        }
                    }
                    System.out.println();
                    Thread.sleep(500L);
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
