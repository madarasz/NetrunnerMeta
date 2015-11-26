package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madadarasz on 2015.11.08..
 * For counting statistics on deck and standings numbers
 */
@NodeEntity
public class CountDeckStands {
    @GraphId
    private Long id;
    private String title;
    private int decknum;
    private int standingnum;
    private double percentage; // overall standing percentage
    private String colorcode;

    public CountDeckStands() {
    }

    public CountDeckStands(String title, int decknum, int standingnum, double percentage, String colorcode) {
        this.title = title;
        this.decknum = decknum;
        this.standingnum = standingnum;
        this.percentage = percentage;
        this.colorcode = colorcode;
    }

    public String getTitle() {
        return title;
    }

    public int getDecknum() {
        return decknum;
    }

    public int getStandingnum() {
        return standingnum;
    }

    public String getColorcode() {
        return colorcode;
    }

    public double getPercentage() {
        return percentage;
    }
}
