package com.madarasz.netrunnerstats.database.DOs.stats.entries;

/**
 * Created by madarasz on 11/10/15.
 * Entry row of deck multidimensional scaling
 */
public class MDSEntry {
    private double x;
    private double y;
    private String deckTitle;
    private String deckURL;
    private boolean topdeck;

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
}
