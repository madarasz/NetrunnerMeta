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
    private String cardpacktitle;
    private int indecks;
    private int intopdecks;

    public CardUsage() {
    }

    public CardUsage(String cardtitle, String cardpacktitle, String sidecode, int indecks, int intopdecks) {
        this.cardtitle = cardtitle;
        this.cardpacktitle = cardpacktitle;
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

    public String getCardpacktitle() {
        return cardpacktitle;
    }
}
