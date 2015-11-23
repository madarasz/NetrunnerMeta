package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;
import com.madarasz.netrunnerstats.helper.comparator.CardUsageComparator;
import org.springframework.data.neo4j.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/22/15.
 * Holds statistics on card usage of cards in data pack.
 */
@NodeEntity
public class CardUsageStat {
    @GraphId
    private Long id;
    @Indexed private String cardpackname;
    private boolean cardpool; // true if cardpool, false if cardpack
    @RelatedTo(type = "USAGE") private @Fetch Set<CardUsage> cards;

    public CardUsageStat() {
    }

    public CardUsageStat(String cardpackname, boolean cardpool) {
        this.cardpackname = cardpackname;
        this.cardpool = cardpool;
        cards = new HashSet<>();
    }

    public void addCardUsage(CardUsage cardUsage) {
        cards.add(cardUsage);
    }

    public String getCardpackname() {
        return cardpackname;
    }

    public boolean isCardpool() {
        return cardpool;
    }

    public Set<CardUsage> getCards() {
        return cards;
    }

    public List<CardUsage> getSortedRunnerCards() {
        return getSortedCards("runner");
    }

    public List<CardUsage> getSortedCorpCards() {
        return getSortedCards("corp");
    }

    public List<CardUsage> getSortedCards(String sidecode) {
        List<CardUsage> result = new ArrayList<>();
        for (CardUsage card : cards) {
            if (card.getSidecode().equals(sidecode)) {
                result.add(card);
            }
        }
        CardUsageComparator comparator = new CardUsageComparator();
        result.sort(comparator);
        return result;
    }
}
