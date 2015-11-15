package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.MDSEntry;

import java.util.Comparator;

/**
 * Created by madarasz on 11/10/15.
 * Comparator for sorting MDSEntries
 */
public class MDSEntryComparator implements Comparator<MDSEntry> {

    @Override
    public int compare(MDSEntry o1, MDSEntry o2) {
        return o1.getDeckURL().compareTo(o2.getDeckURL());
    }
}
