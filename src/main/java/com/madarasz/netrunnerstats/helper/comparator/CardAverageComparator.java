package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardAverage;
import java.util.Comparator;

/**
 * Comparator for CardAverage structure, orders by average cards used.
 * Created by madarasz on 2015-06-17.
 */
public class CardAverageComparator implements Comparator<CardAverage>{

    @Override
    public int compare(CardAverage c1, CardAverage c2) {
        return  -c1.getAverage().compareTo(c2.getAverage());
    }
}
