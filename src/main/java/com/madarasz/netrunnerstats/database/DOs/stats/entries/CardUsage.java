package com.madarasz.netrunnerstats.database.DOs.stats.entries;

/**
 * Created by madarasz on 11/22/15.
 * Holds card usage data
 */
public class CardUsage {
    private String cardtitle;
    private String sidecode;
    private int indecks;
    private int intopdecks;

    public CardUsage() {
    }

    public CardUsage(String cardtitle, String sidecode, int indecks, int intopdecks) {
        this.cardtitle = cardtitle;
        this.sidecode = sidecode;
        this.indecks = indecks;
        this.intopdecks = intopdecks;
    }

    public String getCardtitle() {
        return cardtitle;
    }

    public String getSidecode() {
        return sidecode;
    }

    public int getIndecks() {
        return indecks;
    }

    public int getIntopdecks() {
        return intopdecks;
    }
}
