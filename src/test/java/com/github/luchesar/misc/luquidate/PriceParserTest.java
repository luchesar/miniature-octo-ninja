package com.github.luchesar.misc.luquidate;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.text.SimpleDateFormat;

import static org.fest.assertions.Assertions.assertThat;

public class PriceParserTest {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private PriceParser parser = new PriceParser();

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void parseEmptyPrice() throws Exception {
        assertPriceParsed(new Price[0], "");
    }

    @Test
    public void parseBrokenPrice() throws Exception {
        try {
            assertPriceParsed(new Price[0], "\"20sdfsf\",388.31000000,390.01000000");
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Price date has wrong format");
        }
        try {
            assertPriceParsed(new Price[0], "\"2014-04-10 21:55:40\",sdf,390.01000000");
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Price bid is not a float");
        }
        try {
            assertPriceParsed(new Price[0], "\"2014-04-10 21:55:40\",388.31000000,asdfsdf");
            Assert.fail("Exception expected");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Price ask is not a float");
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseMissingFile() throws Exception {
        parser.parse(new File("/some/missing/file"));
    }

    @Test
    public void parseSinglePrice() throws Exception {
        assertPriceParsed(new Price[] {
                new Price(dateFormat.parse("2014-04-10 21:55:40").getTime(),388.31000000f,390.01000000f )
        }, "\"2014-04-10 21:55:40\",388.31000000,390.01000000");
    }

    @Test
    public void parseMultiplePrice() throws Exception {
        assertPriceParsed(
                new Price[] {
                    new Price(dateFormat.parse("2018-04-10 21:55:40").getTime(), 1.1f, 1.2f ),
                    new Price(dateFormat.parse("2014-04-10 22:55:40").getTime(), 2.1f, 2.2f),
                    new Price(dateFormat.parse("2013-04-10 23:55:40").getTime(), 3.1f, 3.2f )
                },
                "\"2018-04-10 21:55:40\",1.1,1.2\n" +
                "\"2014-04-10 22:55:40\",2.1,2.2\n" +
                "\"2013-04-10 23:55:40\",3.1,3.2\n"

        );
    }

    private void assertPriceParsed(Price[] expected,  String toParse) throws Exception {
        File pricesCsv = tmpFolder.newFile("prices.scv");
        FileUtils.write(pricesCsv, toParse);

        Price[] prices = parser.parse(pricesCsv);
        assertThat(prices).isEqualTo(expected);
    }
}