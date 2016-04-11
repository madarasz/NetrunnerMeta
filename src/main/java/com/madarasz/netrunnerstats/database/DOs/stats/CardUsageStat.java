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
    private int runnerdecks;
    private int runnertopdecks;
    private int corpdecks;
    private int corptopdecks;
    @RelatedTo(type = "USAGE") private @Fetch Set<CardUsage> cards;

    public CardUsageStat() {
    }

    public CardUsageStat(String cardpackname) {
        this.cardpackname = cardpackname;
        cards = new HashSet<>();
    }

    public CardUsageStat(String cardpackname, int runnerdecks, int runnertopdecks, int corpdecks, int corptopdecks) {
        this.cardpackname = cardpackname;
        this.runnerdecks = runnerdecks;
        this.runnertopdecks = runnertopdecks;
        this.corpdecks = corpdecks;
        this.corptopdecks = corptopdecks;
        cards = new HashSet<>();
    }

    public void addCardUsage(CardUsage cardUsage) {
        cards.add(cardUsage);
    }

    public String getCardpackname() {
        return cardpackname;
    }

    public int getRunnerdecks() {
        return runnerdecks;
    }

    public int getRunnertopdecks() {
        return runnertopdecks;
    }

    public int getCorpdecks() {
        return corpdecks;
    }

    public int getCorptopdecks() {
        return corptopdecks;
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
