package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.DPDecks;

import java.util.Comparator;

/**
 * Comparator for CardUsage structure, orders by data packs.
 * Created by madarasz on 2015-06-17.
 */
public class DPDecksComparator extends DPComparator implements Comparator<DPDecks>{

    @Override
    public int compare(DPDecks c1, DPDecks c2) {
        return -Integer.compare(getNumber(c1.getDptitle()), getNumber(c2.getDptitle()));
    }
}
