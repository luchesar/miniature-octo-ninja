package com.github.luchesar.misc.luquidate;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PriceParser {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Price[] parse(File pricesFile) throws IOException {
        if (!pricesFile.exists()) {
            throw new IllegalArgumentException("Trades file doesn't exist");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pricesFile), "UTF-8"))) {
            String line;
            List<Price> prices = new ArrayList<>();

            while((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] columns = line.split(",");
                    long date = parseDate(columns[0]);
                    float bid = parsePrice(columns[1], "bid");
                    float ask = parsePrice(columns[2], "ask");
                    prices.add(new Price(date, bid, ask));
                }
            }
            return prices.toArray(new Price[0]);
        }
    }

    private long parseDate(String date) {
        try {
            return dateFormat.parse(date.replace("\"", "")).getTime();
        } catch(ParseException e) {
            throw new IllegalArgumentException("Price date has wrong format " + date);
        }
    }

    private float parsePrice(String price, String fieldName) {
        try {
            return Float.parseFloat(price);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Price " + fieldName +" is not a float " + price);
        }
    }
}
