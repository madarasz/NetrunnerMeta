package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 11/10/15.
 * Entry row of deck multidimensional scaling
 */
@NodeEntity
public class MDSEntry {
    @GraphId
    private Long id;
    private double x;
    private double y;
    private String deckTitle;
    private String deckURL;
    private boolean topdeck;
    private String shortHtmlDigest;
    private String htmlDigest;
    private String digest;

    public MDSEntry() {
    }

    public MDSEntry(double x, double y, String deckTitle, String deckURL, boolean topdeck, String shortHtmlDigest, String htmlDigest, String digest) {
        this.x = x;
        this.y = y;
        this.deckTitle = deckTitle;
        this.deckURL = deckURL;
        this.topdeck = topdeck;
        this.shortHtmlDigest = shortHtmlDigest;
        this.htmlDigest = htmlDigest;
        this.digest = digest;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getDeckTitle() {
        return deckTitle;
    }

    public String getDeckURL() {
        return deckURL;
    }

    public String getShortHtmlDigest() {
        return shortHtmlDigest;
    }

    public String getHtmlDigest() {
        return htmlDigest;
    }

    public String getDigest() {
        return digest;
    }

    public boolean isTopdeck() {
        return topdeck;
    }

}
