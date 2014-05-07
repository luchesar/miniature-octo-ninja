package com.github.luchesar.misc.luquidate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fest.util.Strings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;

import static org.fest.assertions.Assertions.assertThat;

public class LiquidatorMainTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void noArgs() throws Exception {

    }

    @Test
    public void missingFilesArgs() throws Exception {

    }

    @Test
    public void moreThenTwoArgs() throws Exception {

    }

    @Test
    public void errorParsingTradesFile() throws Exception {

    }

    @Test
    public void errorParsingPricesFile() throws Exception {

    }

    @Test
    public void singleTradeLiquidated() throws Exception {

    }

    @Test
    public void singleTradeNotLiquidated() throws Exception {

    }

    @Test
    public void threeTradesLiquidated() throws Exception {

    }

    @Test
    public void subsetTradesLiquidated() throws Exception {

    }

    private void assertLiquidatedSingleLine(String[] output,
                                            String errContains,
                                            String trade,
                                            String price) throws Exception {
        File tradesFile = tmpFolder.newFile("trades.csv");
        File pricesFile = tmpFolder.newFile("prices.csv");
        FileUtils.write(tradesFile, trade);
        FileUtils.write(pricesFile, price);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));

        try {
            LiquidatorMain.main(args(tradesFile.getAbsolutePath(), pricesFile.getAbsolutePath()));

            String outStr = out.toString("UTF-8");
            if (output.length > 0) {
                assertThat(IOUtils.readLines(new StringReader(outStr))).containsExactly(output);
            } else {
                assertThat(outStr).isEmpty();
            }
            String errStr = err.toString("UTF-8");
            if (!Strings.isEmpty(errContains)) {
                assertThat(errStr).contains(errContains);
            } else {
                assertThat(errStr).isEmpty();
            }
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    private String[] args(String... args) {
        return args;
    }
}
