package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.result.StatCounts;
import com.madarasz.netrunnerstats.DOs.stats.DPIntentities;
import com.madarasz.netrunnerstats.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.DOs.stats.IdentityMDS;
import com.madarasz.netrunnerstats.DOs.stats.entries.CountDeckStands;
import com.madarasz.netrunnerstats.DOs.stats.entries.DPIdentity;
import com.madarasz.netrunnerstats.DOs.stats.entries.MDSEntry;
import com.madarasz.netrunnerstats.DRs.*;
import com.madarasz.netrunnerstats.DRs.stats.DPStatsRepository;
import com.madarasz.netrunnerstats.helper.ColorPicker;
import com.madarasz.netrunnerstats.helper.Enums;
import com.madarasz.netrunnerstats.helper.MultiDimensionalScaling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/7/15.
 * Handling statistical functions.
 */
@Component
public class Statistics {

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    StandingRepository standingRepository;

    @Autowired
    DPStatsRepository dpStatsRepository;

    @Autowired
    MultiDimensionalScaling multiDimensionalScaling;

    @Autowired
    ColorPicker colorPicker;

    /**
     * Log statistics about deck count for each identity for all cardpool legality
     */
    public DPStatistics getPackStats(String cardpackName) {
        DPStatistics statistics = dpStatsRepository.findByDpname(cardpackName);
        if (statistics == null) {
            statistics = new DPStatistics(cardpackName, deckRepository.countByCardPack(cardpackName), standingRepository.countByCardPool(cardpackName));
            System.out.println("*********************************************************************");
            System.out.println(String.format("Stats for cardpool: %s (%d decks, %d standings)",
                    cardpackName, deckRepository.countByCardPack(cardpackName), standingRepository.countByCardPool(cardpackName)));
            System.out.println("*********************************************************************");

            List<StatCounts> stats = standingRepository.getTopIdentityStatsByCardPool(cardpackName);
            for (StatCounts stat : stats) {
                String identity = stat.getCategory();
                if (stat.getSideCode().equals("runner")) {
                    statistics.addRunnerIdentity(identity,
                            deckRepository.countTopByCardPackAndIdentity(cardpackName, identity),
                            stat.getCount(), colorPicker.colorIdentity(identity));
                } else {
                    statistics.addCorpIdentity(identity,
                            deckRepository.countTopByCardPackAndIdentity(cardpackName, identity),
                            stat.getCount(), colorPicker.colorIdentity(identity));
                }
                System.out.println(String.format("%s - %d (%d)", identity, stat.getCount(),
                        deckRepository.countTopByCardPackAndIdentity(cardpackName, identity)));
            }

            System.out.println("*********************************************************************");

            stats = standingRepository.getTopFactionStatsByCardPool(cardpackName);
            for (StatCounts stat : stats) {
                String faction = stat.getCategory();
                if (stat.getSideCode().equals("runner")) {
                    statistics.addRunnerFaction(faction, deckRepository.countTopByCardPackAndFaction(cardpackName, faction),
                            stat.getCount(), colorPicker.colorFaction(faction));
                } else {
                    statistics.addCorpFaction(faction, deckRepository.countTopByCardPackAndFaction(cardpackName, faction),
                            stat.getCount(), colorPicker.colorFaction(faction));
                }
                System.out.println(String.format("%s - %d (%d)", faction, stat.getCount(), deckRepository.countTopByCardPackAndFaction(cardpackName, faction)));
            }

            System.out.println("*********************************************************************");
            System.out.println("Saving DP Statistics.");
            dpStatsRepository.save(statistics); // TODO: commit?
        }
        return statistics;
    }

    public void getAllStats() {
        List<Card> identities = cardRepository.findIdentities();
        long totalDecks = deckRepository.count();
        System.out.println("*********************************************************************");
        System.out.println(String.format("Number of all decks: %d", totalDecks));
        for (Enums.Factions faction : Enums.Factions.values()) {
            String factionName = faction.toString().replaceAll("_", "-");
            int factionCount = deckRepository.countByFaction(factionName);
            if (factionCount > 0) {
                System.out.println(String.format("* %s: %d", factionName, factionCount));
                for (Card card : identities) {
                    if (card.getFaction_code().equals(factionName)) {
                        int identityCount = deckRepository.countByIdentity(card);
                        if (identityCount > 0) {
                            System.out.println(String.format("** %s: %d", card.getTitle(), identityCount));
                        }
                    }
                }
            }
        }

        System.out.println("*********************************************************************");

        for (Enums.CardCycles cardCycle : Enums.CardCycles.values()) {
            String cycleName = cardCycle.toString().replaceAll("_"," ");
            int cyclenumber = cardCycle.getCycleNumber();
            int cycleCount = deckRepository.countByCycle(cyclenumber);
            if (cycleCount > 0) {
                System.out.println(String.format("* %s: %d", cycleName, cycleCount));
                int number = 0;
                do {
                    number++;
                    CardPack cardPack = cardPackRepository.findByCyclenumberAndNumber(cyclenumber, number);
                    if (cardPack == null) {
                        break;
                    } else {
                        String cardPackName = cardPack.getName();
                        int cardpackCount = deckRepository.countByCardPack(cardPackName);
                        if (cardpackCount > 0) {
                            System.out.println(String.format("** %s: %d", cardPackName, cardpackCount));
                            getPackStats(cardPackName);
                        }
                    }
                } while(true);
            }
        }

        System.out.println("*********************************************************************");
    }

    /**
     * Generate multidimensional scaling for identity and cardpool legality
     * @param identityName filter for identity
     * @param cardpackName filter for cardpool
     */
    public IdentityMDS getPackMath(String identityName, String cardpackName) {
        ArrayList<Deck> decks = new ArrayList<Deck>(deckRepository.filterByIdentityAndCardPool(identityName, cardpackName));
        ArrayList<Deck> topdecks = new ArrayList<Deck>(deckRepository.filterTopByIdentityAndCardPool(identityName, cardpackName));

        // TODO: better topdecks contain
        ArrayList<String> topURLs = new ArrayList<String>();
        for (Deck topdeck : topdecks) {
            topURLs.add(topdeck.getUrl());
        }

        int decknum = deckRepository.countByIdentityAndCardPool(identityName, cardpackName);
        int topdecknum = deckRepository.countTopByIdentityAndCardPool(identityName, cardpackName);
        IdentityMDS result = new IdentityMDS(cardpackName, identityName, decknum, topdecknum);
        if (decknum > 0) {
            double[][] distance = multiDimensionalScaling.getDistanceMatrix(decks);
            double[][] scaling = multiDimensionalScaling.calculateMDS(distance);
            for (int i = 0; i < decks.size(); i++) {
                // NaN fix
                scaling[0][i] = NaNFix(scaling[0][i]);
                scaling[1][i] = NaNFix(scaling[1][i]);

                MDSEntry mds = new MDSEntry(scaling[0][i], scaling[1][i],
                        decks.get(i).getName(), decks.get(i).getUrl(), topURLs.contains(decks.get(i).getUrl()));
                result.addDeck(mds);
                System.out.println(String.format("\"%s\",\"%f\",\"%f\"", decks.get(i).getUrl(), scaling[0][i], scaling[1][i]));
            }
        }
        return result;
    }
    public DPIntentities getIdentityLinksForDataPack(String cardpackName, String sidecode) {
        DPStatistics stats = getPackStats(cardpackName);
        DPIntentities result = new DPIntentities();

        List<CountDeckStands> data;
        if (sidecode.equals("runner")) {
            data = stats.getSortedRunnerIdentities();
        } else if (sidecode.equals("corp")) {
            data = stats.getSortedCorpIdentities();
        } else {
            return new DPIntentities();
        }

        for (CountDeckStands identity : data) {
            int topdecknum = identity.getDecknum();
            String title = identity.getTitle();
            int decknum = deckRepository.countByIdentityAndCardPool(title, cardpackName);
            if (decknum > 0) {
                DPIdentity entry = new DPIdentity(title,
                        String.format("http://localhost:8080/MDSIdentity/%s/%s", cardpackName, title),
                        decknum, topdecknum);
                result.addIdentitiy(entry);
            }
        }
        return result;
    }

    private double NaNFix(double input) {
        if (Double.isNaN(input)) {
            return 0;
        } else {
            return input;
        }
    }
}
