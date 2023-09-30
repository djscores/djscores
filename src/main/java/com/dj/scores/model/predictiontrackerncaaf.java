package com.dj.scores.model;

import java.io.BufferedInputStream;
import java.net.URL;

public class predictiontrackerncaaf {
    public static void getncaaf() {
        
        try {
            String url = "https://www.thepredictiontracker.com/ncaapredictions.csv";
            String fileName = "C:/Users/daryl/Downloads/ncaapredictions.csv";
            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
            StringBuilder ncaapredstrbld = new StringBuilder();
            
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                ncaapredstrbld.append(dataBuffer);
                // fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            System.out.println(ncaapredstrbld.toString()); 
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + "\n" + e.getCause());
        }
    }
}
