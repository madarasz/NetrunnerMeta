package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.stats.IdentityAverage;
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

    private static final String[] TYPES_RUNNER = new String[]{"event", "hardware", "icebreaker", "program", "resource"};
    private static final String[] TYPES_CORP = new String[]{"agenda", "asset", "operation", "upgrade", "barrier", "code gate", "sentry", "mythic", "trap"};

    public AverageDigest() {
    }

    public List<CardAverage> getSortedAverages(IdentityAverage stat) {
        List<CardAverage> result = new ArrayList<>();
        Card identity = cardRepository.findByTitle(stat.getIdentity());
        if (identity != null) {
            String[] filters;
            if (identity.isRunner()) {
                filters = TYPES_RUNNER;
            } else {
                filters = TYPES_CORP;
            }

            for (String filter : filters) {
                List<CardAverage> subset = filterAndSortCards(stat.getCards(), filter);
                result.addAll(subset);
            }
        }

        return result;
    }

    private List<CardAverage> filterAndSortCards(Set<CardAverage> cards, String typefilter) {
        List<CardAverage> result = new ArrayList<>();
        for (CardAverage card : cards) {
            if (card.getTypecodes().contains(typefilter)) {
                result.add(card);
                card.setTypecodes(typefilter);
            }
        }
        CardAverageComparator comparator = new CardAverageComparator();
        result.sort(comparator);
        return result;
    }
}
