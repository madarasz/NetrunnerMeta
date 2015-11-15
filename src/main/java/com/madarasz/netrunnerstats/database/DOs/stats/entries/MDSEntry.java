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

    public MDSEntry() {
    }

    public MDSEntry(double x, double y, String deckTitle, String deckURL, boolean topdeck) {
        this.x = x;
        this.y = y;
        this.deckTitle = deckTitle;
        this.deckURL = deckURL;
        this.topdeck = topdeck;
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

    public boolean isTopdeck() {
        return topdeck;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDeckTitle(String deckTitle) {
        this.deckTitle = deckTitle;
    }

    public void setDeckURL(String deckURL) {
        this.deckURL = deckURL;
    }

    public void setTopdeck(boolean topdeck) {
        this.topdeck = topdeck;
    }
}
