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

public class draftkingsncaaf {
    
    public static ArrayList<String[]> getOdds() throws Exception {
        ArrayList<String[]> draftkingsodds = new ArrayList<String[]>();
        String url = "https://sportsbook.draftkings.com/leagues/football/ncaaf";
        System.setProperty("webdriver.chrome.driver","C:\\Users\\daryl\\work\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(90));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(90));
        driver.get(url);
        String mgmhtml = driver.getPageSource();
        Document doc = Jsoup.parse(mgmhtml);
        Elements games = doc.select("tbody");
        for (Element game : games) {
            Elements teams = game.select("tr");
            for (Element team : teams) {
                // System.out.println(team.text());
                String name = team.getElementsByClass("event-cell__name-text").text();
                String odds0 = team.select("td").get(0).text();
                String odds1 = team.select("td").get(1).text();
                String odds2 = team.select("td").size() > 1 ? team.select("td").get(2).text() : "";

                // if (odds0.charAt(0) != '+') odds0 = "-"+odds0.substring(1, odds0.length()-1);
                // if (odds1.charAt(0) != '+') odds1 = "-"+odds1.substring(1, odds1.length()-1);
                // if (odds2.charAt(0) != '+') odds2 = "-"+odds2.substring(1, odds2.length()-1);

                String[] lineodds = { name,
                    odds0,
                    odds1,
                    odds2,
                };
                draftkingsodds.add(lineodds);
                // System.out.println(teamshort + "\t" + 
                //     team.select("td").get(0).text() + "\t" +
                //     team.select("td").get(1).text() + "\t" +
                //     team.select("td").get(2).text() + "\t");
                // System.out.printf("%-15s%-15s%-15s%-15s\n",
                //     lineodds[0],
                //     lineodds[1],
                //     lineodds[2],
                //     lineodds[3]);
                
            }
        }
        Thread.sleep(5000);
        driver.close();
        return draftkingsodds;
    }
}
