package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.DPIdentity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/10/15.
 * List for statistical data on identities of data pack
 */
public class DPIntentities {
    private List<DPIdentity> identities;

    public DPIntentities() {
        identities = new ArrayList<DPIdentity>();
    }

    public List<DPIdentity> getIdentities() {
        return identities;
    }

    public void addIdentitiy(DPIdentity identity) {
        identities.add(identity);
    }
}
