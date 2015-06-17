package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.DOs.Card;
import java.util.Comparator;
import java.util.Map;

/**
 * Comparator for Map<Card, Integer> structure, orders by Integer.
 * Created by madarasz on 2015-06-17.
 */
public class CardMapValueComparator implements Comparator<Card>{

    Map<Card, Integer> base;

    public CardMapValueComparator(Map<Card, Integer> base) {
        this.base = base;
    }

    @Override
    public int compare(Card c1, Card c2) {
        if (base.get(c1) >= base.get(c2)) {
            return -1;
        } else {
            return 1;
        }
    }
}
