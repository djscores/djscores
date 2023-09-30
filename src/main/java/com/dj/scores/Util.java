package com.dj.scores;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Util
{
	public static void addToMap(HashMap<String,Double[]> map, String key, String value) {
		Double[] team = (Double[]) map.get(key);
		if (team == null) {
			team = new Double[3];
		}
		if (team[0] == null) {
			if (!"".equals(value))
			team[0] = new Double(Double.parseDouble(value));
		}
		else {
			if (!"".equals(value))
			team[0] = team[0] + Double.parseDouble(value);
		}
		map.put(key,team);
	}
    public static void printMap(HashMap<String,Double[]> map) {
        for(String key : map.keySet()){
            Double[] values = map.get(key);
            // System.out.print(key+"\t");
            // for (Double value : values) {
            //     System.out.print(value+"\t");
            // }
            System.out.println(key+"\t"+values[0]);
        }
    }
    public static void printOpponents(HashMap<String,ArrayList<String>> allOpponents, String team) {
        System.out.println("***"+team+"***");
        for(String key : allOpponents.keySet()) {
            if (key.toLowerCase().contains(team)) {
                ArrayList<String> opponents = allOpponents.get(key);
                if (opponents != null)
                for (String opponent : opponents) {
                    System.out.println(opponent);
                }
            }
        }
    }
    
    public static Double getOpponentAveragePoints(String team, ArrayList<String[]> games) {
        Double pointsTotal = 0D;
        // System.out.println("games.length "+games.length);
        int numGames = 0;
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i)[0] != null) {
                if (games.get(i)[0].equals(team)) {
                    // System.out.println("\t"+games[i][0] + "\t" + games[i][1] + "\t" + games[i][2] + "\t" + games[i][3]);
                    pointsTotal += Double.parseDouble(games.get(i)[3]);
                    numGames++;
                }
                if (games.get(i)[1].equals(team)) {
                    // System.out.println("\t"+games[i][0] + "\t" + games[i][1] + "\t" + games[i][2] + "\t" + games[i][3]);
                    pointsTotal += Double.parseDouble(games.get(i)[2]);
                    numGames++;
                }
            }
        }
        return pointsTotal/numGames;
    }
    public static HashMap<String,String> nbaTeamsTestUrls() {
        HashMap<String,String> teams = new HashMap<String,String>();
        teams.put("Atlanta Hawks","/teams/ATL/");
        return teams;
    }
    public static List<List> readCsv(String fileName) throws IOException {
        ArrayList<List> csvfile = new ArrayList<List>();
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        String line;
        int i = 0;
        ArrayList<String> awayTeam = new ArrayList<String>();
        ArrayList<Integer> awayPoints = new ArrayList<Integer>();
        ArrayList<String> homeTeam = new ArrayList<String>();
        ArrayList<Integer> homePoints = new ArrayList<Integer>();
        ArrayList<Integer> gameTotal = new ArrayList<Integer>();
        ArrayList<Integer> homeMov = new ArrayList<Integer>();

        while ((line = br.readLine()) != null) {
            if (i < 10) {
                System.out.println(line);
            }
            if (i > 0) {
                String[] csvline = line.split(",");
                awayTeam.add(csvline[1]);
                homeTeam.add(csvline[3]);
                awayPoints.add(Integer.parseInt(csvline[2]));
                homePoints.add(Integer.parseInt(csvline[4]));
                gameTotal.add(Integer.parseInt(csvline[2])+Integer.parseInt(csvline[4]));
                homeMov.add(Integer.parseInt(csvline[4])-Integer.parseInt(csvline[2]));
            }
            i++;
        }
        br.close();
        csvfile.add(awayTeam);
        csvfile.add(awayPoints);
        csvfile.add(homeTeam);
        csvfile.add(homePoints);
        csvfile.add(gameTotal);
        csvfile.add(homeMov);
        return csvfile;
    }
    public static List<String[]> scrapeMlbRefGames() throws IOException, ParseException {
        BufferedWriter filewriter = new BufferedWriter(new FileWriter("C:\\Users\\daryl\\work\\points\\mlb.csv"));
        String url = "https://www.baseball-reference.com/leagues/MLB-schedule.shtml";
        System.setProperty("webdriver.chrome.driver","C:\\Users\\daryl\\work\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(90));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(90));
        driver.get(url);
        String gameshtml = driver.getPageSource();
        List<String[]> games = new ArrayList<String[]>();
        Document doc = Jsoup.parse(gameshtml);
        Elements gameElement = doc.getElementsByClass("section_content");
        Elements gameDivs = gameElement.select("div");
        System.out.println("gameDivs "+gameDivs.size());
        DateFormat fmt = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        DateFormat machinedate = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        filewriter.append("Date,Away,Awayscore,Home,Homescore\r\n");
        for (int i = 1; i < gameDivs.size(); i++) {
            Elements day = gameDivs.get(i).select("h3");
            Elements dayGames = gameDivs.get(i).select("p");
            String daystring = day.text();
            if (daystring.contains("Today")) {
                for (Element dayGame : dayGames) {
                    String game = dayGame.text();
                    String[] teams = game.split("@");
                    String away = teams[0].split("pm")[1];
                    String home = teams[1].substring(0,teams[1].indexOf("Preview"));
                    Date today = new Date(System.currentTimeMillis());
                    String formatdate = machinedate.format(today);
                    filewriter.append(formatdate + "," + away.trim() + "," + "," + home.trim() + "," + "\r\n");
                }
                break;
            }
            Date gamedate = fmt.parse(daystring.substring(daystring.indexOf(",")+2, daystring.length()));
            String formatdate = machinedate.format(gamedate);
            // System.out.println("date - "+formatdate);
            // System.out.println("dayGames "+dayGames.size());
            for (Element dayGame : dayGames) {
                String game = dayGame.text();
                if (!game.contains("Standings")) 
                try {
                    String away = game.substring(0,game.indexOf("(")-1);
                    String awayscore = game.substring(game.indexOf("(")+1, game.indexOf(")"));
                    String home = game.substring(game.indexOf("@")+2, game.lastIndexOf("("));
                    String homescore = game.substring(game.lastIndexOf("(")+1, game.lastIndexOf(")"));
                    filewriter.append(formatdate + "," + away + "," + awayscore + "," + home.trim() + "," + homescore+"\r\n");
                    String[] gamearray = {away,awayscore,home,homescore};
                    games.add(gamearray);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println(game);
                }
            }
            // System.out.println("games "+games.size());
        }
        filewriter.close();
        System.out.println("games "+games.size());
        return games;
    }
}
