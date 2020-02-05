package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardCombo;

import java.util.Comparator;

/**
 * Comparator for CardUsage structure, orders by top decks using.
 * Created by madarasz on 2015-06-17.
 */
public class CardComboComparator implements Comparator<CardCombo>{

    @Override
    public int compare(CardCombo c1, CardCombo c2) {
        return -Integer.compare(c1.getScore(), c2.getScore());
    }
}
