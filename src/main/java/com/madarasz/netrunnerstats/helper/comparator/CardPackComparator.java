package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.CardPack;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardCombo;

import java.util.Comparator;

/**
 * Comparator for CardPack structure, orders by timeline.
 * Created by madarasz on 2016-08-25.
 */
public class CardPackComparator implements Comparator<CardPack>{

    @Override
    public int compare(CardPack c1, CardPack c2) {
        if (c1.getCyclenumber() == c2.getCyclenumber()) {
            return Integer.compare(c1.getNumber(), c2.getNumber());
        } else {
            return Integer.compare(c1.getCyclenumber(), c2.getCyclenumber());
        }
    }
}
