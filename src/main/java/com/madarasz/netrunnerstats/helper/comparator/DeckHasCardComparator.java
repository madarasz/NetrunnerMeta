package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;

import java.util.Comparator;

/**
 * Created by madarasz on 11/10/15.
 * Compares DeckHasCards based on title alphabetical order.
 */
public class DeckHasCardComparator implements Comparator<DeckHasCard> {

    @Override
    public int compare(DeckHasCard o1, DeckHasCard o2) {
        return o1.getCard().getTitle().compareTo(o2.getCard().getTitle());
    }
}
