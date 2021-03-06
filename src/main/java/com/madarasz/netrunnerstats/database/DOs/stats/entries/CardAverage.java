package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import com.madarasz.netrunnerstats.database.DOs.Card;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 11/25/15.
 * Statistical information on deck averages.
 */
@NodeEntity
public class CardAverage {
    @GraphId
    private Long id;
    private String cardtitle;
    private String using;   // what percentage decks are using it
    private String average; // what is the average card count per deck
    private String averageifused; // what is the average card count if used
    private String typecodes;   // all card typecodes and subtypes
    private String cardpack;
    private String faction;

    public CardAverage() {
    }

    public CardAverage(Card card, String using, String average, String averageifused) {
        this.cardtitle = card.getTitle();
        this.using = using;
        this.average = average;
        this.averageifused = averageifused;
        this.typecodes = card.getType_code() + " - " + card.getSubtype_code();
        this.cardpack = card.getCardPack().getName();
        this.faction = card.getFaction_code();
    }

    public String getFaction() {
        return faction;
    }

    public String getCardtitle() {
        return cardtitle;
    }

    public String getUsing() {
        return using;
    }

    public String getAverage() {
        return average;
    }

    public String getAverageifused() {
        return averageifused;
    }

    public String getTypecodes() {
        return typecodes;
    }

    public String getCardpack() {
        return cardpack;
    }

    public void setTypecodes(String typecodes) {
        this.typecodes = typecodes;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - in decks: %s - average: %s - AIU: %s",
                cardtitle, cardpack, using, average, averageifused);
    }
}
