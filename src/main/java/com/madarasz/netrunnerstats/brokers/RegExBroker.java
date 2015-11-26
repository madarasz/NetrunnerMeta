package com.madarasz.netrunnerstats.brokers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handling general regex parsing
 * Created by madarasz on 10/06/15.
 */
@Component
public class RegExBroker {

    private static final Logger logger = LoggerFactory.getLogger(RegExBroker.class);

    private static final String REGEX_CARD_QUANTITY = "^\\d{1}x?";
    private static final String REGEX_FIRST_NUMBER = "\\d+";
    private static final String[] DATE_FORMATS = {"yyyy-MM-dd", "MMMM dd, yyyy", "MM/dd/yyyy", "dd.MM.yyyy"};

    public int getCardQuantity(String line) {
        Pattern pattern = Pattern.compile(REGEX_CARD_QUANTITY);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return Character.getNumericValue(matcher.group(0).charAt(0));
        } else {
            return 0;
        }
    }

    public String getSecondQuantity(String line) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(line);
        if ((matcher.find()) && (matcher.find())) {
            return matcher.group(0);
        } else {
            return "";
        }
    }

    public String getCardFromLine(String line) {
        String[] splits = line.split("\\d{1}x | \\(");
        if (splits[0].isEmpty()) {
            return splits[1];
        } else {
            return splits[0];
        }
    }

    public int getNumberFromBeginning(String line) {
        Pattern pattern = Pattern.compile(REGEX_FIRST_NUMBER); // TODO: make it work for "Top 4"
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(0));
            } catch (Exception e) {
                logger.warn("Couldn't find the first number in line: " + line);
                return -1; // conversion problem
            }
        } else {
            return 0;
        }
    }

    public String sanitizeText(String text) {
        return text.replaceAll("“", "\"").replaceAll("”", "\"").replaceAll("’", "\'");
    }

    public Date parseDate(String datetext) {
        for (String format : DATE_FORMATS) {
            try {
                return new SimpleDateFormat(format).parse(datetext);
            } catch (ParseException e) {
            }
        }

        logger.warn("ERROR - could not format date: " + datetext);
        return new Date(0);
    }

}
