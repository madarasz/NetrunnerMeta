package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.CardPack;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.Tournament;
import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.database.DOs.result.CardCounts;
import com.madarasz.netrunnerstats.database.DOs.result.StatCounts;
import com.madarasz.netrunnerstats.database.DOs.stats.*;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.*;
import com.madarasz.netrunnerstats.database.DRs.*;
import com.madarasz.netrunnerstats.database.DRs.stats.*;
import com.madarasz.netrunnerstats.helper.*;
import com.madarasz.netrunnerstats.helper.comparator.CardCountComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * Created by madarasz on 11/7/15.
 * Handling statistical functions.
 */
@Component
public class Statistics {

    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);
    public static final String LAST_3 = "Last 3 aggregated";

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
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    CardUsageStatsRepository cardUsageStatsRepository;

    @Autowired
    IdentityAverageRepository identityAverageRepository;

    @Autowired
    CardStatRepository cardStatRepository;

    @Autowired
    DeckDigest deckDigest;

    @Autowired
    ColorPicker colorPicker;

    @Autowired
    Operations operations;

    @Autowired
    LastThree lastThree;

    @Autowired
    TournamentDrillDownRepository tournamentDrillDownRepository;

    /**
     * Log statistics about stat count for each faction for a cardpool legality
     * @param cardpackName cardpool name
     * @return DPStatistics
     */
    public DPStatistics getPackStats(String cardpackName) {
        DPStatistics result = dpStatsRepository.findByPackTitle(cardpackName);

        if (result == null) {
            StopWatch stopwatch = new StopWatch();
            stopwatch.start();

            CardPack cardPack = cardPackRepository.findByName(cardpackName);
            int runnerAllStandingCount = standingRepository.countByCardPoolSidecode(cardpackName, "runner");
            int corpAllStandingCount = standingRepository.countByCardPoolSidecode(cardpackName, "corp");
            int runnerTopStandingCount = standingRepository.countTopByCardPoolSidecode(cardpackName, "runner");
            int corpTopStandingCount = standingRepository.countTopByCardPoolSidecode(cardpackName, "corp");
            result = new DPStatistics(cardpackName, cardPack.getNumber(), cardPack.getCyclenumber(),
                    runnerAllStandingCount, corpAllStandingCount, runnerTopStandingCount, corpTopStandingCount);

            List<StatCounts> statsRunner = standingRepository.getFactionStatsByCardPoolSide(cardpackName, "runner");
            List<StatCounts> statsCorp = standingRepository.getFactionStatsByCardPoolSide(cardpackName, "corp");
            for (StatCounts statCounts : statsRunner) {
                result.addRunnerFaction(statCounts.getCategory(), statCounts.getCount(),
                        standingRepository.countTopByCardPoolFaction(cardpackName, statCounts.getCategory()));
            }
            for (StatCounts statCounts : statsCorp) {
                result.addCorpFaction(statCounts.getCategory(), statCounts.getCount(),
                        standingRepository.countTopByCardPoolFaction(cardpackName, statCounts.getCategory()));
            }

            stopwatch.stop();
            logger.debug("*********************************************************************");
            logger.info(String.format("Saving DP Statistics: %s - (%.3f sec)", cardpackName,
                    stopwatch.getTotalTimeSeconds()));
            dpStatsRepository.save(result);
        }

        return result;
    }

    /**
     * Generate multidimensional scaling for identity and cardpool legality
     * @param identityName filter for identity
     * @param cardpackName filter for cardpool
     * @return IdentityMDS
     */
    public Set<MDSEntry> getPackMath(String identityName, String cardpackName) {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        Set<MDSEntry> result = new HashSet<>();
        ArrayList<Deck> decks = new ArrayList<>();
        ArrayList<Deck> topdecks = new ArrayList<>();

        if (cardpackName.equals(LAST_3)) {
            for (String cardpoolName : lastThree.getLastThreeCardpoolNames()) {
                decks.addAll(deckRepository.filterByIdentityAndCardPool(identityName, cardpoolName));
                topdecks.addAll(deckRepository.filterTopByIdentityAndCardPool(identityName, cardpoolName));
            }
        } else {
            decks = new ArrayList<>(deckRepository.filterByIdentityAndCardPool(identityName, cardpackName));
            topdecks = new ArrayList<>(deckRepository.filterTopByIdentityAndCardPool(identityName, cardpackName));
        }

        ArrayList<String> topURLs = new ArrayList<>();
        for (Deck topdeck : topdecks) {
            topURLs.add(topdeck.getUrl());
        }

        if (decks.size() > 0) {
            double[][] distance = multiDimensionalScaling.getDistanceMatrix(decks);
            double[][] scaling = multiDimensionalScaling.calculateMDS(distance);
            for (int i = 0; i < decks.size(); i++) {
                // NaN fix
                scaling[0][i] = NaNFix(scaling[0][i]);
                scaling[1][i] = NaNFix(scaling[1][i]);
                Deck deck = decks.get(i);

                if (cardpackName.equals(LAST_3)) {
                    Tournament tournament = tournamentRepository.getTournamentByDeckUrl(deck.getUrl());
                    MDSEntryLast3 mds = new MDSEntryLast3(scaling[0][i], scaling[1][i], deck.getName(), deck.getUrl(),
                            topURLs.contains(decks.get(i).getUrl()),
                            deckDigest.shortHtmlDigest(deck), deckDigest.htmlDigest(deck), deckDigest.digest(deck),
                            tournament.getCardpool().getName());
                    result.add(mds);
                } else {
                    MDSEntry mds = new MDSEntry(scaling[0][i], scaling[1][i], deck.getName(), deck.getUrl(),
                            topURLs.contains(decks.get(i).getUrl()),
                            deckDigest.shortHtmlDigest(deck), deckDigest.htmlDigest(deck), deckDigest.digest(deck));
                    result.add(mds);
                }

                logger.debug(String.format("\"%s\",\"%f\",\"%f\"", decks.get(i).getUrl(), scaling[0][i], scaling[1][i]));
            }
        }
        stopwatch.stop();
        logger.debug("*********************************************************************");
        logger.info(String.format("* calculating MDS Statistics: %s - %s (%.3f sec)", cardpackName, identityName,
                stopwatch.getTotalTimeSeconds()));
        return result;
    }

    /**
     * Generate multidimensional scaling for faction and cardpool legality
     * @param factionName filter for faction
     * @param cardpackName filter for cardpool
     * @return IdentityMDS
     */
    public Set<MDSEntry> getPackMathFaction(String factionName, String cardpackName) {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        Set<MDSEntry> result = new HashSet<>();
        ArrayList<Deck> decks = new ArrayList<>(deckRepository.filterByFactionAndCardPool(factionName, cardpackName));
        ArrayList<Deck> topdecks = new ArrayList<>(deckRepository.filterTopByFactionAndCardPool(factionName, cardpackName));

        ArrayList<String> topURLs = new ArrayList<>();
        for (Deck topdeck : topdecks) {
            topURLs.add(topdeck.getUrl());
        }

        if (decks.size() > 0) {
            double[][] distance = multiDimensionalScaling.getDistanceMatrix(decks);
            double[][] scaling = multiDimensionalScaling.calculateMDS(distance);
            for (int i = 0; i < decks.size(); i++) {
                // NaN fix
                scaling[0][i] = NaNFix(scaling[0][i]);
                scaling[1][i] = NaNFix(scaling[1][i]);
                Deck deck = decks.get(i);

                MDSEntry mds = new MDSEntryFaction(scaling[0][i], scaling[1][i], deck.getName(), deck.getUrl(),
                        topURLs.contains(decks.get(i).getUrl()),
                        deckDigest.shortHtmlDigest(deck), deckDigest.htmlDigest(deck), deckDigest.digest(deck),
                        deck.getIdentity().getTitle());
                result.add(mds);

                logger.debug(String.format("\"%s\",\"%f\",\"%f\"", decks.get(i).getUrl(), scaling[0][i], scaling[1][i]));
            }
        }
        stopwatch.stop();
        logger.debug("*********************************************************************");
        logger.info(String.format("* calculating MDS Statistics: %s - %s (%.3f sec)", cardpackName, factionName,
                stopwatch.getTotalTimeSeconds()));
        return result;
    }


    /**
     * Generates cardpool statistics: deck number, ranking number, tournament number per cardpool
     * @return CardPoolStats
     */
    public CardPoolStats getCardPoolStats() {
        CardPoolStats result = cardPoolStatsRepository.find();
        if (result == null) {
            StopWatch stopwatch = new StopWatch();
            stopwatch.start();
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
            stopwatch.stop();
            logger.info(String.format("Saving Cardpool Statistics. (%.3f sec)", stopwatch.getTotalTimeSeconds()));
            cardPoolStatsRepository.save(result);
        }
        return result;
    }

    /**
     * Calculates most used a card in the cardpack
     * @param cardpack cardpack name
     * @return CardUsageStat
     */
    public CardUsageStat getMostUsedCardsFromCardPack(String cardpack) {
        CardUsageStat result = cardUsageStatsRepository.findByCardPackName(cardpack);
        if (result == null) {
            StopWatch stopwatch = new StopWatch();
            stopwatch.start();
            int runnerdecks = getDeckNumberFromCardpoolOnward(cardpack, "runner", false);
            int toprunnerdecks = getDeckNumberFromCardpoolOnward(cardpack, "runner", true);
            int corpdecks = getDeckNumberFromCardpoolOnward(cardpack, "corp", false);
            int topcorpdecks = getDeckNumberFromCardpoolOnward(cardpack, "corp", true);
            result = new CardUsageStat(cardpack, runnerdecks, toprunnerdecks, corpdecks, topcorpdecks);
            List<Card> cards = cardRepository.findByCardPackName(cardpack);
            for (Card card : cards) {
                String code = card.getCode();
                int count = deckRepository.countByUsingCard(code);
                if (count > 0) {
                    int topcount = deckRepository.countTopByUsingCard(code);
                    if (card.getSide_code().equals("runner")) {
                        result.addCardUsage(new CardUsage(card.getTitle(), card.getCardPack().getName(),
                                card.getSide_code(), card.getFaction_code(), count, topcount,
                                (double)count / runnerdecks, (double)topcount / toprunnerdecks));
                    } else {
                        result.addCardUsage(new CardUsage(card.getTitle(), card.getCardPack().getName(),
                                card.getSide_code(), card.getFaction_code(), count, topcount,
                                (double)count / corpdecks, (double)topcount / topcorpdecks));
                    }
                } else {
                    result.addCardUsage(new CardUsage(card.getTitle(), card.getCardPack().getName(),
                            card.getSide_code(), card.getFaction_code(), 0, 0, 0, 0));
                }
            }
            stopwatch.stop();
            logger.info(String.format("Saving Card Usage Statistics, cardpack: %s (%.3f sec)", cardpack,
                    stopwatch.getTotalTimeSeconds()));
            cardUsageStatsRepository.save(result);
        }
        return result;
    }


    /**
     * Calculates most used a card with a cardpool of tournaments
     * @param cardpool cardpool name
     * @param sidecode runner or corp
     * @return CardUsage list
     */
    public List<CardUsage> getMostUsedCardsForCardpool(String cardpool, String sidecode) {
        int topdecks = 0;
        int alldecks = 0;
        List<CardCounts> stats;
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        // check is last 3 is requested
        List<String> last3Names = lastThree.getLastThreeCardpoolNames();
        if (cardpool.equals(LAST_3)) {
            stats = cardRepository.findMostPopularCardsBy3CardPack(last3Names.get(0), last3Names.get(1),
                    last3Names.get(2), sidecode);
            for (String cardpoolName : last3Names) {
                topdecks += deckRepository.countTopByCardpoolAndSide(cardpoolName, sidecode);
                alldecks += deckRepository.countByCardpoolAndSide(cardpoolName, sidecode);
            }
        } else {
            stats = cardRepository.findMostPopularCardsByCardPackInTop(cardpool, sidecode);
            topdecks = deckRepository.countTopByCardpoolAndSide(cardpool, sidecode);
            alldecks = deckRepository.countByCardpoolAndSide(cardpool, sidecode);
        }
        List<CardUsage> result = new ArrayList<>();

        for (CardCounts count : stats) {
            int countAll = deckRepository.countByCardpoolUsingCard(cardpool, count.getTitle());
            result.add(new CardUsage(count.getTitle(), count.getCardpack(), sidecode, count.getFaction(), countAll, count.getCount(),
                    (double) countAll / alldecks, (double) count.getCount() / topdecks));
            logger.debug(String.format("%s (%s) - %d", count.getTitle(), count.getCardpack(), count.getCount()));
        }
        stopwatch.stop();
        logger.info(String.format("* calculating Card Usage Statistics: %s - %s (%.3f sec)", cardpool, sidecode,
                stopwatch.getTotalTimeSeconds()));
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
            StopWatch stopwatch = new StopWatch();
            stopwatch.start();
            List<Deck> decks = new ArrayList<>();

            // check is last 3 is requested
            int topdecknum = 0;
            if (cardpool.equals(LAST_3)) {
                for (String cardpoolName : lastThree.getLastThreeCardpoolNames()) {
                    decks.addAll(deckRepository.filterByIdentityAndCardPool(identity, cardpoolName));
                    topdecknum += deckRepository.countTopByIdentityAndCardPool(identity, cardpool);
                }
            } else {
                decks = deckRepository.filterByIdentityAndCardPool(identity, cardpool);
                topdecknum = deckRepository.countTopByIdentityAndCardPool(identity, cardpool);
            }
            Set<Card> cards = new HashSet<>();
            int decknum = decks.size();
            result = new IdentityAverage(identity, cardpool, decknum, topdecknum);

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

            result.addDecks(getPackMath(identity, cardpool));
            stopwatch.stop();
            logger.info(String.format("Saving deck averages: %s - %s (%.3f)", identity, cardpool,
                    stopwatch.getTotalTimeSeconds()));
            identityAverageRepository.save(result);
        }
        return result;
    }

    /**
     * Calculates average card usage statistics for faction and cardpool.
     * @param faction faction title
     * @param cardpool cardpool name
     * @return IdentityAverage
     */
    public IdentityAverage getFactionAverage(String faction, String cardpool) {
        IdentityAverage result = identityAverageRepository.findIdentityCardPool(faction, cardpool);
        if (result == null) {
            StopWatch stopwatch = new StopWatch();
            stopwatch.start();

            // check is last 3 is requested
            int topdecknum = 0;
            List<Deck> decks = deckRepository.filterByFactionAndCardPool(faction, cardpool);
            topdecknum = deckRepository.countTopByCardPoolAndFaction(cardpool, faction);
            Set<Card> cards = new HashSet<>();
            int decknum = decks.size();
            result = new IdentityAverage(faction, cardpool, decknum, topdecknum);

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

            result.addDecks(getPackMathFaction(faction, cardpool));
            stopwatch.stop();
            logger.info(String.format("Saving deck averages: %s - %s (%.3f)", faction, cardpool,
                    stopwatch.getTotalTimeSeconds()));
            identityAverageRepository.save(result);
        }
        return result;
    }

    /**
     * Calculates ICE and ICE breaker usage for a cardpool
     * @param sidecode runner or corp
     * @param cardpool cardpool name
     * @return list of CardAverage stats
     */
    public List<CardAverage> getICEAverage(String sidecode, String cardpool) {

        StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        // special non-ice breaker cards that should be counted
        final List<String> extraCards = new ArrayList<>(Arrays.asList("D4v1d", "e3 Feedback Implants",
                "Grappling Hook"));

        List<CardAverage> result = new ArrayList<>();

        List<Deck> decks = new ArrayList<>();
        if (cardpool.equals(LAST_3)) {
            for (String cardpoolName : lastThree.getLastThreeCardpoolNames()) {
                decks.addAll(deckRepository.findByCardpoolAndSide(cardpoolName, sidecode));
            }
        } else {
            decks = deckRepository.findByCardpoolAndSide(cardpool, sidecode);
        }

        Set<Card> cards = new HashSet<>();
        int decknum = decks.size();

        // get all relevant cards
        for (Deck deck : decks) {
            for (DeckHasCard deckHasCard : deck.getCards()) {
                Card card = deckHasCard.getCard();
                if ((!cards.contains(card)) &&
                        ((card.getSubtype_code().contains("icebreaker")) ||
                                (card.getType_code().contains("ice")) ||
                                (extraCards.contains(card.getTitle())))) {
                    cards.add(card);
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
            result.add(cardAverage);
        }
        stopwatch.stop();
        logger.info(String.format("* calculating ICE/breaker averages: %s - %s (%.3f)", sidecode, cardpool,
                stopwatch.getTotalTimeSeconds()));
        return result;
    }

    /**
     * Returns the number of decks from a cardpool and onward
     * @param cardpool cardpool name
     * @param side side_code
     * @param topdeck just top decks?
     * @return deck number
     */
    public int getDeckNumberFromCardpoolOnward(String cardpool, String side, boolean topdeck) {
        List<CardPool> cardPools = getCardPoolStats().getSortedCardpool();
        Collections.reverse(cardPools);
        CardPack cardPack;
        if (cardpool.equals(LAST_3)) {
            cardPack = cardPackRepository.findByName(cardPools.get(2).getTitle());
        } else {
            cardPack = cardPackRepository.findByName(cardpool);
        }
        int result = 0;
        boolean count = false;
        for (CardPool currentPool : cardPools) {
            if (currentPool.getCyclenumber()*100 + currentPool.getDpnumber()
                    >= cardPack.getCyclenumber()*100 + cardPack.getNumber()) {
                count = true;
            }
            if (count) {
                if (topdeck) {
                    result += deckRepository.countTopByCardpoolAndSide(currentPool.getTitle(), side);
                } else {
                    result += deckRepository.countByCardpoolAndSide(currentPool.getTitle(), side);
                }
            }
        }
        return result;
    }

    /**
     * Statistics for a single card.
     * Includes usage over time, top cards/identities, possible combos, deck links.
     * @param cardTitle card title
     * @return CardStat
     */
    public CardStat getCardStats(String cardTitle) {
        CardStat result = cardStatRepository.findbyTitle(cardTitle);
        if (result == null) {
            Card card = cardRepository.findByTitle(cardTitle);

            if (card != null) {
                StopWatch stopwatch = new StopWatch();
                stopwatch.start();

                result = new CardStat(card);
                String side = card.getSide_code();
                List<CardPool> cardPools = getCardPoolStats().getSortedCardpool();
                List<CardPool> validPools = new ArrayList<>(cardPools);
                Collections.reverse(cardPools);
                CardCountComparator comparator = new CardCountComparator();

                // deck/standing numbers over time
                for (CardPool cardPool : cardPools) {
                    String pack = cardPool.getTitle();
                    if (!card.getCardPack().later(cardPackRepository.findByName(pack))) {
                        if (card.getType_code().equals("identity")) {
                            // count standings for identities
                            int standingnum = standingRepository.countByCardPoolSidecode(pack, side);
                            int topstandingnum = standingRepository.countTopByCardPoolSidecode(pack, side);
                            int using = standingRepository.countByCardPoolId(pack, cardTitle);
                            int topusing = standingRepository.countTopByCardPoolId(pack, cardTitle);
                            result.addOverTime(
                                    new CardUsage(cardTitle, pack, side, card.getFaction_code(), using, topusing,
                                            (float) using / standingnum, (float) topusing / topstandingnum));
                            logger.debug(String.format("%s - top:%,.3f%% all:%,.3f%%", pack,
                                    (float) topusing / topstandingnum, (float) using / standingnum));
                        } else {
                            // count decks for non-identity cards
                            int decknum = deckRepository.countByCardpoolAndSide(pack, side);
                            int topdecknum = deckRepository.countTopByCardpoolAndSide(pack, side);
                            int using = deckRepository.countByCardpoolUsingCard(pack, cardTitle);
                            int topusing = deckRepository.countTopByCardpoolUsingCard(pack, cardTitle);
                            result.addOverTime(
                                    new CardUsage(cardTitle, pack, side, card.getFaction_code(), using, topusing,
                                            (float) using / decknum, (float) topusing / topdecknum));
                            logger.debug(String.format("%s - top:%,.3f%% all:%,.3f%%", pack,
                                    (float) topusing / topdecknum, (float) using / decknum));
                        }
                    } else {
                        validPools.remove(cardPool);
                    }
                }

                // statistics on last three packs
                // get all decks using card
                List<CardPool> last3Pools = new ArrayList<>(validPools);
                last3Pools = trimCardPool(last3Pools, 3);
                int alldeck = 0;
                List<Deck> decks = new ArrayList<>();
                List<TournamentDrilldown> drilldowns = new ArrayList<>();
                for (CardPool cardPool : last3Pools) {
                    String pack = cardPool.getTitle();
                    decks.addAll(deckRepository.findByCardpoolUsingCard(pack, cardTitle));
                    drilldowns.add(getTournamentDrilldown(pack, side));
                }
                int countcard = decks.size();
                logger.debug(String.format("All decks: %d - deck using card: %d", alldeck, countcard));

                // get top cards/identities
                List<CardCount> counts = new ArrayList<>();
                if (card.getType_code().equals("identity")) {
                    // get top cards
                    List<Card> cards = new ArrayList<>();
                    for (Deck deck : decks) {
                        for (DeckHasCard deckHasCard : deck.getCards()) {
                            Card cardi = deckHasCard.getCard();
                            if (!cards.contains(cardi)) {
                                cards.add(cardi);
                            }
                        }
                    }
                    for (Card cardi : cards) {
                        int count = 0;
                        for (Deck deck : decks) {
                            for (DeckHasCard deckHasCard : deck.getCards()) {
                                if (deckHasCard.getCard().equals(cardi)) {
                                    count++;
                                    break;
                                }
                            }
                        }
                        counts.add(new CardCount(cardi, count));
                    }
                    counts.sort(comparator);
                    counts = trimCardCount(counts, 8);

                    for (CardCount cardCount : counts) {
                        if (cardCount.getCount() > 0) {
                            Card id = cardCount.getCard();
                            result.addTop(new CardUsage(id.getTitle(), id.getCardPack().getName(), side, id.getFaction_code(), cardCount.getCount(), -1,
                                    (float) cardCount.getCount() / decks.size(), -1));
                            logger.debug(String.format("%s - %,.3f%%",
                                    cardCount.getCard().getTitle(), (float) cardCount.getCount() / decks.size()));
                        }
                    }

                } else {
                    // get top identities
                    List<Card> identities = cardRepository.findIdentitiesBySide(side);
                    for (Card identity : identities) {
                        int count = 0;
                        for (Deck deck : decks) {
                            if (deck.getIdentity().equals(identity)) {
                                count++;
                            }
                        }
                        if (count > 0) {
                            int deckcount = 0;
                            for (TournamentDrilldown drilldown : drilldowns) {
                                Set<StandingDeckCountID> standingDeckCountIDs;
                                standingDeckCountIDs = drilldown.getIds();
                                for (StandingDeckCountID standingDeckCountID : standingDeckCountIDs) {
                                    if (standingDeckCountID.getTitle().equals(identity.getTitle())) {
                                        deckcount += standingDeckCountID.getAllDeckCount();
                                        break;
                                    }
                                }
                            }
                            result.addTop(new CardUsage(identity.getTitle(), identity.getCardPack().getName(), side,
                                    identity.getFaction_code(), count, -1, (float) count / deckcount, -1));
                            logger.debug(String.format("%s - %,.3f%%",
                                    identity.getTitle(), (float) count / deckcount));
                        }
                    }
                }

                // get possible combos
                List<Card> cards = new ArrayList<>();
                for (Deck deck : decks) {
                    for (DeckHasCard deckHasCard : deck.getCards()) {
                        Card icard = deckHasCard.getCard();
                        if (!cards.contains(icard)) {
                            cards.add(icard);
                        }
                    }
                }
                cards.remove(card);
                counts.clear();

                for (Card icard : cards) {
                    int icount = 0;
//                    int countoneof = 0;
                    int countboth = 0;
                    for (CardPool cardPool : validPools) {
                        if (icard.getCardPack().getCyclenumber()*100 + icard.getCardPack().getNumber() <=
                                cardPool.getCyclenumber()*100 + cardPool.getDpnumber()) {
                            icount += deckRepository.countByCardpoolUsingCard(cardPool.getTitle(), icard.getTitle());
//                            countoneof += deckRepository.countByCardpoolUsingCardOneOf(cardPool.getTitle(), cardTitle, icard.getTitle());
                            countboth += deckRepository.countByCardpoolUsingCardBoth(cardPool.getTitle(), cardTitle, icard.getTitle());
                        }
                    }
//                    counts.add(new CardCount(icard, (int) ((float) countboth / countoneof * 100))); //Jaccard similarity
                    counts.add(new CardCount(icard, (int) ((float) countboth / icount * countboth / countcard * 100)));
//                    logger.trace(String.format("***,%s,%d,%d", icard.getTitle(), countboth, countoneof));
                    logger.trace(String.format("***,%s,%d,%d", icard.getTitle(), countboth, icount));
                }
                counts.sort(comparator);
                counts = trimCardCount(counts, 10);
                for (CardCount cardCount : counts) {
                    Card combo = cardCount.getCard();
                    result.addCombos(new CardCombo(combo.getTitle(), combo.getCardPack().getName(), combo.getFaction_code(), cardCount.getCount()));
                    logger.debug(String.format("%s - %d",
                            combo.getTitle(), cardCount.getCount()));
                }

                // generate deck links
                for (CardPool cardPool : validPools) {
                    String dptitle = cardPool.getTitle();
                    DPDecks dpDecks = new DPDecks(dptitle, deckRepository.countByCardpoolUsingCard(dptitle, cardTitle));
                    List<Deck> decksInDP = deckRepository.findBestByCardpoolUsingCard(dptitle, cardTitle);
                    for (Deck deck : decksInDP) {
                        dpDecks.addDeckLink(deckDigest.getDeckLink(deck));
                    }
                    if (dpDecks.getCount() > 10) {
                        dpDecks.addDeckLink("..."); // symbol for more decks
                    }
                    result.addDecks(dpDecks);
                }
                stopwatch.stop();
                logger.info(String.format("Saving stats for card: %s (%.3f sec)", cardTitle ,
                        stopwatch.getTotalTimeSeconds()));
                cardStatRepository.save(result);
            } else {
                logger.error("No such card: " + cardTitle);
            }
        }
        return result;
    }

    /**
     * Logs win-more cards with best top 30% / all ratio
     * EXPERIMENTAL
     */
    public void getWinMoreCards() {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        List<Card> cards = cardRepository.findAllCards();
        List<CardCount> counts = new ArrayList<>();
        for (Card card : cards) {
            List<CardUsage> cardStat = getCardStats(card.getTitle()).getOverTime();
            if (cardStat.size() > 0) {
                CardUsage last = cardStat.get(cardStat.size() - 1);
                counts.add(new CardCount(card, (int) (last.getTopdeckfraction() * 1000 - last.getDeckfraction() * 1000)));
            }
        }

        CardCountComparator comparator = new CardCountComparator();
        counts.sort(comparator);
        counts = trimCardCount(counts, 20);

        for (CardCount cardCount : counts) {
            logger.info(String.format("%d - %s", cardCount.getCount(), cardCount.getCard().getTitle()));
        }

        stopwatch.stop();
        logger.info(String.format("Caltulating top win-more (%.3f sec)", stopwatch.getTotalTimeSeconds()));
    }

    public List<StandingDeckCount> getTournamentFactions(String cardpoolTitle, String sideCode) {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        List<String> last3Names = lastThree.getLastThreeCardpoolNames();
        List<StandingDeckCount> result = new ArrayList<>();
        if (cardpoolTitle.equals(LAST_3)) {
            List<StatCounts> factions = standingRepository.getFactionStatsBy3CardPoolSide(
                    last3Names.get(0), last3Names.get(1), last3Names.get(2), sideCode);
            for (StatCounts faction : factions) {
                String title = faction.getCategory();
                int allStandingCount = faction.getCount();
                int topStandingCount = 0;
                int allDeckCount = 0;
                int topDeckCount = 0;
                for (String cardpool : last3Names) {
                    topStandingCount += standingRepository.countTopByCardPoolFaction(cardpool, title);
                    allDeckCount += deckRepository.countByCardPoolAndFaction(title, cardpool);
                    topDeckCount += deckRepository.countTopByCardPoolAndFaction(title, cardpool);
                }
                result.add(new StandingDeckCount(title, allStandingCount, topStandingCount, allDeckCount, topDeckCount));
            }
        } else {
            List<StatCounts> factions = standingRepository.getFactionStatsByCardPoolSide(cardpoolTitle, sideCode);
            for (StatCounts faction : factions) {
                String title = faction.getCategory();
                int allStandingCount = faction.getCount();
                int topStandingCount = standingRepository.countTopByCardPoolFaction(cardpoolTitle, title);
                int allDeckCount = deckRepository.countByCardPoolAndFaction(cardpoolTitle, title);
                int topDeckCount = deckRepository.countTopByCardPoolAndFaction(cardpoolTitle, title);
                result.add(new StandingDeckCount(title, allStandingCount, topStandingCount, allDeckCount, topDeckCount));
            }
        }
        stopwatch.stop();
        logger.info(String.format("* calculating factions: %s - %s (%.3f)", cardpoolTitle, sideCode,
                stopwatch.getTotalTimeSeconds()));
        return result;
    }

    public List<StandingDeckCountID> getTournamentIdentities(String cardpoolTitle, String sideCode) {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        List<String> last3Names = lastThree.getLastThreeCardpoolNames();
        List<StandingDeckCountID> result = new ArrayList<>();
        if (cardpoolTitle.equals(LAST_3)) {
            List<StatCounts> identities = standingRepository.getIdentityStatsBy3CardPoolSide(
                    last3Names.get(0), last3Names.get(1), last3Names.get(2), sideCode);
            for (StatCounts id : identities) {
                String title = id.getCategory();
                int allStandingCount = id.getCount();
                int topStandingCount = 0;
                int allDeckCount = 0;
                int topDeckCount = 0;
                for (String cardpool : last3Names) {
                    topStandingCount += standingRepository.countTopByCardPoolId(cardpool, title);
                    allDeckCount += deckRepository.countByIdentityAndCardPool(title, cardpool);
                    topDeckCount += deckRepository.countTopByIdentityAndCardPool(title, cardpool);
                }
                Card card = cardRepository.findByTitle(title);
                result.add(new StandingDeckCountID(title, card.getFaction_code(), allStandingCount, topStandingCount, allDeckCount, topDeckCount));
            }
        } else {
            List<StatCounts> identities = standingRepository.getIdentityStatsByCardPoolSide(cardpoolTitle, sideCode);
            for (StatCounts id : identities) {
                String title = id.getCategory();
                int allStandingCount = id.getCount();
                int topStandingCount = standingRepository.countTopByCardPoolId(cardpoolTitle, title);
                int allDeckCount = deckRepository.countByIdentityAndCardPool(title, cardpoolTitle);
                int topDeckCount = deckRepository.countTopByIdentityAndCardPool(title, cardpoolTitle);
                Card card = cardRepository.findByTitle(title);
                result.add(new StandingDeckCountID(title, card.getFaction_code(), allStandingCount, topStandingCount, allDeckCount, topDeckCount));
            }
        }
        stopwatch.stop();
        logger.info(String.format("* calculating ids: %s - %s (%.3f)", cardpoolTitle, sideCode,
                stopwatch.getTotalTimeSeconds()));
        return result;
    }

    /**
     * Calculates TournamentDrilldown statistics
     * @param cardpoolTitle cardpool name
     * @param sideCode runner / corp
     * @return TournamentDrilldown
     */
    public TournamentDrilldown getTournamentDrilldown(String cardpoolTitle, String sideCode) {
        TournamentDrilldown result = tournamentDrillDownRepository.findByCardpoolSidecode(cardpoolTitle, sideCode);

        if (result == null) {
            StopWatch stopwatch = new StopWatch();
            stopwatch.start();

            List<String> last3Names = lastThree.getLastThreeCardpoolNames();
            int cycleNum = -1;
            int dpNum = -1;
            int allStandingCount = 0;
            int topStandingCount = 0;
            int allDeckCount = 0;
            int topDeckCount = 0;

            // calculating counts
            if (cardpoolTitle.equals(LAST_3)) {
                for (String cardpool : last3Names) {
                    allStandingCount += standingRepository.countByCardPoolSidecode(cardpool, sideCode);
                    topStandingCount += standingRepository.countTopByCardPoolSidecode(cardpool, sideCode);
                    allDeckCount += deckRepository.countByCardpoolAndSide(cardpool, sideCode);
                    topDeckCount += deckRepository.countTopByCardpoolAndSide(cardpool, sideCode);
                }
            } else {
                CardPack cardpool = cardPackRepository.findByName(cardpoolTitle);
                cycleNum = cardpool.getCyclenumber();
                dpNum = cardpool.getNumber();

                allStandingCount = standingRepository.countByCardPoolSidecode(cardpoolTitle, sideCode);
                topStandingCount = standingRepository.countTopByCardPoolSidecode(cardpoolTitle, sideCode);
                allDeckCount = deckRepository.countByCardpoolAndSide(cardpoolTitle, sideCode);
                topDeckCount = deckRepository.countTopByCardpoolAndSide(cardpoolTitle, sideCode);
            }

            result = new TournamentDrilldown(cardpoolTitle, sideCode, cycleNum, dpNum,
                    allStandingCount, topStandingCount, allDeckCount, topDeckCount);

            // calculating identities
            result.addIds(getTournamentIdentities(cardpoolTitle, sideCode));

            // calculating factions
            result.addFactions(getTournamentFactions(cardpoolTitle, sideCode));

            // calculating ICE/breaker stats
            result.addIce(getICEAverage(sideCode, cardpoolTitle));

            // calculating most used cards in cardpool
            result.addMostUsedCard(getMostUsedCardsForCardpool(cardpoolTitle, sideCode));

            stopwatch.stop();
            logger.info(String.format("Saving tournament drilldown (%.3f sec): %s - %s",
                    stopwatch.getTotalTimeSeconds(), cardpoolTitle, sideCode));
            tournamentDrillDownRepository.save(result);
        }
        return result;
    }

    /**
     * WARNING: cycle names are not in DB. They are coming from Enums.
     * @return Cycle-Datapack structure
     */
    public List<Cycle> getDPStructure() {
        List<Cycle> result = new ArrayList<>();
        for (int i = 1; i< Enums.CardCycles.values().length; i++) {
            Cycle cycle = new Cycle(i);
            List<String> dps = cardPackRepository.getSortedPackNamesInCycle(i);
            if (dps.size() > 1) {
                cycle.addDatapacks(dps);
            }
            result.add(cycle);
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

    private List<CardPool> trimCardPool(List<CardPool> list, int size) {
        if (list.size() > size) {
            return new ArrayList<>(list.subList(0, size));
        } else {
            return list;
        }
    }

    private List<CardCount> trimCardCount(List<CardCount> list, int size) {
        if (list.size() > size) {
            return new ArrayList<>(list.subList(0, size));
        } else {
            return list;
        }
    }
}
