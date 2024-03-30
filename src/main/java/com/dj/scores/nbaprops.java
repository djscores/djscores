package com.dj.scores;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class nbaprops {
    static HashMap<String,String> teamOffense = new HashMap<String,String>();
    static HashMap<String,String> teamDefense = new HashMap<String,String>();
    static HashMap<String,String> teamUrl = new HashMap<String,String>();
    static ArrayList<String[]> matchupList = new ArrayList<String[]>();
    static ArrayList<String[]> srsTotals = new ArrayList<String[]>();
    static HashMap<String,Double> asrsList = new HashMap<String,Double>();
    static HashMap<String,Double> homeTeamProbabilities = new HashMap<String,Double>();

    public static void getnba() {
        String url = "https://www.covers.com/sports/nba/matchups";
        try {
            Document doc = Jsoup.connect(url).get();
            
            Elements matchups = doc.getElementsByClass("cmg_btn cmg_btn_primary");
            Elements matchupNames = doc.getElementsByClass("cmg_matchup_header_team_names");

            for (int i = 0; i < matchupNames.size(); i++) { 
                String[] matchupName;
                if (matchupNames.get(i).text().contains(" vs "))
                    matchupName = matchupNames.get(i).text().split(" vs ");
                else
                    matchupName = matchupNames.get(i).text().split(" at ");
                if (matchupName[0].contains("L.A.")) matchupName[0] = matchupName[0].split(" ")[1];
                if (matchupName[1].contains("L.A.")) matchupName[1] = matchupName[1].split(" ")[1];
                matchupName[1] = matchupName[1].split(" NEUTRAL")[0];
                matchupList.add(matchupName);
                // System.out.println(matchupNames.get(i).text());
            }
            for (int i = 0; i < matchups.size(); i++) { 
                Element matchup = matchups.get(i);
                String matchupHref = matchup.attr("href");
                String matchupurl = "https://www.covers.com"+matchupHref;
                Document matchupdoc = Jsoup.connect(matchupurl).get();
                
                Elements pointsAwayTable = matchupdoc.getElementsByAttributeValueMatching("aria-labelledby", "points");
                
                // https://www.covers.com/sport/player-props/matchup/291002?propEvent=NBA_GAME_PLAYER_POINTS&countryCode=US&stateProv=Ohio&isLeagueVersion=False
                String gameid = matchupHref.substring(matchupHref.lastIndexOf("/")+1,matchupHref.length());
                String pointsurl = "https://www.covers.com/sport/player-props/matchup/" + gameid + "?propEvent=NBA_GAME_PLAYER_POINTS&countryCode=US&stateProv=Ohio&isLeagueVersion=False";
                
                Document pointsdoc = Jsoup.connect(pointsurl).get();
                Elements playerarticles = pointsdoc.select("article");
                for (Element playerarticle : playerarticles) {
                    
                    Elements playerhrefs = playerarticle.getElementsByAttributeValueContaining("href","/players/");
                    if (playerhrefs.size() > 0) {
                        Element playerhref = playerhrefs.get(1);
                        if (playerhref != null) {
                            String playerhrefstr = playerhref.attr("href");
                            Elements propdiv = playerarticle.getElementsByClass("player-props-projection-bestOdds-div");
                            String points = propdiv.select("div").get(1).text();
                            String propstr = propdiv.first().getElementsByClass("other-over-odds").text();
                            propstr = propstr.split("Points Scored")[0];
                            // System.out.println(playerhref.text() + " - " + propstr);
                            System.out.printf("%-25s%-5s\n",playerhref.text(),propstr);
                            Elements otheroddsrows = playerarticle.getElementsByClass("other-odds-row");
                            for (Element otheroddsrow : otheroddsrows) {
                                Element otherOddsLabelDiv  = otheroddsrow.select("div").first();
                                Element bookimg = otherOddsLabelDiv.select("img").first();
                                String imgsrc = bookimg.attr("src");
                                System.out.println(imgsrc);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}