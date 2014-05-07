package com.github.luchesar.misc.luquidate;

import java.util.Arrays;

/**
 * A lightweight in memory representation of a trade that only takes 3*32 = 96
 * bytes along with the reference to the trade instance. This makes ~122MB per 1M Trades
 * so no problem with scaling up to few millions of trades.
 */
public class Trade {
    private final int id;
    private final float stopPrice;

    public Trade(int id, float stopPrice) {
        this.id = id;
        this.stopPrice = stopPrice;
    }

    public int getId() {
        return id;
    }

    public float getStopPrice() {
        return stopPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trade)) return false;

        Trade trade = (Trade) o;

        if (id != trade.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return Arrays.toString(new Object[]{id, stopPrice});
    }
}