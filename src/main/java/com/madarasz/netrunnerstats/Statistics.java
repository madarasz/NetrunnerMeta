package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.database.DOs.*;
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
    public static final String CORE_2 = "Revised Core Set";

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
    MatchRepository matchRepository;

    @Autowired
    MatchCountRepository matchCountRepository;

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
     * @return MDSEntry
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
     * @return MDSEntry
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
                int matchnum = matchRepository.countForPool(title);
                CardPool cardPool = new CardPool(title, tournamentnum, decknum, standingsnum, matchnum,
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
            int runnerdecks = getDeckNumberFromCardpoolOnward(cardpack, "runner", false, true);
            int toprunnerdecks = getDeckNumberFromCardpoolOnward(cardpack, "runner", true, true);
            int corpdecks = getDeckNumberFromCardpoolOnward(cardpack, "corp", false, true);
            int topcorpdecks = getDeckNumberFromCardpoolOnward(cardpack, "corp", true, true);
            List<String> last3Names = lastThree.getLastThreeCardpoolNames();
            result = new CardUsageStat(cardpack, runnerdecks, toprunnerdecks, corpdecks, topcorpdecks);
            List<Card> cards = cardRepository.findByCardPackName(cardpack);
            for (Card card : cards) {
                String code = card.getCode();
//                int count = deckRepository.countByUsingCard(code);
                int count = 0;
                for (String cardpoolName : last3Names) {
                    count += deckRepository.countByCardpoolUsingCard(cardpoolName, card.getTitle());
                }


                if (count > 0) {
//                    int topcount = deckRepository.countTopByUsingCard(code);
                    int topcount = 0;
                    for (String cardpoolName : last3Names) {
                        topcount += deckRepository.countTopByCardpoolUsingCard(cardpoolName, card.getTitle());
                    }

                    if (card.getSide_code().equals("runner")) {
                        result.addCardUsage(new CardUsage(card.getTitle(), card.getCardPack().getName(),
                                card.getSide_code(), card.getFaction_code(), count, topcount,
                                (double)count / runnerdecks, (double)topcount / toprunnerdecks,
                                card.getCardPack().getNumber(), card.getCardPack().getCyclenumber()));
                    } else {
                        result.addCardUsage(new CardUsage(card.getTitle(), card.getCardPack().getName(),
                                card.getSide_code(), card.getFaction_code(), count, topcount,
                                (double)count / corpdecks, (double)topcount / topcorpdecks,
                                card.getCardPack().getNumber(), card.getCardPack().getCyclenumber()));
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
        int dpnum = -1;
        int cyclenum = -1;
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
            CardPack cardPack = cardPackRepository.findByName(cardpool);
            dpnum = cardPack.getNumber();
            cyclenum = cardPack.getCyclenumber();
        }
        List<CardUsage> result = new ArrayList<>();

        for (CardCounts count : stats) {
            int countAll = deckRepository.countByCardpoolUsingCard(cardpool, count.getTitle());
            result.add(new CardUsage(count.getTitle(), count.getCardpack(), sidecode, count.getFaction(), countAll, count.getCount(),
                    (double) countAll / alldecks, (double) count.getCount() / topdecks,
                    dpnum, cyclenum));
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
            int topdecknum;
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
                "Grappling Hook", "Security Nexus", "Kongamato", "Gbahali"));

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
                        ((card.getSubtype_code().contains("Icebreaker")) ||
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
    public int getDeckNumberFromCardpoolOnward(String cardpool, String side, boolean topdeck, boolean countLast3) {
        List<CardPool> cardPools = getCardPoolStats().getSortedCardpool();

        // if only counting last 3 cardpools
        if (countLast3) {
            cardPools = cardPools.subList(0,3);
        }

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
            if (count || cardpool.equals(CORE_2)) {
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
     * If some parts are missing due to DP statistics purge, it will be recalculated
     * @param cardTitle card title
     * @return CardStat
     */
    public CardStat getCardStats(String cardTitle) {
        Card card = cardRepository.findByTitle(cardTitle);
        if (card != null) {

            CardStat result = cardStatRepository.findbyTitle(cardTitle);
            if (result == null) {
                result = new CardStat(card);
            }

            StopWatch stopwatch = new StopWatch();
            stopwatch.start();
            boolean changed = false;
            boolean changedLast3 = false;

            String side = card.getSide_code();
            List<CardPool> cardPools = getCardPoolStats().getSortedCardpool();
            List<CardPool> validPools = new ArrayList<>(cardPools);
            Collections.reverse(cardPools);
            CardCountComparator comparator = new CardCountComparator();

            // deck/standing numbers over time
            for (CardPool cardPool : cardPools) {
                String pack = cardPool.getTitle();
                if (!card.getCardPack().later(cardPackRepository.findByName(pack)) || // if the card is later than the pool
                        card.getCardPack().getCode().equals("core2")) {  // or it's 'Revised Core Set'
                    if (!result.isInOverTime(pack)) {   // if it's not already in the stats

                        changed = true;
                        if (lastThree.isInLastThree(pack)) {
                            changedLast3 = true;
                        }

                        if (card.getType_code().equals("identity")) {
                            // count standings for identities
                            int standingnum = standingRepository.countByCardPoolSidecode(pack, side);
                            int topstandingnum = standingRepository.countTopByCardPoolSidecode(pack, side);
                            int using = standingRepository.countByCardPoolId(pack, cardTitle);
                            int topusing = standingRepository.countTopByCardPoolId(pack, cardTitle);
                            result.addOverTime(
                                    new CardUsage(cardTitle, pack, side, card.getFaction_code(), using, topusing,
                                            (float) using / standingnum, (float) topusing / topstandingnum,
                                            cardPool.getDpnumber(), cardPool.getCyclenumber()));
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
                                            (float) using / decknum, (float) topusing / topdecknum,
                                            cardPool.getDpnumber(), cardPool.getCyclenumber()));
                            logger.debug(String.format("%s - top:%,.3f%% all:%,.3f%%", pack,
                                    (float) topusing / topdecknum, (float) using / decknum));
                        }
                    }
                } else {
                    validPools.remove(cardPool);
                }
            }

            // statistics on last three packs
            if (changedLast3) {
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

                // get top cards/identities from last 3
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
                                    (float) cardCount.getCount() / decks.size(), -1,
                                    id.getCardPack().getNumber(), id.getCardPack().getCyclenumber()));
                            logger.debug(String.format("%s - %,.3f%%",
                                    cardCount.getCard().getTitle(), (float) cardCount.getCount() / decks.size()));
                        }
                    }

                } else {
                    // get top identities from last 3
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
                                    identity.getFaction_code(), count, -1, (float) count / deckcount, -1,
                                    identity.getCardPack().getNumber(), identity.getCardPack().getCyclenumber()));
                            logger.debug(String.format("%s - %,.3f%%",
                                    identity.getTitle(), (float) count / deckcount));
                        }
                    }
                }

                // get possible combos from last 3
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
                        if (icard.getCardPack().getCyclenumber() * 100 + icard.getCardPack().getNumber() <=
                                cardPool.getCyclenumber() * 100 + cardPool.getDpnumber()) {
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
            }

            // generate deck links
            for (CardPool cardPool : validPools) {
                String dptitle = cardPool.getTitle();
                if (!result.isInDecks(dptitle)) {   // if it's not already in stats
                    DPDecks dpDecks = new DPDecks(dptitle, deckRepository.countByCardpoolUsingCard(dptitle, cardTitle),
                            cardPool.getDpnumber(), cardPool.getCyclenumber());
                    List<Deck> decksInDP = deckRepository.findBestByCardpoolUsingCard(dptitle, cardTitle);
                    for (Deck deck : decksInDP) {
                        dpDecks.addDeckLink(deckDigest.getDeckLink(deck));
                    }
                    if (dpDecks.getCount() > 10) {
                        dpDecks.addDeckLink("..."); // symbol for more decks
                    }
                    result.addDecks(dpDecks);
                }
            }
            stopwatch.stop();
            if (changed) {
                logger.info(String.format("Saving/updating stats for card: %s (%.3f sec)", cardTitle,
                        stopwatch.getTotalTimeSeconds()));
                cardStatRepository.save(result);
            }
            return result;
        } else {
            logger.error("No such card: " + cardTitle);
            return null;
        }
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
                if (last.getDeckfraction() > 0.05) {
                    counts.add(new CardCount(card, (int) (last.getTopdeckfraction() / last.getDeckfraction() * 100)));
                }
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
                result.add(new StandingDeckCountID(title, card.getFaction_code(), allStandingCount, topStandingCount,
                        allDeckCount, topDeckCount, getIDWinrate(cardpoolTitle, title)));
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
                result.add(new StandingDeckCountID(title, card.getFaction_code(), allStandingCount, topStandingCount,
                        allDeckCount, topDeckCount, getIDWinrate(cardpoolTitle, title)));
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

    public MatchCount getIDWinrate(String cardPoolTitle, String idTitle) {
        MatchCount result = matchCountRepository.findByCardPackID(cardPoolTitle, idTitle);

        if (result == null) {

            List<Match> matches = matchRepository.findForPoolID(cardPoolTitle, idTitle);

            int winCount = 0;
            int timedWinCount = 0;
            int tieCount = 0;
            for (Match match : matches) {
                if (match.isTie()) {
                    tieCount++;
                } else if (match.getWinner().getTitle().equals(idTitle)) {
                    if (match.isTimed()) {
                        timedWinCount++;
                    } else {
                        winCount++;
                    }
                }
            }

            result = new MatchCount(matches.size(), winCount, timedWinCount, tieCount);
            logger.info("** Saving relevant matches statistics: " + matches.size());
            matchCountRepository.save(result);
        }

        return result;
    }

    /**
     * WARNING: cycle names are not in DB. They are coming from Enums.
     * @return Cycle-Datapack structure
     */
    public List<Cycle> getDPStructure() {
        List<Cycle> result = new ArrayList<>();
        for (Enums.CardCycles theCycle : Enums.CardCycles.values()) {
            Cycle cycle = new Cycle(theCycle.getCycleNumber());
            List<String> dps = cardPackRepository.getSortedPackNamesInCycle(theCycle.getCycleNumber());
            if (dps.size() > 0) {
                if (dps.size() > 1) {
                    cycle.addDatapacks(dps);
                } else if (!dps.get(0).equals(cycle.getTitle())) {
                    cycle.addDatapacks(dps);
                }
                result.add(cycle);
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
