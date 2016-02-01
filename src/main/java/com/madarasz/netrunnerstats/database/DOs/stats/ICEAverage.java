package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardAverage;
import org.springframework.data.neo4j.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by madarasz on 01/30/16.
 * Statistical information on average ICE/breaker distribution on cardpool.
 */
@NodeEntity
public class ICEAverage {
    @GraphId
    private Long id;
    @Indexed
    private String sidecode;
    @Indexed
    private String cardpool;
    @RelatedTo(type = "CARDS") private @Fetch Set<CardAverage> cards;

    public ICEAverage() {
        this.cards = new HashSet<>();
    }

    public ICEAverage(String sidecode, String cardpool) {
        this.sidecode = sidecode;
        this.cardpool = cardpool;
        this.cards = new HashSet<>();
    }

    public String getSidecode() {
        return sidecode;
    }

    public String getCardpool() {
        return cardpool;
    }

    public Set<CardAverage> getCards() {
        return cards;
    }

    public void addCard(CardAverage cardAverage) {
        cards.add(cardAverage);
    }

}
