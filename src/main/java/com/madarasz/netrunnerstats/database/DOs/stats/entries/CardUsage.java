package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 11/22/15.
 * Holds card usage data
 */
@NodeEntity
public class CardUsage {
    @GraphId
    private Long id;
    @Indexed private String cardtitle;
    private String sidecode;
    private String faction;
    private String cardpacktitle;
    private int indecks;
    private int intopdecks;
    private double deckfraction;
    private double topdeckfraction;

    public CardUsage() {
    }

    public CardUsage(String cardtitle, String cardpacktitle, String sidecode, String faction, int indecks, int intopdecks) {
        this.cardtitle = cardtitle;
        this.cardpacktitle = cardpacktitle;
        this.sidecode = sidecode;
        this.faction = faction;
        this.indecks = indecks;
        this.intopdecks = intopdecks;
    }

    public CardUsage(String cardtitle, String cardpacktitle, String sidecode, String faction, int indecks, int intopdecks, double deckfraction, double topdeckfraction) {
        this.cardtitle = cardtitle;
        this.sidecode = sidecode;
        this.faction = faction;
        this.cardpacktitle = cardpacktitle;
        this.indecks = indecks;
        this.intopdecks = intopdecks;
        this.deckfraction = deckfraction;
        this.topdeckfraction = topdeckfraction;
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

    public String getCardpacktitle() {
        return cardpacktitle;
    }

    public double getDeckfraction() {
        return deckfraction;
    }

    public double getTopdeckfraction() {
        return topdeckfraction;
    }

    public String getFaction() {
        return faction;
    }
}
