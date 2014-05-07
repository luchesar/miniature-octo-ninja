package com.github.luchesar.misc.luquidate;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class LiquidatorMainTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void noArgs() throws Exception {
        try {
            invokeLiquidator(args());
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Please specify the trades.csv and the prices.csv as first and second parameters");
        }
    }

    @Test
    public void missingTradesFile() throws Exception {
        File pricesFile = tmpFolder.newFile("prices.csv");
        FileUtils.write(pricesFile, "");

        try {
            invokeLiquidator(args("missing/file", pricesFile.getAbsolutePath()));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("trades file missing/file is missing");
        }
    }

    @Test
    public void missingPricesFile() throws Exception {
        File tradesFile = tmpFolder.newFile("trades.csv");
        FileUtils.write(tradesFile, "");

        try {
            invokeLiquidator(args(tradesFile.getAbsolutePath(), "missing/file"));
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("prices file missing/file is missing");
        }
    }

    @Test
    public void errorParsingTradesFile() throws Exception {
        try {
            assertLiquidated(args("17 \"2014-04-11 01:31:33\" 17.900000"),
                    "17,\"Buydfs\",20.00,1.00", "\"2014-04-11 01:31:33\",17.90000000,350.00000000");
            Assert.fail("RuntimeException expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("type is not Buy|Sell");
        }
    }

    @Test
    public void errorParsingPricesFile() throws Exception {
        try {
            assertLiquidated(args("17 \"2014-04-11 01:31:33\" 17.900000"),
                    "17,\"Buy\",20.00,1.00", "\"2014-04-11 01:31:33\",sdfs,350.00000000");
            Assert.fail("RuntimeException expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("bid is not a float sdfs");
        }
    }

    @Test
    public void singleBuyTradeLiquidated() throws Exception {
        assertLiquidated(args("17 \"2014-04-11 01:31:33\" 17.900000"), "17,\"Buy\",20.00,1.00", "\"2014-04-11 01:31:33\",17.90000000,350.00000000");
    }

    @Test
    public void singleSellTradeLiquidated() throws Exception {
        assertLiquidated(args("17 \"2014-04-11 01:31:33\" 21.100000"), "17,\"Sell\",20.00,1.00", "\"2014-04-11 01:31:33\",17.999,21.100000");
    }

    @Test
    public void oneOfThreeBuyTradesLiquidated() throws Exception {
        assertLiquidated(
                args("17 \"2014-04-11 01:31:33\" 17.900000"),
                "17,\"Buy\",20.00,1.00\n" +
                        "18,\"Buy\",20.00,3.00" +
                        "19,\"Buy\",20.00,4.00",
                "\"2014-04-11 01:31:33\",17.90,350.00000000"
        );
    }

    @Test
    public void oneOfThreeSellTradesLiquidated() throws Exception {
        assertLiquidated(
                args("17 \"2014-04-11 01:31:33\" 21.100000"),
                "17,\"Sell\",20.00,1.00\n" +
                        "18,\"Sell\",20.00,2.00\n" +
                        "19,\"Sell\",20.00,3.00",
                "\"2014-04-11 01:31:33\",17.999,21.100000"
        );
    }

    @Test
    public void buyTradesLiquidatedAtDifferentTimes() throws Exception {
        assertLiquidated(
                args("17 \"2014-04-11 01:31:33\" 17.900000",
                        "18 \"2014-04-11 01:32:33\" 16.900000",
                        "19 \"2014-04-11 01:33:33\" 15.900000"),
                "17,\"Buy\",20.00,1.00\n" +
                        "18,\"Buy\",20.00,3.00\n" +
                        "19,\"Buy\",20.00,4.00",
                "\"2014-04-11 01:32:33\",16.90,350.00000000\n" +
                        "\"2014-04-11 01:31:33\",17.90,350.00000000\n" +
                        "\"2014-04-11 01:33:33\",15.90,350.00000000\n"
        );
    }

    @Test
    public void sellTradesLiquidatedAtDifferentTimes() throws Exception {
        assertLiquidated(
                args("17 \"2014-04-11 01:31:33\" 21.100000",
                        "18 \"2014-04-11 01:32:33\" 22.100000",
                        "19 \"2014-04-11 01:33:33\" 23.100000"),
                "17,\"Sell\",20.00,1.00\n" +
                        "18,\"Sell\",20.00,2.00\n" +
                        "19,\"Sell\",20.00,3.00",
                "\"2014-04-11 01:33:33\",17.999,23.100000\n" +
                        "\"2014-04-11 01:31:33\",17.999,21.100000\n" +
                        "\"2014-04-11 01:32:33\",17.999,22.100000"
        );
    }

    @Test
    public void combinedCase() throws Exception {
        assertLiquidated(
                args("1 \"2014-04-11 01:31:33\" 17.900000",
                        "2 \"2014-04-11 01:32:33\" 16.900000",
                        "3 \"2014-04-11 01:33:33\" 15.900000",
                        "5 \"2014-04-11 01:31:00\" 21.100000",
                        "6 \"2014-04-11 01:32:00\" 22.100000",
                        "7 \"2014-04-11 01:33:00\" 23.100000"),
                "1,\"Buy\",20.00,1.00\n" +
                        "2,\"Buy\",20.00,3.00\n" +
                        "3,\"Buy\",20.00,4.00\n" +
                        "4,\"Buy\",20.00,19.00\n" +
                        "5,\"Sell\",20.00,1.00\n" +
                        "6,\"Sell\",20.00,2.00\n" +
                        "7,\"Sell\",20.00,3.00\n" +
                        "8,\"Sell\",20.00,10000.00\n",
                "\"2014-04-11 01:32:33\",16.90,3.00000000\n" +
                        "\"2014-04-11 01:31:33\",17.90,3.00000000\n" +
                        "\"2014-04-11 01:33:33\",15.90,3.00000000\n" +
                        "\"2014-04-11 01:33:00\",170.999,23.100000\n" +
                        "\"2014-04-11 01:31:00\",170.999,21.100000\n" +
                        "\"2014-04-11 01:32:00\",170.999,22.100000"
        );
    }

    private void assertLiquidated(String[] out,
                                  String tradeFile,
                                  String priceFile) throws Exception {
        File tradesFile = tmpFolder.newFile("trades.csv");
        File pricesFile = tmpFolder.newFile("prices.csv");
        FileUtils.write(tradesFile, tradeFile);
        FileUtils.write(pricesFile, priceFile);

        Output outputs = invokeLiquidator(args(tradesFile.getAbsolutePath(), pricesFile.getAbsolutePath()));

        if (out.length > 0) {
            List<String> resultList = IOUtils.readLines(new StringReader(outputs.out));
            String[] result = resultList.toArray(new String[resultList.size()]);
            Arrays.sort(result);
            Arrays.sort(out);
            assertThat(result).isEqualTo(out);
        } else {
            assertThat(outputs.out).isEmpty();
        }
        assertThat(outputs.err).isEmpty();
    }

    private Output invokeLiquidator(String... args) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));

        try {
            LiquidatorMain.execute(args);

            return new Output(out.toString("UTF-8"), err.toString("UTF-8"));
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    private String[] args(String... args) {
        return args;
    }

    private class Output {
        String out;
        String err;

        private Output(String out, String err) {
            this.out = out;
            this.err = err;
        }
    }
}
