package uk.sky;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataFilterer {

    final static Logger logger = (Logger) LoggerFactory.getLogger(DataFilterer.class);

    private static List<LogDetail> readLogDetails(final Reader source) {
        List<LogDetail> logList = new ArrayList<>();
        String line;
        boolean ignoreFirstLine = true;

        try (BufferedReader reader = new BufferedReader(source)) {
                while ((line = reader.readLine()) != null) {
                    if (!ignoreFirstLine) {
                        String[] tokens = line.split(",");
                        if (tokens != null && tokens.length == 3) {
                            logList.add(new LogDetail(tokens[0], tokens[1], tokens[2]));
                        }
                    }
                    ignoreFirstLine = false;

                }
            } catch (IOException e) {
            logger.error("Data error has occured {}", e);
        }
        return logList;
    }

    public static Collection<?> filterByCountry(Reader source, String country) {
        return readLogDetails(source).stream().filter(l -> l.countryCode.equals(country)).collect(Collectors.toList());
    }

    public static Collection<?> filterByCountryWithResponseTimeAboveLimit(Reader source, String country, long limit) {
        return readLogDetails(source).stream().filter(l -> l.countryCode.equals(country) && Long.parseLong(l.responseTime) > limit).collect(Collectors.toList());
    }

    public static Collection<?> filterByResponseTimeAboveAverage(Reader source) {
        List<LogDetail> logDetails =  readLogDetails(source);
        final OptionalDouble average = logDetails.stream().mapToDouble(l -> Long.parseLong(l.responseTime)).average();
        return logDetails.stream().filter(l -> new Double(l.responseTime) > average.getAsDouble()).collect(Collectors.toList());
    }

     static class LogDetail {

        private final String requestTimeStamp;
        private final String countryCode;
        private final String responseTime;

        LogDetail(final String requestTimeStamp, final String countryCode, final String responseTime) {
            this.requestTimeStamp = requestTimeStamp;
            this.countryCode = countryCode;
            this.responseTime = responseTime;
        }

         public String getRequestTimeStamp() {
             return requestTimeStamp;
         }

         public String getCountryCode() {
             return countryCode;
         }

         public String getResponseTime() {
             return responseTime;
         }
     }
}