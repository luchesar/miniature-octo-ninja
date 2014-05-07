package com.github.luchesar.misc.luquidate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public class LiquidatorMain {
    /**
     * The first arguments is the trades.scv and the second arg is the prices.scv
     * @param args
     */
    public static void main(String[] args) {
        File tradesFile = new File(args[0]);
        File pricesFile = new File(args[1]);

        try {
            liquidate(tradesFile, pricesFile);
        } catch (Exception e) {
            System.exit(1);
        }
    }

    private static void liquidate(File tradesFile, File pricesFile) throws IOException {
        TradeParser.Trades trades = new TradeParser().parse(tradesFile);
        Trade[] buyTrades = sortTradesByStopPrice(trades.getBuy());
        Trade[] sellTrades = sortTradesByStopPrice(trades.getSell());

        Price[] prices = new PriceParser().parse(pricesFile);

        Arrays.sort(prices, Comparator.comparing(new Function<Price, Long>() {
            @Override
            public Long apply(Price trade) {
                return trade.getTime();
            }
        }));

        new Liquidator(System.out).liquidate(buyTrades, sellTrades, prices);
    }

    private static Trade[] sortTradesByStopPrice(Trade[] trades) {
        Arrays.sort(trades, Comparator.comparing(new Function<Trade, Float>() {
            @Override
            public Float apply(Trade trade) {
                return trade.getStopPrice();
            }
        }));

        return trades;
    }
}