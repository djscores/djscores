package com.dj.odds;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class mgm {
    public static ArrayList<String[]> getOdds() throws Exception {
        String url = "https://sports.oh.betmgm.com/en/sports/football-11/betting/usa-9/nfl-35";
        System.setProperty("webdriver.chrome.driver","C:\\Users\\daryl\\work\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(90));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(90));
        driver.get(url);
        String mgmhtml = driver.getPageSource();
        Document doc = Jsoup.parse(mgmhtml);
        
        Elements events = doc.getElementsByClass("grid-event-wrapper has-all-markets image ng-star-inserted");
        // Elements events = doc.select("ms-six-pack-event");
        ArrayList<String[]> mgmodds = new ArrayList<String[]>();
        for (Element event : events) {
            Elements participants = event.getElementsByClass("participant-info");
            Elements optionGroups = event.select("ms-option-group");
            
            // for (int i = 0; i < participants.size(); i++) {
            //     System.out.print(participants.get(i).text()+"\t");
            //     for (int j = i*3; j < i*3+3; j++) {
            //         System.out.print(optionGroups.get(j).text()+"\t");
            //     }
            //     System.out.println();
            // }
            // for (Element detail : eventDetail) {
            //     participants = detail.getElementsByClass("participant");
            // }
            // Elements oddsgroup = event.select("ms-option-group");
            if (participants.size() > 0) {
                String[] odds = {participants.get(0).text(),
                    optionGroups.get(0).select("ms-option").get(0).text(),
                    optionGroups.get(1).select("ms-option").get(0).text(),
                    optionGroups.get(2).select("ms-option").get(0).text()
                };
                mgmodds.add(odds);
                String[] odds2 = {participants.get(1).text(),
                    optionGroups.get(0).select("ms-option").get(1).text(),
                    optionGroups.get(1).select("ms-option").get(1).text(),
                    optionGroups.get(2).select("ms-option").get(1).text()
                };
                mgmodds.add(odds2);
                
                String name = participants.get(0).text();
                String.format("%15s", name);
                // System.out.printf("%-15s%-15s%-15s%-15s\n",name,
                //     optionGroups.get(0).select("ms-option").get(0).text(),
                //     optionGroups.get(1).select("ms-option").get(0).text(),
                //     optionGroups.get(2).select("ms-option").get(0).text());
                // System.out.println(name + "\t" + 
                //     optionGroups.get(0).select("ms-option").get(0).text() + "\t" + 
                //     optionGroups.get(1).select("ms-option").get(0).text() + "\t" + 
                //     optionGroups.get(2).select("ms-option").get(0).text());
                name = participants.get(1).text();
                String.format("%15s", name);
                // System.out.println(name + "\t" + 
                //     optionGroups.get(0).select("ms-option").get(1).text() + "\t" + 
                //     optionGroups.get(1).select("ms-option").get(1).text() + "\t" + 
                //     optionGroups.get(2).select("ms-option").get(1).text());
                // System.out.printf("%-15s%-15s%-15s%-15s\n",name,
                //     optionGroups.get(0).select("ms-option").get(1).text(),
                //     optionGroups.get(1).select("ms-option").get(1).text(),
                //     optionGroups.get(2).select("ms-option").get(1).text());
            }
        }
        Thread.sleep(9000);
        driver.close();
        return mgmodds;
    }
}
