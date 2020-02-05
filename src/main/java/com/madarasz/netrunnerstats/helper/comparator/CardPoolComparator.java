package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardPool;
import java.util.Comparator;

/**
 * Comparator for CardPool structure, orders by Integer.
 * Created by madarasz on 2015-06-17.
 */
public class CardPoolComparator implements Comparator<CardPool>{

    @Override
    public int compare(CardPool c1, CardPool c2) {
        if ((c1.getCyclenumber() > c2.getCyclenumber()) ||
                ((c1.getCyclenumber() == c2.getCyclenumber()) && (c1.getDpnumber() > c2.getDpnumber()))) {
            return -1;
        } else {
            return 1;
        }
    }
}
