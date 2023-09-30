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

public class draftkingsnfl {
    
    public static ArrayList<String[]> getOdds() throws Exception {
        ArrayList<String[]> draftkingsodds = new ArrayList<String[]>();
        String url = "https://sportsbook.draftkings.com/leagues/football/nfl";
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
                if (name.contains(" "))
                    name = name.split(" ")[1];
                
                String[] lineodds = { name,
                    team.select("td").get(0).text(),
                    team.select("td").get(1).text(),
                    team.select("td").get(2).text(),
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
