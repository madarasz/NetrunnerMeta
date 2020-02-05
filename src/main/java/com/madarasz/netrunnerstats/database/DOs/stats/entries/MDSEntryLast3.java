package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 4/11/16.
 * Entry row of deck multidimensional scaling,
 * plus info on cardpool for last 3 aggregated cardpool.
 */
@NodeEntity
public class MDSEntryLast3 extends MDSEntry {
    private String cardpool;

    public MDSEntryLast3() {
    }

    public MDSEntryLast3(double x, double y, String deckTitle, String deckURL, boolean topdeck, String shortHtmlDigest, String htmlDigest, String digest, String cardpool) {
        super(x, y, deckTitle, deckURL, topdeck, shortHtmlDigest, htmlDigest, digest);
        this.cardpool = cardpool;
    }

    public String getCardpool() {
        return cardpool;
    }
}
