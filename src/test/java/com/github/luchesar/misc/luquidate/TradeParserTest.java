package com.github.luchesar.misc.luquidate;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class TradeParserTest {
    TradeParser parser = new TradeParser();

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void parseEmptyTrade() throws Exception {
        assertTradesParsed(new Trade[0], new Trade[0], "");
    }

    @Test
    public void parseBrokenTrade() throws Exception {
        try {
            assertTradesParsed(new Trade[0], new Trade[0], "fakeId,\"Sell\",673.04696,60.0000");
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Trade id is not an integer");
        }
        try {
            assertTradesParsed(new Trade[0], new Trade[0], "1,\"sdfsd\",673.04696,60.0000");
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Trade type is not Buy|Sell");
        }
        try {
            assertTradesParsed(new Trade[0], new Trade[0], "1,\"Sell\",sdf,60.0000");
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Trade opening price is not a float");
        }
        try {
            assertTradesParsed(new Trade[0], new Trade[0], "1,\"Sell\",673.04696,aower");
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Trade margin is not a float");
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseMissingFile() throws Exception {
        parser.parse(new File("/some/missing/file"));
    }

    @Test
    public void  parseBuyTrade() throws Exception {
        assertTradesParsed(
                new Trade[]{new Trade(5, (float)(673.04696 - 60.0))},
                new Trade[0],
                "5,\"Buy\",673.04696,60.0000");
    }

    @Test
    public void  parseSellTade() throws Exception {
        assertTradesParsed(
                new Trade[0],
                new Trade[]{new Trade(100, (float)(673.04696 + 60.0))},
                "100,\"Sell\",673.04696,60.0000");
    }

    @Test
    public void  parseTrades() throws Exception {
        assertTradesParsed(
                new Trade[]{
                        new Trade(1, (float)(10.111 - 1.125)),
                        new Trade(3, (float)(10.113 - 1.325)),
                        new Trade(5, (float)(10.115 - 1.525)),
                        new Trade(6, (float)(10.116 - 1.625))
                },
                new Trade[]{
                        new Trade(2, (float)(10.112 + 1.225)),
                        new Trade(4, (float)(10.114 + 1.425)),
                        new Trade(7, (float)(10.117 + 1.725)),
                        new Trade(8, (float)(10.118 + 18625))
                },
                        "1,\"Buy\",10.111,1.125\n" +
                        "2,\"Sell\",10.112,1.225\n" +
                        "3,\"Buy\",10.113,1.325\n\n" +
                        "4,\"Sell\",10.114,1.425\n" +
                        "5,\"Buy\",10.115,1.525\n" +
                        "6,\"Buy\",10.116,1.625\n\n" +
                        "7,\"Sell\",10.117,1.725\n" +
                        "8,\"Sell\",10.118,1.825\n"
        );
    }

    private void assertTradesParsed(Trade[] expectedBuy, Trade[] expectedSell, String toParse) throws Exception {
        File tradesCsv = tmpFolder.newFile("trade.scv");
        FileUtils.write(tradesCsv, toParse);

        TradeParser.Trades trades = parser.parse(tradesCsv);
        assertThat(trades.getBuy()).isEqualTo(expectedBuy);
        assertThat(trades.getSell()).isEqualTo(expectedSell);
    }

}
