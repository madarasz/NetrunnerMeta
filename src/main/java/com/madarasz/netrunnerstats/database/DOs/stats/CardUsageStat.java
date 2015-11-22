package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;
import com.madarasz.netrunnerstats.helper.comparator.CardUsageComparator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/22/15.
 * Holds statistics on card usage of cards in data pack.
 */
public class CardUsageStat {
    private String cardpackname;
    private Set<CardUsage> cards;

    public CardUsageStat(String cardpackname) {
        this.cardpackname = cardpackname;
        cards = new HashSet<>();
    }

    public void addCardUsage(CardUsage cardUsage) {
        cards.add(cardUsage);
    }

    public String getCardpackname() {
        return cardpackname;
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
