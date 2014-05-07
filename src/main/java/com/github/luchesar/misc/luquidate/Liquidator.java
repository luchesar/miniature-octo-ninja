package com.github.luchesar.misc.luquidate;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class Liquidator {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final PrintStream out;

    public Liquidator(PrintStream out) {
        this.out = out;
    }

    public void liquidate(Trade[] buy, Trade[] sell, Price[] prices) {
        for (Price price: prices) {
            int split = stopIndex(buy, price.getBid());
            Trade[] tmpBuy = Arrays.copyOfRange(buy, 0, split);
            liquidate(price.getTime(), Arrays.copyOfRange(buy, split, buy.length), price.getBid());
            buy = tmpBuy;

            int sellSplit = stopIndex(sell, price.getAsk());
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

    private int stopIndex(Trade[] trades, float stopPrice) {
        return binarySearchIndex(trades, stopPrice, 0, trades.length);
    }

    private int binarySearchIndex(Trade[] trades, float stopPrice, int offset, int length) {
        if (length == 0) {
            return offset;
        }
        int index = offset + length / 2;
        Trade trade = trades[index];
        Trade previous = null;
        if (index > 0) {
            previous = trades[index - 1];
        }
        if (trade.getStopPrice() > stopPrice) {
            if ((previous == null || previous.getStopPrice() <= stopPrice)) {
                return index;
            }
            return binarySearchIndex(trades, stopPrice, offset, length/2);
        }
        return binarySearchIndex(trades, stopPrice, index + 1, (length - 1)/2);
    }
}
