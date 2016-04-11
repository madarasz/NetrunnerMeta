package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardAverage;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.helper.comparator.CardAverageComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/25/15.
 * Digesting statistical data for identity averages.
 */
@Component
public class AverageDigest {

    @Autowired
    CardRepository cardRepository;

    private static final String[] TYPES_UNIVERSAL = new String[]{"event", "hardware", "icebreaker", "program", "resource", "agenda", "asset", "operation", "upgrade", "barrier", "code gate", "sentry", "mythic", "trap"};
    private static final String[] TYPES_ICE = new String[]{"barrier", "code gate", "sentry", "mythic", "trap",
            "fracter", "decoder", "killer", "ai", "D4v1d", "hardware"};

    public AverageDigest() {
    }

    public List<CardAverage> getICESortedAverages(Set<CardAverage> stat) {
        return getSortedAverages(stat, TYPES_ICE);
    }

    public List<CardAverage> getSortedAverages(Set<CardAverage> stat) {
        return getSortedAverages(stat, TYPES_UNIVERSAL);
    }

    private List<CardAverage> getSortedAverages(Set<CardAverage> stat, String[] filters) {
        List<CardAverage> result = new ArrayList<>();
        for (String filter : filters) {
            List<CardAverage> subset = filterAndSortCards(new ArrayList<>(stat), filter);
            result.addAll(subset);
        }
        return result;
    }

    private List<CardAverage> filterAndSortCards(List<CardAverage> cards, String typefilter) {
        List<CardAverage> result = new ArrayList<>();
        for (CardAverage card : cards) {
            if ((card.getTypecodes().contains(typefilter)) || (card.getCardtitle().equals(typefilter))) {
                result.add(card);
                card.setTypecodes(typefilter);
            }
        }
        CardAverageComparator comparator = new CardAverageComparator();
        result.sort(comparator);
        return result;
    }
}
