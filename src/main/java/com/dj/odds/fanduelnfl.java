package com.dj.odds;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class fanduelnfl {
    public static ArrayList<String[]> getOdds() throws Exception {
        ArrayList<String[]> fanduelodds = new ArrayList<String[]>();
        String url = "https://sportsbook.fanduel.com/navigation/nfl";
        System.setProperty("webdriver.chrome.driver","C:\\Users\\daryl\\work\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(90));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(90));
        driver.get(url);
        String fdhtml = driver.getPageSource();
        Document doc = Jsoup.parse(fdhtml);
        Element main = doc.getElementById("main");
        Elements lis = main.select("li");
        for (Element li : lis) {
            Elements labels = li.getElementsByAttributeValueMatching("aria-label","Odds");
            String[] lineodds = new String[4];
            for (int i = 0; i < labels.size(); i++) {
                String odds = labels.get(i).attr("aria-label").replace(" Odds","");
                odds = odds.replace("OVER,","");
                odds = odds.replace("UNDER,","");
                String[] oddsarr = odds.split(",");
                if (lineodds[0] == null) {
                    lineodds[0] = oddsarr[0];
                    String[] shortname = lineodds[0].split(" ");
                    lineodds[0] = shortname[shortname.length-1];
                    if (oddsarr.length > 2) {
                        lineodds[1] = oddsarr[1] + " " + oddsarr[2];
                        continue;
                    }
                    else
                        lineodds[1] = oddsarr[1];
                }
                if (lineodds[0] != null && lineodds[1] != null && lineodds[3] == null) {
                    lineodds[3] = oddsarr[1];
                    continue;
                }
                if (lineodds[0] != null && lineodds[1] != null && lineodds[3] != null && lineodds[2] == null) {
                    lineodds[2] = oddsarr[1] + " " + oddsarr[2];
                    fanduelodds.add(lineodds);
                    // System.out.println(lineodds[0] + "\t" + lineodds[1] + "\t" + lineodds[2] + "\t" + lineodds[3]);
                    
                    // System.out.printf("%-15s%-15s%-20s%-15s\n",lineodds[0],
                    // lineodds[1],
                    // lineodds[2],
                    // lineodds[3]);
                    // lineodds = new String[4];
                }
            }
        }
        Thread.sleep(5000);
        driver.close();
        return fanduelodds;
    }
}
