package com.dj.scores.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class predictiontrackernfl {
    public static void getnfl() {
        
        try {
            // String url = "https://www.thepredictiontracker.com/nflpredictions.csv";
            String fileName = "C:/Users/daryl/Downloads/nflpredictions.csv";
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            ArrayList<String[]> linelist = new ArrayList<String[]>();
            String line;
            System.out.printf("%-15s%-15s%-15s%-15s%-15s\n","away","home","averageline","homecover","homewin");
            int i = 0;
            while ((line = reader.readLine()) != null) {
                if (i < 1) {i++; continue;}
                String[] lines = line.split(",");
                String away = lines[0];
                String home = lines[1];
                double averageline = Double.parseDouble(lines[60]);
                if (averageline > 0) { 
                    averageline = averageline * -1;
                }
                else {
                    averageline = averageline * -1;
                }
                double percentHomeCover = Double.parseDouble(lines[63]);
                double percentHomeWin = Double.parseDouble(lines[64]);
                                
                double homecover = (percentHomeCover > .5) ? -((percentHomeCover/(1-percentHomeCover))*100) : (1-percentHomeCover)/percentHomeCover*100;
                double homewin = (percentHomeWin > .5) ? -((percentHomeWin/(1-percentHomeWin))*100) : (1-percentHomeWin)/percentHomeWin*100;
                DecimalFormat df = new DecimalFormat("0.00");
                System.out.printf("%-15s%-15s%-15s%-15s%-15s\n",away,home,df.format(averageline),df.format(homecover),df.format(homewin));
                linelist.add(lines);
            }
            
            System.out.println();
        }
        catch (Exception e) {
            e.printStackTrace();
            // System.out.println(e.getMessage() + "\n" + e.getStackTrace()[0] + "\n" + e.getStackTrace()[1]);
        }
    }
    public static String[][] getnflLogisticRegression() throws IOException {
        String url = "https://www.thepredictiontracker.com/fb/ranks.logit.txt";
        Document doc = Jsoup.connect(url).get();
        doc = doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
        String s = doc.html().replaceAll("\\\\n", "\n");
        String[] sbody = s.split("<body>")[1].split("</body>");
        String[] ratings = sbody[0].split("\n");
        System.out.println(ratings.length);
        String[][] teamratings = new String[2][ratings.length];
        for (int i = 0 ; i < ratings.length; i++) {
            if (!ratings[i].contains("-")) {
                teamratings[0][i] = ratings[i].substring(4,20).trim();
                teamratings[1][i] = ratings[i].substring(20,ratings[i].length()-1);
            }
            else {
                teamratings[0][i] = ratings[i].substring(4,19).trim();
                teamratings[1][i] = ratings[i].substring(19,ratings[i].length()-1);
            }
        }
        System.out.println(teamratings.toString());
        return teamratings;
    }
}
