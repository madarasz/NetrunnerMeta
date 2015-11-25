package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardAverage;
import org.springframework.data.neo4j.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by madarasz on 11/25/15.
 * Statistical information on average card distribution on identities.
 */
@NodeEntity
public class IdentityAverage {
    @GraphId
    private Long id;
    @Indexed private String identity;
    @Indexed private String cardpool;
    @RelatedTo(type = "CARDS") private @Fetch Set<CardAverage> cards;

    public IdentityAverage() {
        this.cards = new HashSet<>();
    }

    public IdentityAverage(String identity, String cardpool) {
        this.identity = identity;
        this.cardpool = cardpool;
        this.cards = new HashSet<>();
    }

    public String getIdentity() {
        return identity;
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
