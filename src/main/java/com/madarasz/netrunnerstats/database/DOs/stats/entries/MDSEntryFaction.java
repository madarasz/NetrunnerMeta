package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 5/02/16.
 * Entry row of deck multidimensional scaling,
 * plus info on identity for faction MDS.
 */
@NodeEntity
public class MDSEntryFaction extends MDSEntry {
    private String identity;

    public MDSEntryFaction() {
    }

    public MDSEntryFaction(double x, double y, String deckTitle, String deckURL, boolean topdeck, String shortHtmlDigest, String htmlDigest, String digest, String identity) {
        super(x, y, deckTitle, deckURL, topdeck, shortHtmlDigest, htmlDigest, digest);
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }
}
