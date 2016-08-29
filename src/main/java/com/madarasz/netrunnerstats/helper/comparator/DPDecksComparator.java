package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.DPDecks;

import java.util.Comparator;

/**
 * Comparator for CardUsage structure, orders by data packs.
 * Created by madarasz on 2015-06-17.
 */
public class DPDecksComparator implements Comparator<DPDecks>{

    @Override
    public int compare(DPDecks c1, DPDecks c2) {
        if (c1.getCyclenumber() == c2.getCyclenumber()) {
            return Integer.compare(c1.getDpnumber(), c2.getDpnumber());
        } else {
            return Integer.compare(c1.getCyclenumber(), c2.getCyclenumber());
        }
    }
}
