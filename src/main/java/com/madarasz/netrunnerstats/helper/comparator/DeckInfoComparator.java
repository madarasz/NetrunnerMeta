package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.DeckInfo;

import java.util.Comparator;

/**
 * Created by madarasz on 11/10/15.
 * Comparator for sorting DeckInfo
 */
public class DeckInfoComparator implements Comparator<DeckInfo> {

    @Override
    public int compare(DeckInfo o1, DeckInfo o2) {
        return o1.getUrl().compareTo(o2.getUrl());
    }
}
