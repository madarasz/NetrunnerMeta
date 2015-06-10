package com.madarasz.netrunnerstats.brokers;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by madarasz on 10/06/15.
 */
@Component
public class RegExBroker {
    private static final String REGEX_QUANTITY = "^\\d{1}x";

    public int getQuantity(String line) {
        Pattern pattern = Pattern.compile(REGEX_QUANTITY);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return Character.getNumericValue(matcher.group(0).charAt(0));
        } else {
            return 0;
        }
    }

    public String getTitle(String line) {
        String[] splits = line.split("\\d{1}x | \\(");
        if (splits[0].isEmpty()) {
            return splits[1];
        } else {
            return splits[0];
        }
    }
}
