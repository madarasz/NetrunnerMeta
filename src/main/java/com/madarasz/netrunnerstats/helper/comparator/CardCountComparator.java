package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.helper.CardCount;

import java.util.Comparator;

/**
 * Comparator for CardCount structure, orders by count descending.
 */
public class CardCountComparator implements Comparator<CardCount>{

    @Override
    public int compare(CardCount c1, CardCount c2) {
        return -Integer.compare(c1.getCount(), c2.getCount());
    }
}
