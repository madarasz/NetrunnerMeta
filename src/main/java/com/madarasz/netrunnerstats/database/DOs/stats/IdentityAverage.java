package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardAverage;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.MDSEntry;
import com.madarasz.netrunnerstats.helper.AverageDigest;
import org.springframework.data.neo4j.annotation.*;

import java.util.HashSet;
import java.util.List;
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
    private int allDeckCount;
    private int topDeckCount;
    @RelatedTo(type = "CARDS") private @Fetch Set<CardAverage> cards;
    @RelatedTo(type = "DECKS") private @Fetch Set<MDSEntry> decks;

    public IdentityAverage() {
        this.cards = new HashSet<>();
        this.decks = new HashSet<>();
    }

    public IdentityAverage(String identity, String cardpool, int allDeckCount, int topDeckCount) {
        this.identity = identity;
        this.cardpool = cardpool;
        this.allDeckCount = allDeckCount;
        this.topDeckCount = topDeckCount;
        this.cards = new HashSet<>();
        this.decks = new HashSet<>();
    }

    public String getIdentity() {
        return identity;
    }

    public String getCardpool() {
        return cardpool;
    }

    public int getAllDeckCount() {
        return allDeckCount;
    }

    public int getTopDeckCount() {
        return topDeckCount;
    }

    public Set<MDSEntry> getDecks() {
        return decks;
    }

    public List<CardAverage> getCards() {
        AverageDigest averageDigest = new AverageDigest();
        return averageDigest.getSortedAverages(cards);
    }

    public void addCard(CardAverage cardAverage) {
        cards.add(cardAverage);
    }

    public void addDecks(Set<MDSEntry> mdsEntries) {
        decks.addAll(mdsEntries);
    }

}
