package com.github.luchesar.misc.luquidate;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class LiquidatorMain {
    /**
     * The first arguments is the trades.scv and the second arg is the prices.scv
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void execute(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Please specify the trades.csv and the prices.csv as first and second parameters");
        }
        File tradesFile = new File(args[0]);
        File pricesFile = new File(args[1]);
        if (!tradesFile.exists()) {
            throw new IllegalArgumentException("The trades file " + args[0] + " is missing");
        }
        if (!pricesFile.exists()) {
            throw new IllegalArgumentException("The prices file " + args[1] + " is missing");
        }

        liquidate(tradesFile, pricesFile);
    }

    private static void liquidate(File tradesFile, File pricesFile) throws IOException {
        TradeParser.Trades trades = new TradeParser().parse(tradesFile);

        // Load all the trades into the memory and sort them on the stop price. Having them sorted will
        // give the ability to liquidate trades with a linear complexity O(Number of trades) for each new price.
        Trade[] buyTrades = sortTradesByStopPrice(trades.getBuy());
        Trade[] sellTrades = sortTradesByStopPrice(trades.getSell());

        // Load all the prices into the memory
        Price[] prices = new PriceParser().parse(pricesFile);

        // Sort the prices chronologically so we can feed them to the Liquidator
        Arrays.sort(prices, Ordering.natural().onResultOf(new Function<Price, Long>() {
            @Override
            public Long apply(Price trade) {
                return trade.getTime();
            }
        }));

        new Liquidator(System.out).liquidate(buyTrades, sellTrades, prices);
    }

    private static Trade[] sortTradesByStopPrice(Trade[] trades) {
        Arrays.sort(trades, Ordering.natural().onResultOf(new Function<Trade, Float>() {
            @Override
            public Float apply(Trade trade) {
                return trade.getStopPrice();
            }
        }));

        return trades;
    }
}