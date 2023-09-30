package com.dj.scores.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class espnfpi {

    static ArrayList<String[]> mgmodds;
    static ArrayList<String[]> draftkingsodds;
    static ArrayList<String[]> fanduelodds;

    public static void runespnfpi() throws Exception {
        // mgmodds = mgm.getOdds();
        // draftkingsodds = draftkingsnfl.getOdds();
		// fanduelodds = fanduelnfl.getOdds();
        ArrayList<String[]> fpilist = new ArrayList<String[]>();

        // String weekurl = "https://www.espn.com/nfl/schedule/_/week/4/year/2023/seasontype/2";
        // String weekurl = "https://www.espn.com/college-football/schedule";
        String weekurl = "https://www.espn.com/mlb/schedule";
        Document weekdoc = Jsoup.connect(weekurl).get();
        Elements gameids = weekdoc.getElementsByAttributeValueMatching("href", "gameId");
        
        for (Element gameid : gameids) {
            String[] fpigame = new String[4];
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
                
                DecimalFormat df = new DecimalFormat("0.00");
                String[] shortname = teams.get(0).text().split(" ");
                String shortname0 = shortname[shortname.length-1];
                shortname = teams.get(1).text().split(" ");
                String shortname1 = shortname[shortname.length-1];
                fpigame[0] = shortname0;
                fpigame[1] = shortname1;
                fpigame[2] = df.format(awaymoneyline);
                fpigame[3] = df.format(homemoneyline);
                fpilist.add(fpigame);
                System.out.println(teams.get(0).text() + " @ " + teams.get(1).text() + " " + df.format(awaydouble) + " " + df.format(homedouble) + " " + df.format(awaymoneyline) + " " + df.format(homemoneyline));
            }
        }
        System.out.printf("%-15s%-15s%-15s%-15s%-15s\n","team","espn","mgm","dk","fd");
        for (String[] fpimoneyline : fpilist) {
            String[] mgm = getLine(fpimoneyline[0],mgmodds);
            String[] draftkings = getLine(fpimoneyline[0],draftkingsodds);
            String[] fanduel = getLine(fpimoneyline[0],fanduelodds);
            // System.out.println(fpimoneyline[0] + "\t\t" + fpimoneyline[2] + "\t" + mgm[2] + "\t" + draftkings[2] + "\t" + fanduel[3]);
            
            System.out.printf("%-15s%-15s%-15s%-15s%-15s\n",fpimoneyline[0],
            fpimoneyline[2],
            mgm.length > 2 ? mgm[3]: "",
            draftkings.length > 2 ? draftkings[3] : "",
            fanduel.length > 2 ? fanduel[3] : "");

            mgm = getLine(fpimoneyline[1],mgmodds);
            draftkings = getLine(fpimoneyline[1],draftkingsodds);
            fanduel = getLine(fpimoneyline[1],fanduelodds);
            // System.out.println(fpimoneyline[1] + "\t\t" + fpimoneyline[3] + "\t" + mgm[2] + "\t" + draftkings[2] + "\t" + fanduel[3]);
            System.out.printf("%-15s%-15s%-15s%-15s%-15s\n",fpimoneyline[1],
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
}