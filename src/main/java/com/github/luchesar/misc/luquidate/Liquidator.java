package com.github.luchesar.misc.luquidate;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Liquidator {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final PrintStream out;

    public Liquidator(PrintStream out) {
        this.out = out;
    }

    public void liquidate(Trade[] buy, Trade[] sell, Price[] prices) {
        for (Price price: prices) {
            int split = tradesStopPriceLower(buy, price.getBid());
            Trade[] tmpBuy = Arrays.copyOfRange(buy, 0, split);
            liquidate(price.getTime(), Arrays.copyOfRange(buy, split, buy.length), price.getBid());
            buy = tmpBuy;

            int sellSplit = tradesStopPriceHigher(sell, price.getAsk());
            Trade[] tmpSell = Arrays.copyOfRange(sell, sellSplit, sell.length);
            liquidate(price.getTime(), Arrays.copyOfRange(sell, 0, sellSplit), price.getAsk());
            sell = tmpSell;
        }
    }

    private void liquidate(long date, Trade[] trades, float price) {
        for (Trade trade: trades) {
            out.println(trade.getId() + " \"" + dateFormat.format(new Date(date)) + "\" " + String.format("%f", price));
        }
    }

    private int tradesStopPriceLower(Trade[] trades, float stopPrice) {
        for (int i = 0; i < trades.length; i++) {
            Trade trade = trades[i];
            if (trade.getStopPrice() > stopPrice) {
                return i;
            }
        }
        return trades.length;
    }

    private int tradesStopPriceHigher(Trade[] trades, float stopPrice) {
        for (int i = 0; i < trades.length; i++) {
            Trade trade = trades[i];
            if (trade.getStopPrice() > stopPrice) {
                return i;
            }
        }
        return trades.length;
    }
}
