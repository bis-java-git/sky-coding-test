package uk.sky;

import org.junit.Test;

import java.io.*;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataFiltererTest {

    public static final String SRC_TEST_RESOURCES_SINGLE_LINE = "src/test/resources/single-line";

    public static final String SRC_TEST_RESOURCES_MULTI_LINES = "src/test/resources/multi-lines";

    public static final String SRC_TEST_RESOURCES_EMPTY = "src/test/resources/empty";

    @Test
    public void whenEmpty() throws IOException {
        FileReader reader = openFile(SRC_TEST_RESOURCES_EMPTY);
        assertTrue(DataFilterer.filterByCountry(reader, "GB").isEmpty());
        closeFile(reader);
    }

    @Test
    public void whenCountryCodeFigurePresent() throws IOException {
        FileReader reader = openFile(SRC_TEST_RESOURCES_SINGLE_LINE);

        List<DataFilterer.LogDetail> filterList = (List<DataFilterer.LogDetail>) DataFilterer.filterByCountry(reader, "GB");
        assertEquals(filterList.size(), 1);
        assertEquals(filterList.get(0).getCountryCode(), "GB");
        closeFile(reader);

        reader = openFile(SRC_TEST_RESOURCES_MULTI_LINES);
        assertEquals(DataFilterer.filterByCountry(reader, "GB").size(), 1);
        closeFile(reader);

        reader = openFile(SRC_TEST_RESOURCES_MULTI_LINES);
        assertEquals(DataFilterer.filterByCountry(reader, "US").size(), 3);
        closeFile(reader);

        reader = openFile(SRC_TEST_RESOURCES_MULTI_LINES);
        assertEquals(DataFilterer.filterByCountry(reader, "DE").size(), 1);
        closeFile(reader);
    }

    @Test
    public void whenCountryCodeFigureNotPresent() throws IOException {
        FileReader reader = openFile(SRC_TEST_RESOURCES_SINGLE_LINE);
        assertTrue(DataFilterer.filterByCountry(reader, "GB2").isEmpty());
        closeFile(reader);
    }

    @Test
    public void whenCountryCodeFigurePresentAndResponseTimeAboveLimit() throws IOException {
        FileReader reader = openFile(SRC_TEST_RESOURCES_MULTI_LINES);
        assertEquals(DataFilterer.filterByCountryWithResponseTimeAboveLimit(reader, "US", 700).size(), 2);
        closeFile(reader);
    }

    @Test
    public void whenCountryCodeFigurePresentAndResponseTimeAboveLimitNotPresent() throws IOException {
        FileReader reader = openFile(SRC_TEST_RESOURCES_MULTI_LINES);
        assertTrue(DataFilterer.filterByCountryWithResponseTimeAboveLimit(reader, "US", 1000).isEmpty());
        closeFile(reader);
    }

    @Test
    public void whenFilterByResponseTimeAboveAverage() throws IOException {
        FileReader reader = openFile(SRC_TEST_RESOURCES_MULTI_LINES);
        assertEquals(DataFilterer.filterByResponseTimeAboveAverage(reader).size(), 3);
        closeFile(reader);
    }

    private FileReader openFile(String filename) throws FileNotFoundException {
        return new FileReader(new File(filename));
    }

    private void closeFile(Reader reader) throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
