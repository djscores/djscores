package com.dj.scores.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class predictiontrackerncaaf {
    static ArrayList<String[]> linelist = new ArrayList<String[]>();
    public static void getncaaf() {
        try {
            // String url = "https://www.thepredictiontracker.com/predncaa.html";
            String fileName = "C:/Users/daryl/Downloads/ncaapredictions.csv";
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            String fileline;
            System.out.printf("%-20s%-20s%-10s%-10s%-10s%-10s%-10s%-10s\n","away","home","lineopen","line","logistic","median","pctlog","pctmedian");
            int i = 0;
            DecimalFormat df = new DecimalFormat("0.00");
            while ((fileline = reader.readLine()) != null) {
                if (i < 1) {i++; continue;}
                String[] lines = fileline.split(",");
                String away = lines[0];
                String home = lines[1];
                String lineopen = lines[2];
                String line = lines[3];
                //spread
                String logistic = lines[50];
                // if (logisticRegression > 0) { 
                //     logisticRegression = logisticRegression * -1;
                // }
                // else {
                //     logisticRegression = logisticRegression * -1;
                // }
                //percent correct
                String median = lines[63];
                double linedouble = Double.parseDouble(line);
                double logisticdouble = Double.parseDouble(logistic);
                double mediandouble = Double.parseDouble(median);
                // double percentHomeWin = Double.parseDouble(lines[64]);
                // double homecover = (percentHomeCover > .5) ? -((percentHomeCover/(1-percentHomeCover))*100) : (1-percentHomeCover)/percentHomeCover*100;
                // double homewin = (percentHomeWin > .5) ? -((percentHomeWin/(1-percentHomeWin))*100) : (1-percentHomeWin)/percentHomeWin*100;
                
                String percentlog = df.format((linedouble - logisticdouble) / linedouble);
                String percentmedian = df.format((linedouble - mediandouble) / linedouble);
                // System.out.printf("%-20s%-20s%-10s%-10s%-10s%-10s%-10s%-10s\n",away,home,lineopen,line,df.format(logistic),df.format(median),percentlog,percentmedian);
                String[] relevantline = {away,home,lineopen,line,logistic,median,percentlog.replace("∞","0"),percentmedian.replace("∞","0")};
                linelist.add(relevantline);
            }
            Collections.sort(linelist,new Comparator<String[]>() {
                @Override
                public int compare(String[] o1, String[] o2) {
                    return Double.compare(Double.parseDouble(o2[6]), Double.parseDouble(o1[6]));
                }
            });
            for (String[] line : linelist) System.out.printf("%-20s%-20s%-10s%-10s%-10s%-10s%-10s%-10s\n",
                line[0],
                line[1],
                line[2],
                line[3],
                df.format(Double.parseDouble(line[4])),df.format(Double.parseDouble(line[5])),line[6],line[7]);
        }   
        catch (Exception e) {
            e.printStackTrace();
            // System.out.println(e.getMessage() + "\n" + e.getStackTrace()[0] + "\n" + e.getStackTrace()[1]);
        }
    }
}
