package com.github.luchesar.misc.luquidate;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Liquidator {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    private final PrintStream out;

    public Liquidator(PrintStream out) {
        this.out = out;
    }

    public void liquidate(Trade[] buy, Trade[] sell, Price[] prices) {
        for (Price price: prices) {
            int split = tradesStopPriceLower(buy, price.getBid());
            buy = Arrays.copyOfRange(buy, split, buy.length);
            liquidate(price.getTime(), Arrays.copyOfRange(buy, 0, split), price.getBid());

            int split1 = tradesStopPriceHigher(sell, price.getAsk());
            sell = Arrays.copyOfRange(sell, split1, sell.length);
            liquidate(price.getTime(), Arrays.copyOfRange(sell, 0, split1), price.getAsk());
        }
    }

    private void liquidate(long date, Trade[] trades, float price) {
        for (Trade trade: trades) {
            out.println(trade.getId() + " " + dateFormat.format(new Date(date)) + " " + price);
        }
    }

    private int tradesStopPriceLower(Trade[] trades, float stopPrice) {
        return -1;
    }

    private int tradesStopPriceHigher(Trade[] trades, float stopPrice) {
        return -1;
    }
}
