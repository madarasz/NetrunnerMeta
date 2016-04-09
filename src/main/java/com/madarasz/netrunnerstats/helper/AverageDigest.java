package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.stats.ICEAverage;
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
    private static final String[] TYPES_RUNNER_PART1 = new String[]{"event", "hardware", "resource"};
    private static final String[] TYPES_RUNNER_PART2 = new String[]{"icebreaker", "program"};
    private static final String[] TYPES_CORP_PART1 = new String[]{"agenda", "asset", "operation", "upgrade"};
    private static final String[] TYPES_CORP_PART2 = new String[]{"barrier", "code gate", "sentry", "mythic", "trap"};
    private static final String[] TYPES_ICE = new String[]{"barrier", "code gate", "sentry", "mythic", "trap",
            "fracter", "decoder", "killer", "ai", "D4v1d", "hardware"};

    public AverageDigest() {
    }

    public List<CardAverage> getICESortedAverages(ICEAverage stat) {
        return getSortedAverages(stat, TYPES_ICE);
    }

    public List<CardAverage> getICESortedAverages(Set<CardAverage> stat) {
        return getSortedAverages(stat, TYPES_ICE);
    }

    public List<CardAverage> getSortedAverages(IdentityAverage stat) {
        Card identity = cardRepository.findByTitle(stat.getIdentity());
        if (identity != null) {
            String[] filters;
            if (identity.isRunner()) {
                filters = TYPES_RUNNER;
            } else {
                filters = TYPES_CORP;
            }
            return getSortedAverages(stat, filters);
        } else {
            return new ArrayList<CardAverage>();
        }
    }

    public List<CardAverage> getSortedAverages(IdentityAverage stat, int part) {
        Card identity = cardRepository.findByTitle(stat.getIdentity());
        if (identity != null) {
            String[] filters;
            if (identity.isRunner()) {
                if (part == 1) {
                    filters = TYPES_RUNNER_PART1;
                } else {
                    filters = TYPES_RUNNER_PART2;
                }
            } else {
                if (part == 1) {
                    filters = TYPES_CORP_PART1;
                } else {
                    filters = TYPES_CORP_PART2;
                }
            }
            return getSortedAverages(stat, filters);
        } else {
            return new ArrayList<CardAverage>();
        }
    }

    private List<CardAverage> getSortedAverages(IdentityAverage stat, String[] filters) {
        List<CardAverage> result = new ArrayList<>();
        for (String filter : filters) {
            List<CardAverage> subset = filterAndSortCards(stat.getCards(), filter);
            result.addAll(subset);
        }
        return result;
    }

    private List<CardAverage> getSortedAverages(Set<CardAverage> stat, String[] filters) {
        List<CardAverage> result = new ArrayList<>();
        for (String filter : filters) {
            List<CardAverage> subset = filterAndSortCards(stat, filter);
            result.addAll(subset);
        }
        return result;
    }

    private List<CardAverage> getSortedAverages(ICEAverage stat, String[] filters) {
        List<CardAverage> result = new ArrayList<>();
        for (String filter : filters) {
            List<CardAverage> subset = filterAndSortCards(stat.getCards(), filter);
            result.addAll(subset);
        }
        return result;
    }

    private List<CardAverage> filterAndSortCards(Set<CardAverage> cards, String typefilter) {
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
