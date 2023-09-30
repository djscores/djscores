package com.dj.odds;
import java.io.IOException;
import java.time.Duration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class fanduelmlb {
    public static void getOdds() throws Exception {
        String url = "https://sportsbook.fanduel.com/navigation/mlb";
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
            for (int i = 0; i < labels.size(); i++) {
                System.out.println(labels.get(i).attr("aria-label"));
            
            }
        }
        
        Thread.sleep(5000);
    }
}
