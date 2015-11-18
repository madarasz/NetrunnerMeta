package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.DPIdentity;
import com.madarasz.netrunnerstats.helper.comparator.DPIdentityComparator;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/10/15.
 * List for statistical data on identities of data pack
 */
@NodeEntity
public class DPIdentities {
    @GraphId
    private Long id;
    @RelatedTo(type = "IDS") private @Fetch Set<DPIdentity> identities;
    private String cardpool;
    private String sidecode;

    public DPIdentities() {
        identities = new HashSet<DPIdentity>();
    }

    public DPIdentities(String carpool, String sidecode) {
        this.cardpool = carpool;
        this.sidecode = sidecode;
        this.identities = new HashSet<DPIdentity>();
    }

    public Set<DPIdentity> getIdentities() {
        return identities;
    }

    public String getSidecode() {
        return sidecode;
    }

    public String getCardpool() {
        return cardpool;
    }

    public void addIdentitiy(DPIdentity identity) {
        identities.add(identity);
    }

    public List<DPIdentity> getSortedIdentities() {
        List<DPIdentity> result = new ArrayList<DPIdentity>(identities);
        DPIdentityComparator comparator = new DPIdentityComparator();
        result.sort(comparator);
        return result;
    }
}
