package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.database.DOs.CardPack;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.result.StatCounts;
import com.madarasz.netrunnerstats.database.DOs.stats.*;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.*;
import com.madarasz.netrunnerstats.database.DRs.*;
import com.madarasz.netrunnerstats.database.DRs.stats.*;
import com.madarasz.netrunnerstats.helper.ColorPicker;
import com.madarasz.netrunnerstats.helper.DeckDigest;
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
    TournamentRepository tournamentRepository;

    @Autowired
    StandingRepository standingRepository;

    @Autowired
    DPStatsRepository dpStatsRepository;

    @Autowired
    MultiDimensionalScaling multiDimensionalScaling;

    @Autowired
    IdentityMDSRepository identityMdsRepository;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    DeckInfosRepository deckInfosRepository;

    @Autowired
    DPIdentitiesRepository dpIdentitiesRepository;

    @Autowired
    DeckDigest deckDigest;

    @Autowired
    ColorPicker colorPicker;

    /**
     * Log statistics about deck count for each identity for all cardpool legality
     */
    public DPStatistics getPackStats(String cardpackName) {
        DPStatistics statistics = dpStatsRepository.findByDpname(cardpackName);
        if (statistics == null) {
            statistics = new DPStatistics(cardpackName, deckRepository.countByCardpool(cardpackName), standingRepository.countByCardPool(cardpackName));
            System.out.println("*********************************************************************");
            System.out.println(String.format("Stats for cardpool: %s (%d decks, %d standings)",
                    cardpackName, deckRepository.countByCardpool(cardpackName), standingRepository.countByCardPool(cardpackName)));
            System.out.println("*********************************************************************");

            List<StatCounts> stats = standingRepository.getTopIdentityStatsByCardPool(cardpackName);
            for (StatCounts stat : stats) {
                String identity = stat.getCategory();
                if (stat.getSideCode().equals("runner")) {
                    statistics.addRunnerIdentity(identity,
                            deckRepository.countTopByIdentityAndCardPool(identity, cardpackName),
                            stat.getCount(), colorPicker.colorIdentity(identity));
                } else {
                    statistics.addCorpIdentity(identity,
                            deckRepository.countTopByIdentityAndCardPool(identity, cardpackName),
                            stat.getCount(), colorPicker.colorIdentity(identity));
                }
                System.out.println(String.format("%s - %d (%d)", identity, stat.getCount(),
                        deckRepository.countTopByIdentityAndCardPool(identity, cardpackName)));
            }

            System.out.println("*********************************************************************");

            stats = standingRepository.getTopFactionStatsByCardPool(cardpackName);
            for (StatCounts stat : stats) {
                String faction = stat.getCategory();
                if (stat.getSideCode().equals("runner")) {
                    statistics.addRunnerFaction(faction, deckRepository.countTopByCardPoolAndFaction(cardpackName, faction),
                            stat.getCount(), colorPicker.colorFaction(faction));
                } else {
                    statistics.addCorpFaction(faction, deckRepository.countTopByCardPoolAndFaction(cardpackName, faction),
                            stat.getCount(), colorPicker.colorFaction(faction));
                }
                System.out.println(String.format("%s - %d (%d)", faction, stat.getCount(), deckRepository.countTopByCardPoolAndFaction(cardpackName, faction)));
            }

            System.out.println("*********************************************************************");
            System.out.println("Saving DP Statistics.");
            dpStatsRepository.save(statistics); // TODO: commit?
        }
        return statistics;
    }

    /**
     * Generate multidimensional scaling for identity and cardpool legality
     * @param identityName filter for identity
     * @param cardpackName filter for cardpool
     */
    public IdentityMDS getPackMath(String identityName, String cardpackName) {
        IdentityMDS result = identityMdsRepository.findByDpnameIdentitytitle(cardpackName, identityName);
        if (result == null) {
            ArrayList<Deck> decks = new ArrayList<Deck>(deckRepository.filterByIdentityAndCardPool(identityName, cardpackName));
            ArrayList<Deck> topdecks = new ArrayList<Deck>(deckRepository.filterTopByIdentityAndCardPool(identityName, cardpackName));

            // TODO: better topdecks contain
            ArrayList<String> topURLs = new ArrayList<String>();
            for (Deck topdeck : topdecks) {
                topURLs.add(topdeck.getUrl());
            }

            int decknum = deckRepository.countByIdentityAndCardPool(identityName, cardpackName);
            int topdecknum = deckRepository.countTopByIdentityAndCardPool(identityName, cardpackName);
            result = new IdentityMDS(cardpackName, identityName, decknum, topdecknum);
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
            System.out.println("*********************************************************************");
            System.out.println(String.format("Saving MDS Statistics: %s - %s", cardpackName, identityName));
            identityMdsRepository.save(result);
        }
        return result;
    }
    public DPIdentities getIdentityLinksForDataPack(String cardpackName, String sidecode) {
        DPIdentities result = dpIdentitiesRepository.findByCardpoolSidecode(cardpackName, sidecode);
        if (result == null) {
            result = new DPIdentities(cardpackName, sidecode);
            DPStatistics stats = getPackStats(cardpackName);

            Set<CountDeckStands> data;
            if (sidecode.equals("runner")) {
                data = stats.getRunnerIdentities();
            } else if (sidecode.equals("corp")) {
                data = stats.getCorpIdentities();
            } else {
                return new DPIdentities();
            }

            for (CountDeckStands identity : data) {
                int topdecknum = identity.getDecknum();
                String title = identity.getTitle();
                int decknum = deckRepository.countByIdentityAndCardPool(title, cardpackName);
                if (decknum > 0) {
                    DPIdentity entry = new DPIdentity(title,
                            String.format("/MDSIdentity/%s/%s", cardpackName, title),
                            decknum, topdecknum);
                    result.addIdentitiy(entry);
                }
            }
            System.out.println(String.format("Saving DPIdentities: %s - %s", cardpackName, sidecode));
            dpIdentitiesRepository.save(result);
        }
        return result;
    }

    public CardPoolStats getCardPoolStats() {
        CardPoolStats result = cardPoolStatsRepository.find();
        if (result == null) {
            result = new CardPoolStats();
            List<CardPack> available = cardPackRepository.findWithStandings();
            for (CardPack cardPack : available) {
                String title = cardPack.getName();
                int standingsnum = standingRepository.countByCardPool(title);
                int decknum = deckRepository.countByCardpool(title);
                int tournamentnum = tournamentRepository.countByCardpool(title);
                CardPool cardPool = new CardPool(title, tournamentnum, decknum, standingsnum,
                        cardPack.getNumber(), cardPack.getCyclenumber());
                result.addCardPool(cardPool);
            }
            System.out.println("Saving Cardpool Statistics.");
            cardPoolStatsRepository.save(result);
        }
        return result;
    }

    public DeckInfos getDeckInfos(String identityName, String cardpool) {
        DeckInfos result = deckInfosRepository.findByCardpoolIdentityname(cardpool, identityName);
        if (result == null) {
            List<Deck> decks = deckRepository.filterByIdentityAndCardPool(identityName, cardpool);
            result = new DeckInfos();
            for (Deck deck : decks) {
                DeckInfo info = getDeckInfo(deck);
                result.addDeckInfo(info);
            }
            System.out.println(String.format("Saving DeckInfos Statistics: %s - %s", cardpool, identityName));
            deckInfosRepository.save(result);
        }
        return result;
    }

    public DeckInfo getDeckInfo(Deck deck) {
        return new DeckInfo(deckDigest.shortHtmlDigest(deck), deckDigest.htmlDigest(deck),
                deckDigest.digest(deck), deck.getUrl());
    }

    private double NaNFix(double input) {
        if (Double.isNaN(input)) {
            return 0;
        } else {
            return input;
        }
    }
}
