package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;

import java.util.Comparator;

/**
 * Comparator for CardUsage structure, orders by data packs.
 * Created by madarasz on 2015-06-17.
 */
public class CardUsageDPComparator extends DPComparator implements Comparator<CardUsage>{

    @Override
    public int compare(CardUsage c1, CardUsage c2) {
        return Integer.compare(getNumber(c1.getCardpacktitle()), getNumber(c2.getCardpacktitle()));
    }
}
