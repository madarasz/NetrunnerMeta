package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.CardPack;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.database.DOs.result.CardCounts;
import com.madarasz.netrunnerstats.database.DOs.result.StatCounts;
import com.madarasz.netrunnerstats.database.DOs.stats.*;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.*;
import com.madarasz.netrunnerstats.database.DRs.*;
import com.madarasz.netrunnerstats.database.DRs.stats.*;
import com.madarasz.netrunnerstats.helper.ColorPicker;
import com.madarasz.netrunnerstats.helper.DeckDigest;
import com.madarasz.netrunnerstats.helper.MultiDimensionalScaling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 11/7/15.
 * Handling statistical functions.
 */
@Component
public class Statistics {

    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);

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
    CardUsageStatsRepository cardUsageStatsRepository;

    @Autowired
    IdentityAverageRepository identityAverageRepository;

    @Autowired
    DeckDigest deckDigest;

    @Autowired
    ColorPicker colorPicker;

    @Autowired
    Operations operations;

    /**
     * Log statistics about deck and stat count for each identity for a cardpool legality
     * @param cardpackName cardpool name
     * @param top just top decks?
     * @return DPStatistics
     */
    public DPStatistics getPackStats(String cardpackName, boolean top) {
        DPStatistics statistics;
        if (top) {
            statistics = dpStatsRepository.findByDpnameOnlyTop(cardpackName);
        } else {
            statistics = dpStatsRepository.findByDpnameAll(cardpackName);
        }

        if (statistics == null) {
            // total count
            int deckcount;
            int standcount;
            int standcountrunner;
            int standcountcorp;
            if (top) {
                deckcount = deckRepository.countTopByCardpool(cardpackName);
                standcount = standingRepository.countTopByCardPool(cardpackName);
                standcountcorp = standingRepository.countTopByCardPoolSidecode(cardpackName, "corp");
                standcountrunner = standingRepository.countTopByCardPoolSidecode(cardpackName, "runner");
            } else {
                deckcount = deckRepository.countByCardpool(cardpackName);
                standcount = standingRepository.countByCardPool(cardpackName);
                standcountcorp = standingRepository.countByCardPoolSidecode(cardpackName, "corp");
                standcountrunner = standingRepository.countByCardPoolSidecode(cardpackName, "runner");
            }
            statistics = new DPStatistics(cardpackName, deckcount, standcount, standcountrunner, standcountcorp, top);
            logger.debug("*********************************************************************");
            logger.debug(String.format("Stats for cardpool: %s (%d decks, %d standings)",
                    cardpackName, deckRepository.countByCardpool(cardpackName), standingRepository.countByCardPool(cardpackName)));
            logger.debug("*********************************************************************");

            List<StatCounts> stats;
            if (top) {
                stats = standingRepository.getTopIdentityStatsByCardPool(cardpackName);
            } else {
                stats = standingRepository.getIdentityStatsByCardPool(cardpackName);
            }

            for (StatCounts stat : stats) {
                String identity = stat.getCategory();
                // count
                int statcount = stat.getCount();
                int count;
                if (top) {
                    count = deckRepository.countTopByIdentityAndCardPool(identity, cardpackName);
                } else {
                    count = deckRepository.countByIdentityAndCardPool(identity, cardpackName);
                }
                // adding stats
                if (stat.getSideCode().equals("runner")) {
                    statistics.addRunnerIdentity(identity, count, statcount, (double)statcount / standcountrunner, colorPicker.colorIdentity(identity));
                } else {
                    statistics.addCorpIdentity(identity, count, statcount, (double)statcount / standcountcorp, colorPicker.colorIdentity(identity));
                }
                logger.debug(String.format("%s - %d (%d)", identity, statcount,
                        deckRepository.countTopByIdentityAndCardPool(identity, cardpackName)));
            }

            logger.debug("*********************************************************************");
            if (top) {
                stats = standingRepository.getTopFactionStatsByCardPool(cardpackName);
            } else {
                stats = standingRepository.getFactionStatsByCardPool(cardpackName);
            }
            for (StatCounts stat : stats) {
                String faction = stat.getCategory();
                // count
                int count;
                int statcount = stat.getCount();
                if (top) {
                    count = deckRepository.countTopByCardPoolAndFaction(cardpackName, faction);
                } else {
                    count = deckRepository.countByCardPoolAndFaction(cardpackName, faction);
                }
                // adding stats
                if (stat.getSideCode().equals("runner")) {
                    statistics.addRunnerFaction(faction, count, statcount, (double)statcount / standcountrunner, colorPicker.colorFaction(faction));
                } else {
                    statistics.addCorpFaction(faction, count, statcount, (double)statcount / standcountcorp, colorPicker.colorFaction(faction));
                }
                logger.debug(String.format("%s - %d (%d)", faction, statcount, deckRepository.countTopByCardPoolAndFaction(cardpackName, faction)));
            }

            logger.debug("*********************************************************************");
            logger.info(String.format("Saving DP Statistics: %s - top: %s", cardpackName, top));
            dpStatsRepository.save(statistics);
        }
        return statistics;
    }

    /**
     * Generate multidimensional scaling for identity and cardpool legality
     * @param identityName filter for identity
     * @param cardpackName filter for cardpool
     * @return IdentityMDS
     */
    public IdentityMDS getPackMath(String identityName, String cardpackName) {
        IdentityMDS result = identityMdsRepository.findByDpnameIdentitytitle(cardpackName, identityName);
        if (result == null) {
            ArrayList<Deck> decks = new ArrayList<>(deckRepository.filterByIdentityAndCardPool(identityName, cardpackName));
            ArrayList<Deck> topdecks = new ArrayList<>(deckRepository.filterTopByIdentityAndCardPool(identityName, cardpackName));

            // TODO: better topdecks contain
            ArrayList<String> topURLs = new ArrayList<>();
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
                    logger.debug(String.format("\"%s\",\"%f\",\"%f\"", decks.get(i).getUrl(), scaling[0][i], scaling[1][i]));
                }
            }
            logger.debug("*********************************************************************");
            logger.info(String.format("Saving MDS Statistics: %s - %s", cardpackName, identityName));
            identityMdsRepository.save(result);
        }
        return result;
    }

    /**
     * Generates indentity deck groups with links for MDS based on cardpool
     * @param cardpackName cardpool
     * @param sidecode runner or corp
     * @return DPIdentities
     */
    public DPIdentities getIdentityLinksForDataPack(String cardpackName, String sidecode) {
        DPIdentities result = dpIdentitiesRepository.findByCardpoolSidecode(cardpackName, sidecode);
        if (result == null) {
            result = new DPIdentities(cardpackName, sidecode);
            DPStatistics stats = getPackStats(cardpackName, false);

            Set<CountDeckStands> data;
            switch (sidecode) {
                case "runner":
                    data = stats.getRunnerIdentities();
                    break;
                case "corp":
                    data = stats.getCorpIdentities();
                    break;
                default:
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
            logger.info(String.format("Saving DPIdentities: %s - %s", cardpackName, sidecode));
            dpIdentitiesRepository.save(result);
        }
        return result;
    }

    /**
     * Generates cardpool statistics: deck number, ranking number, tournament number per cardpool
     * @return CardPoolStats
     */
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
            logger.info("Saving Cardpool Statistics.");
            cardPoolStatsRepository.save(result);
        }
        return result;
    }

    /**
     * Generates html digest of decks for certain indentity and cardpool
     * @param identityName identity name
     * @param cardpool cardpool name
     * @return DeckInfos
     */
    public DeckInfos getDeckInfos(String identityName, String cardpool) {
        DeckInfos result = deckInfosRepository.findByCardpoolIdentityname(cardpool, identityName);
        if (result == null) {
            List<Deck> decks = deckRepository.filterByIdentityAndCardPool(identityName, cardpool);
            result = new DeckInfos(cardpool, identityName);
            for (Deck deck : decks) {
                DeckInfo info = getDeckInfo(deck);
                result.addDeckInfo(info);
            }
            logger.info(String.format("Saving DeckInfos Statistics: %s - %s", cardpool, identityName));
            deckInfosRepository.save(result);
        }
        return result;
    }

    public DeckInfo getDeckInfo(Deck deck) {
        return new DeckInfo(deckDigest.shortHtmlDigest(deck), deckDigest.htmlDigest(deck),
                deckDigest.digest(deck), deck.getUrl());
    }

    /**
     * Calculates most used a card in the cardpack
     * @param cardpack cardpack name
     * @return CardUsageStat
     */
    public CardUsageStat getMostUsedCardsForCardPack(String cardpack) {
        CardUsageStat result = cardUsageStatsRepository.findByCardPackName(cardpack);
        if (result == null) {
            result = new CardUsageStat(cardpack, false);
            List<Card> cards = cardRepository.findByCardPackName(cardpack);
            for (Card card : cards) {
                String code = card.getCode();
                int count = deckRepository.countByUsingCard(code);
                if (count > 0) {
                    int topcount = deckRepository.countTopByUsingCard(code);
                    result.addCardUsage(new CardUsage(card.getTitle(), card.getCardPack().getName(),
                            card.getSide_code(), count, topcount));
                }
            }
            logger.info(String.format("Saving Card Usage Statistics, cardpack: %s", cardpack));
            cardUsageStatsRepository.save(result);
        }
        return result;
    }

    /**
     * Calculates most used a card with a cardpool of tournaments
     * @param cardpool cardpool name
     * @return CardUsageStat
     */
    public CardUsageStat getMostUsedCardsForCardpool(String cardpool) {
        CardUsageStat result = cardUsageStatsRepository.findByCardPoolName(cardpool);
        if (result == null) {
            result = new CardUsageStat(cardpool, true);
            List<CardCounts> stat = cardRepository.findMostPopularCardsByCardPack(cardpool, "runner");
            for (CardCounts count : stat) {
                result.addCardUsage(new CardUsage(count.getTitle(), count.getCardpack(), "runner", -1, count.getCount()));
                logger.debug(String.format("%s (%s) - %d", count.getTitle(), count.getCardpack(), count.getCount()));
            }
            stat = cardRepository.findMostPopularCardsByCardPack(cardpool, "corp");
            for (CardCounts count : stat) {
                result.addCardUsage(new CardUsage(count.getTitle(), count.getCardpack(), "corp", -1, count.getCount()));
                logger.debug(String.format("%s (%s) - %d", count.getTitle(), count.getCardpack(), count.getCount()));
            }
            logger.info(String.format("Saving Card Usage Statistics, cardpool: %s", cardpool));
            cardUsageStatsRepository.save(result);
        }
        return result;
    }

    /**
     * Calculates average card usage statistics for identity and cardpool.
     * @param identity identity title
     * @param cardpool cardpool name
     * @return IdentityAverage
     */
    public IdentityAverage getIdentityAverage(String identity, String cardpool) {
        IdentityAverage result = identityAverageRepository.findIdentityCardPool(identity, cardpool);
        if (result == null) {
            result = new IdentityAverage(identity, cardpool);
            List<Deck> decks = deckRepository.filterByIdentityAndCardPool(identity, cardpool);
            Set<Card> cards = new HashSet<>();
            int decknum = decks.size();

            // get all used cards
            for (Deck deck : decks) {
                for (DeckHasCard card : deck.getCards()) {
                    if (!cards.contains(card.getCard())) {
                        cards.add(card.getCard());
                    }
                }
            }

            // examine all used cards
            for (Card card : cards) {
                int used = 0;
                int sumused = 0;
                String cardcode = card.getCode();
                for (Deck deck : decks) {
                    for (DeckHasCard deckHasCard : deck.getCards()) {
                        if (cardcode.equals(deckHasCard.getCard().getCode())) {
                            used++;
                            sumused += deckHasCard.getQuantity();
                        }
                    }
                }
                CardAverage cardAverage = new CardAverage(card,
                        String.format("%.1f%%", (double) used / decknum * 100),
                        String.format("%.2f", (double) sumused / decknum),
                        String.format("%.2f", (double) sumused / used));
                result.addCard(cardAverage);
            }
            logger.info(String.format("Saving deck averages: %s - %s", identity, cardpool));
            identityAverageRepository.save(result);
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
