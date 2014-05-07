The current solution should be able to handle couple of millions of trades and ~100K prices per second on a single thread on a commodity hardware with 1GB Max heap size.

I should be able to load few millions of trades and process millions of prices for less then a minute.

Every price takes O(log(N)) where N is the number of trades.