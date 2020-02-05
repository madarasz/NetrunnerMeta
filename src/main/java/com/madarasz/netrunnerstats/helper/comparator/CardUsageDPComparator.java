package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;

import java.util.Comparator;

/**
 * Comparator for CardUsage structure, orders by data packs.
 * Created by madarasz on 2015-06-17.
 */
public class CardUsageDPComparator implements Comparator<CardUsage>{

    @Override
    public int compare(CardUsage c1, CardUsage c2) {
        if (c1.getCyclenumber() == c2.getCyclenumber()) {
            return Integer.compare(c1.getDpnumber(), c2.getDpnumber());
        } else {
            return Integer.compare(c1.getCyclenumber(), c2.getCyclenumber());
        }
    }
}
