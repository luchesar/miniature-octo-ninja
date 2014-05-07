package com.github.luchesar.misc.luquidate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TradeParser {
    public Trades parse(File trades) throws IOException {
        if (!trades.exists()) {
            throw new IllegalArgumentException("Trades file doesn't exist");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(trades), "UTF-8"))) {
            String line;
            List<Trade> buyTrades = new ArrayList<>();
            List<Trade> sellTrades = new ArrayList<>();
            while((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] columns = line.split(",");
                    switch (parseType(columns[1])) {
                        case Buy :
                            float startPrice = parsePrice(columns[2], "startPrice");
                            float margin = parsePrice(columns[3], "margin");
                            buyTrades.add(new Trade(parseId(columns[0]),  startPrice - margin));
                            break;
                        case Sell :
                            float sellStartPrice = parsePrice(columns[2], "opening price");
                            float sellMargin = parsePrice(columns[3], "margin");
                            sellTrades.add(new Trade(parseId(columns[0]),  sellStartPrice + sellMargin));
                    }
                }
            }
            return new Trades(buyTrades.toArray(new Trade[0]), sellTrades.toArray(new Trade[0]));
        }

    }

    private enum Type {
        Buy, Sell
    }

    private int parseId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Trade id is not an integer " + id);
        }
    }

    private Type parseType(String type) {
        try {
            return Type.valueOf(type.replace("\"", ""));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trade type is not Buy|Sell " + type);
        }
    }

    private float parsePrice(String price, String fieldName) {
        try {
            return Float.parseFloat(price);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Trade " + fieldName +" is not a float " + price);
        }
    }

    public static class Trades {
        private final Trade[] buy;
        private final Trade[] sell;

        public Trades(Trade[] buy, Trade[] sell) {
            this.buy = buy;
            this.sell = sell;
        }

        public Trade[] getSell() {
            return sell;
        }

        public Trade[] getBuy() {
            return buy;
        }
    }
}
