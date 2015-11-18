package com.madarasz.netrunnerstats.helper.comparator;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.DPIdentity;

import java.util.Comparator;

/**
 * Created by madarasz on 11/10/15.
 * Comparator for sorting DPIdentity
 */
public class DPIdentityComparator implements Comparator<DPIdentity> {

    @Override
    public int compare(DPIdentity o1, DPIdentity o2) {
        if (o1.getDecknum() > o2.getDecknum()) {
            return -1;
        } else {
            return 1;
        }
    }
}
