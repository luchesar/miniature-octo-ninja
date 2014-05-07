package com.github.luchesar.misc.luquidate;

import java.util.Arrays;

/**
 * A lightweight in memory representation of a trade that only takes 3*32+ 64 = 160
 * bytes along with the reference to the price instance. This makes ~153MB per 1M Prices
 * so no problem with scaling up to few millions of trades.
 */
public class Price {
    private final long time;
    private final float bid;
    private final float ask;

    public Price(long time, float bid, float ask) {
        this.time = time;
        this.bid = bid;
        this.ask = ask;
    }

    public long getTime() {
        return time;
    }

    public float getBid() {
        return bid;
    }

    public float getAsk() {
        return ask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Price)) return false;

        Price price = (Price) o;

        if (Float.compare(price.ask, ask) != 0) return false;
        if (Float.compare(price.bid, bid) != 0) return false;
        if (time != price.time) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{time, bid, ask});
    }

    @Override
    public String toString() {
        return Arrays.toString(new Object[]{time, bid, ask});
    }
}
