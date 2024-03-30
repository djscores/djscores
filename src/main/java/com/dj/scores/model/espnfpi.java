package com.dj.scores.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dj.odds.draftkingsnfl;
import com.dj.odds.fanduelnfl;
import com.dj.odds.mgmnfl;
import com.dj.odds.mgmncaaf;
import com.dj.odds.draftkingsncaaf;

public class espnfpi {

    static ArrayList<String[]> mgmodds;
    static ArrayList<String[]> draftkingsodds;
    static ArrayList<String[]> fanduelodds;
    static String[][] teamratings;

    public static void runespnfpi() throws Exception {
        // String weekurl = "https://www.espn.com/nfl/schedule/_/week/8/year/2023/seasontype/2";
        String weekurl = "https://www.espn.com/college-football/schedule";
        teamratings = predictiontrackernfl.getnflLogisticRegression();
        if (weekurl.contains("college")) {
            mgmodds = mgmncaaf.getOdds();
            draftkingsodds = draftkingsncaaf.getOdds();
        }
        else {
            mgmodds = mgmnfl.getOdds();
            draftkingsodds = draftkingsnfl.getOdds();
        }
        // draftkingsodds = draftkingsnfl.getOdds();
		// fanduelodds = fanduelnfl.getOdds();
        ArrayList<String[]> fpilist = new ArrayList<String[]>();

        // String weekurl = "https://www.espn.com/mlb/schedule";
        Document weekdoc = Jsoup.connect(weekurl).get();
        Elements gameids = weekdoc.getElementsByAttributeValueMatching("href", "gameId");
        DecimalFormat df = new DecimalFormat("0.00");

        for (Element gameid : gameids) {
            String[] fpigame = new String[6];
            String gamehref = gameid.attr("href");
            // System.out.println(gamehref);
            Document gamedoc = Jsoup.connect("https://www.espn.com"+gamehref).get();
            Elements awaypredict = gamedoc.getElementsByClass("matchupPredictor__teamValue matchupPredictor__teamValue--b left-0 top-0 flex items-baseline absolute copy");
            Elements homepredict = gamedoc.getElementsByClass("matchupPredictor__teamValue matchupPredictor__teamValue--a bottom-0 right-0 flex items-baseline absolute copy");
            String awaypredictstr = awaypredict.text();
            String homepredictstr = homepredict.text();
            if (!"".equals(awaypredictstr)) {
                Elements teams = gamedoc.select("h2");
                double awaydouble = Double.parseDouble(awaypredictstr.split(" %")[0])/100;
                double homedouble = Double.parseDouble(homepredictstr.split(" %")[0])/100;
                
                double awaymoneyline = (awaydouble > .5) ? -((awaydouble/(1-awaydouble))*100) : (1-awaydouble)/awaydouble*100;
                double homemoneyline = (homedouble > .5) ? -((homedouble/(1-homedouble))*100) : (1-homedouble)/homedouble*100;
                
                String[] shortname = teams.get(0).text().split(" ");
                String shortname0 = shortname[shortname.length-1];
                shortname = teams.get(1).text().split(" ");
                String shortname1 = shortname[shortname.length-1];
                fpigame[0] = shortname0;
                fpigame[1] = shortname1;
                fpigame[2] = df.format(awaymoneyline);
                fpigame[3] = df.format(homemoneyline);
                fpigame[4] = teams.get(0).text();
                fpigame[5] = teams.get(1).text();
                fpilist.add(fpigame);
                System.out.println(teams.get(0).text() + " @ " + teams.get(1).text() + " " + df.format(awaydouble) + " " + df.format(homedouble) + " " + df.format(awaymoneyline) + " " + df.format(homemoneyline));
            }
        }
        System.out.printf("%-35s%-15s%-15s%-15s%-15s\n","team","espn","mgm","dk","fd");
        for (String[] fpimoneyline : fpilist) {
            String[] mgm;
            String[] draftkings;
            String[] fanduel;
            if (!weekurl.contains("college")) {
                mgm = getLine(fpimoneyline[0],mgmodds);
                draftkings = getLine(fpimoneyline[0],draftkingsodds);
                fanduel = getLine(fpimoneyline[0],fanduelodds);
            }
            else {
                mgm = getNcaaLine(fpimoneyline[4],mgmodds);
                draftkings = getLine(fpimoneyline[4],draftkingsodds);
                fanduel = getNcaaLine(fpimoneyline[4],fanduelodds);
            }
            // System.out.println(fpimoneyline[0] + "\t\t" + fpimoneyline[2] + "\t" + mgm[2] + "\t" + draftkings[2] + "\t" + fanduel[3]);
            
            System.out.printf("%-35s%-15s%-15s%-15s%-15s\n",fpimoneyline[4],
            fpimoneyline[2],
            mgm.length > 2 ? mgm[3]: "",
            draftkings.length > 2 ? draftkings[3] : "",
            fanduel.length > 2 ? fanduel[3] : "");

            if (!weekurl.contains("college")) {
                mgm = getLine(fpimoneyline[1],mgmodds);
                draftkings = getLine(fpimoneyline[1],draftkingsodds);
                fanduel = getLine(fpimoneyline[1],fanduelodds);
            }
            else {
                mgm = getNcaaLine(fpimoneyline[5],mgmodds);
                draftkings = getNcaaLine(fpimoneyline[5],draftkingsodds);
                fanduel = getNcaaLine(fpimoneyline[5],fanduelodds);
            }
            // System.out.println(fpimoneyline[1] + "\t\t" + fpimoneyline[3] + "\t" + mgm[2] + "\t" + draftkings[2] + "\t" + fanduel[3]);
            
            System.out.printf("%-35s%-15s%-15s%-15s%-15s\n",fpimoneyline[5],
            fpimoneyline[3],
            mgm.length > 2 ? mgm[3] : "",
            draftkings.length > 2 ? draftkings[3] : "",
            fanduel.length > 2 ? fanduel[3] : "");
        }
    }
    public static String[] getLine(String name, ArrayList<String[]> oddslist) {
        String[] oddsline = {}; if (oddslist != null)
        for (String[] line : oddslist) {
            if (name.equals(line[0])) {
                oddsline = line;
                return oddsline;
            }
        }
        return oddsline;
    }
    public static String[] getNcaaLine(String name, ArrayList<String[]> oddslist) {
        String[] oddsline = {}; if (oddslist != null)
        for (String[] line : oddslist) {
            if (name.toLowerCase().contains(line[0].toLowerCase().toLowerCase())) {
                oddsline = line;
                return oddsline;
            }
        }
        return oddsline;
    }
    public static String getRating(String name) {
        String foundrating = "";
        for (int i = 0; i < teamratings[0].length; i++) {
            if (name.toLowerCase().contains(teamratings[0][i].toLowerCase()))
                return teamratings[1][i];
        }
        return foundrating;
    }
    public static String getNcaafRating(String name) {
        String foundrating = "";
        for (int i = 0; i < teamratings[0].length; i++) {
            if (teamratings[0][i].toLowerCase().contains(name.toLowerCase()))
                return teamratings[1][i];
        }
        return foundrating;
    }
}