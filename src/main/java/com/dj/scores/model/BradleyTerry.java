package com.dj.scores.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class BradleyTerry {

    public static void runBradleyTerry() throws Exception {
        BufferedReader fr = new BufferedReader(new FileReader(new File("C:/Users/daryl/work/djscores/ratings.csv")));
        String line;
        HashMap<String,Double> ratings = new HashMap<String,Double>();
        while ((line = fr.readLine()) != null) {
            String[] ratingLine = line.split(",");
            try {
                ratings.put(ratingLine[0].replaceAll("\"",""),Double.parseDouble(ratingLine[1]));
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        fr.close();
        fr = new BufferedReader(new FileReader(new File("C:/Users/daryl/work/djscores/mlb.csv")));
        Date today = new Date(System.currentTimeMillis());
        DateFormat machinedate = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        String formatToday = machinedate.format(today);
        while ((line = fr.readLine()) != null) {
            String[] todaysGame = line.split(",");
            if (formatToday.compareTo(todaysGame[0]) == 0) {
                String away = todaysGame[1];
                String home = todaysGame[3];
                double awayRating = ratings.get(away);
                double homeRating = ratings.get(home);
                double forecast = forecast(homeRating,awayRating);
                System.out.println(away + " @ " + home + " - " + forecast);
            }
        }

    }

    static public double forecast(double home, double away) {
        return 1/(1+Math.exp(-(home - away)));
    }
}
