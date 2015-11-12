package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CountDeckStands;

import java.util.Comparator;

/**
 * Created by madarasz on 11/10/15.
 * Comparator for sorting CountDeckStands
 */
public class CountDeckStatsComparator implements Comparator<CountDeckStands> {

    @Override
    public int compare(CountDeckStands o1, CountDeckStands o2) {
        if (o1.getStandingnum() > o2.getStandingnum()) {
            return -1;
        } else {
            return 1;
        }
    }
}
