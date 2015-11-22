package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;

import java.util.Comparator;

/**
 * Comparator for CardUsage structure, orders by top decks using.
 * Created by madarasz on 2015-06-17.
 */
public class CardUsageComparator implements Comparator<CardUsage>{

    @Override
    public int compare(CardUsage c1, CardUsage c2) {
        if (c1.getIntopdecks() > c2.getIntopdecks()) {
            return -1;
        } else {
            return 1;
        }
    }
}
