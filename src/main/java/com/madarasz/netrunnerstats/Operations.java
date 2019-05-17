package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.brokers.ABRBroker;
import com.madarasz.netrunnerstats.database.DOs.*;
import com.madarasz.netrunnerstats.database.DOs.admin.AdminData;
import com.madarasz.netrunnerstats.database.DOs.admin.VerificationProblem;
import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.database.DOs.stats.*;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.*;
import com.madarasz.netrunnerstats.database.DRs.*;
import com.madarasz.netrunnerstats.brokers.AcooBroker;
import com.madarasz.netrunnerstats.brokers.NetrunnerDBBroker;
import com.madarasz.netrunnerstats.brokers.StimhackBroker;
import com.madarasz.netrunnerstats.helper.DeckValidator;
import com.madarasz.netrunnerstats.helper.LastThree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Handling all database loading, manupilating operations of the application.
 * Created by istvan on 2015-06-11.
 */
@Component
public class Operations {

    private static final Logger logger = LoggerFactory.getLogger(Operations.class);
    private final DateFormat df = new SimpleDateFormat("yyyy.MM.dd.");
    private final DateFormat df2 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    public static final String LAST_3 = "Last 3 aggregated";

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;

    @Autowired
    CardCycleRepository cardCycleRepository;

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    StandingRepository standingRepository;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    DeckValidator deckValidator;

    @Autowired
    NetrunnerDBBroker netrunnerDBBroker;

    @Autowired
    AcooBroker acooBroker;

    @Autowired
    ABRBroker abrBroker;

    @Autowired
    StimhackBroker stimhackBroker;

    @Autowired
    Neo4jOperations template;

    @Autowired
    AdminDataRepository adminDataRepository;

    @Autowired
    LastThree lastThree;

    /**
     * Wipes DB clean
     */
    public void cleanDB() {
        logger.info("Cleaning DB.");
        Map<String, Object> emptyparams = new HashMap<>();
        template.query("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
        // troublemaker nodes
        template.query("MATCH ()-[h:HAS_CARD]->() DELETE h", emptyparams);
        template.query("MATCH (n:CountDeckStands) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
//        logDBCount();
    }

    /**
     * Logs DB node, relationship count
     */
    public void logDBCount() {
        logger.info(String.format("Cards: %d, CardPacks: %d, CardCycles: %d, Decks: %d, Standings: %d, Tournaments: %d, " +
                        "Deck-card relations: %d",
                template.count(Card.class), template.count(CardPack.class), template.count(CardCycle.class),
                template.count(Deck.class), template.count(Standing.class), template.count(Tournament.class),
                template.count(DeckHasCard.class)));
    }

    /**
     * Logs DB Stats node, relationship count
     */
    public void logDBStatCount() {
        logger.info(String.format("CardPoolStats %d, CardPool: %d, DP statistics: %d, CountDeckStands: %d, " +
                "MDSEntry: %d, " +
                "CardUsageStat: %d, CardUsage: %d, IdentityAverage: %d, CardAverage: %d, Cardstat: %d, Cardcombo: %d, DPDecks: %d " +
                "TournamentDrilldown: %d, StandingDeckCount: %d, StandingDeckCountID: %d, MatchCount: %d",
                template.count(CardPoolStats.class), template.count(CardPool.class),
                template.count(DPStatistics.class), template.count(CountDeckStands.class),
                template.count(MDSEntry.class),
                template.count(CardUsageStat.class), template.count(CardUsage.class),
                template.count(IdentityAverage.class), template.count(CardAverage.class),
                template.count(CardStat.class), template.count(CardCombo.class), template.count(DPDecks.class),
                template.count(TournamentDrilldown.class), template.count(StandingDeckCount.class), template.count(StandingDeckCountID.class),
                template.count(MatchCount.class)));
    }

    /**
     * Loads card pack and card information to DB from NetrunnerDB
     */
    public void loadNetrunnerDB() {
        // load and save card cycles
        Set<CardCycle> allCardCycles = netrunnerDBBroker.readCycles();
        int found = 0;
        for (CardCycle cardCycle : allCardCycles) {
            if (cardCycleRepository.findByName(cardCycle.getName()) == null) {
                cardCycleRepository.save(cardCycle);
                logger.trace("Found cycle: " + cardCycle.toString());
                found++;
            }
        }
        logger.info("Found new cycles: " + found);

        // load and save card packs
        Set<CardPack> allCardPacks = netrunnerDBBroker.readSets();
        found = 0;
        for (CardPack cardPack : allCardPacks) {
            if (cardPackRepository.findByCode(cardPack.getCode()) == null) {
                cardPackRepository.save(cardPack);
                logger.trace("Found pack: " + cardPack.toString());
                found++;
            }
            // add pack to its cycle if necessary
            CardCycle cycle = cardCycleRepository.findByCyclenumber(cardPack.getCyclenumber());
            if (!cycle.getCardPacks().contains(cardPack)) {
                cycle.addCardPack(cardPack);
                cardCycleRepository.save(cycle);
                logger.trace("Added pack to its cycle " + cardPack.toString());
            }
        }
        logger.info("Found new card packs: " + found);

        // load and save cards
        Set<Card> allCards = netrunnerDBBroker.readCards();
        found = 0;
        for (Card card : allCards) {
            logger.info("Checking card:" + card.getTitle() + " (" + card.getCode() + ") - " + card.getCardPack().getName());
            Card foundCard = cardRepository.findByCode(card.getCode());
            if (foundCard == null) {
                // check for older card with same name
                Card oldCard = cardRepository.findByTitle(card.getTitle());
                if (oldCard != null && !oldCard.getCardPack().getCode().equals("sc19")) {
                    logger.warn("Old card prefixed: " + oldCard.getTitle());
                    oldCard.setTitle(oldCard.getTitle() + " (old) - " + oldCard.getCardPack().getCode());
                    cardRepository.save(oldCard);
                }
                cardRepository.save(card);
                logger.info("Found new card: " + card.toString());
                found++;
            } else {
                if (!foundCard.isSame(card) && !foundCard.getTitle().contains(" (old)")) {
                    foundCard.resetValues(card);
                    cardRepository.save(foundCard);
                    logger.warn("Card values updated: " + foundCard.getTitle());
                }
                if (foundCard.getCardPack() == null) {
                    foundCard.setCardPack(card.getCardPack());
                    cardRepository.save(foundCard);
                    logger.warn("Card updated with pack: " + foundCard.toString() + " - " + foundCard.getCardPack());
                }
            }
        }
        logger.info("Found new cards: " + found);

        TweekNetrunnerDB();
    }

    /**
     * Correct TD release during mid cycle
     */
    public void TweekNetrunnerDB() {
        final String RED_SAND_PART_2 = "Red Sand part 2";
        final int RED_SAND_CYCLE_NUM = 14;

        // create Red Sand Part 2
        CardCycle rs2 = cardCycleRepository.findByName(RED_SAND_PART_2);
        if (rs2 == null) {
            rs2 = new CardCycle(RED_SAND_PART_2, "red-sand-2", RED_SAND_CYCLE_NUM);
            cardCycleRepository.save(rs2);
        }

        // move packs to Red Sand Part 2
        CardPack eas = cardPackRepository.findByName("Earth's Scion");
        eas.setCyclenumber(RED_SAND_CYCLE_NUM);
        cardPackRepository.save(eas);
        CardPack baw = cardPackRepository.findByName("Blood and Water");
        baw.setCyclenumber(RED_SAND_CYCLE_NUM);
        cardPackRepository.save(baw);
        CardPack fm = cardPackRepository.findByName("Free Mars");
        fm.setCyclenumber(RED_SAND_CYCLE_NUM);
        cardPackRepository.save(fm);
        CardPack cd = cardPackRepository.findByName("Crimson Dust");
        cd.setCyclenumber(RED_SAND_CYCLE_NUM);
        cardPackRepository.save(cd);
    }

    /**
     * Loads deck from NetrunnerDB
     * @param deckId deckID in NetrunnerDB
     * @return deck object
     */
    public Deck loadNetrunnerDbDeck(int deckId) {
        String url = netrunnerDBBroker.deckViewUrlFromId(deckId, true);
        Deck exists = deckRepository.findByUrl(url);
        if (exists != null) {
            logger.info("Deck is already in DB. Not saving!");
            return exists;
        } else {
            Deck deck = netrunnerDBBroker.readDeck(deckId);
            logger.info("Saving new deck! - " + deck.toString());
            deckRepository.save(deck);
            return deck;
        }
    }

    /**
     * Loads deck from Acoo
     * @param deckId deckID in Acoo
     * @return deck object
     */
    public Deck loadAcooDeck(int deckId) {
        String url = acooBroker.deckUrlFromId(deckId);
        Deck exists = deckRepository.findByUrl(url);
        if (exists != null) {
            logger.info("Deck is already in DB. Not saving!");
            return exists;
        } else {
            Deck deck = acooBroker.readDeck(deckId);
            logger.info("Saving new deck! - " + deck.toString());
            deckRepository.save(deck);
            return deck;
        }
    }

    /**
     * Loads deck from Stimhack
     * @param url deck url
     * @return deck object
     */
    public Set<Deck> loadStimhackDecks(String url) {
        Deck exists = deckRepository.findByUrl(url+"#1");
        if (exists != null) {
            logger.info("Decks are already in DB. Not saving!");
            return new HashSet<>();
        } else {
            Set<Deck> decks = stimhackBroker.readDeck(url);
            for (Deck deck : decks) {
                logger.info("Saving new deck! - " + deck.toString());
                deckRepository.save(deck);
            }
            return decks;
        }
    }

    /**
     * Loads tournament from Stimhack.
     * Saves tournament, standing for the top1 and decks in DB.
     * @param url tournament url
     * @return tournament object
     */
    public Tournament loadStimhackTournament(String url) {
        Tournament exists = tournamentRepository.findByUrl(url);
        if (exists != null) {
            logger.info("Tournament is already in DB. Not saving!");
            return exists;
        } else {
            logger.info("Parsing tournament at: " + url);
            // decks
            Set<Deck> decks = loadStimhackDecks(url);
            // tournament
            Tournament tournament = stimhackBroker.readTournament(url);
            // standings for top1
            for (Deck deck : decks) {
                Standing standing = new Standing(tournament, 1, deck.getIdentity(), true, deck.getIdentity().isRunner(), deck);
                logger.info("Saving standing! - " + standing.toString());
                standingRepository.save(standing);
            }
            logger.info("Saving new tournament! - " + tournament.toString());
            tournamentRepository.save(tournament);
            return tournament;
        }
    }

    /**
     * Loads tournament urls from stimhack.com specific to a cardpool legality.
     * Does not work on the "Older lists" part. (Works from "The source" card pack.)
     * @param cardpoolName name of card pack
     * @return list of URLs
     */
    public Set<Tournament> loadStimhackPackTournaments(String cardpoolName) {
        Set<Tournament> result = new HashSet<>();
        Set<String> urls = stimhackBroker.getTournamentURLs(cardpoolName);
        logger.info(String.format("%d stimhack tournaments found for cardpool: %s", urls.size(), cardpoolName));
        for (String url : urls) {
            result.add(loadStimhackTournament(url));
        }
        return result;
    }

    /**
     * Loads tournament data from Acoo. Saves in DB if not already present.
     * Does not save standing and deck information.
     * @param tournamentId tournamentID in Acoo
     * @return tournament object
     */
    public Tournament loadAcooTournament(int tournamentId) {
        String url = acooBroker.tournamentUrlFromId(tournamentId);
        Tournament exists = tournamentRepository.findByUrl(url);
        if (exists != null) {
            logger.info("Tournament is already in DB. Not saving!");
            return exists;
        } else {
            Tournament tournament = acooBroker.readTournament(tournamentId);
            logger.info("Saving new tournament! - " + tournament.toString());
            tournamentRepository.save(tournament);
            return tournament;
        }
    }

    /**
     * Loads decks from tournament from Acoo. Saves decks in DB if not already present.
     * Also saves Standings.
     * @param tournamentId tournamentID in ACoo
     */
    public void loadAcooTournamentDecks(int tournamentId) {
        String url = acooBroker.tournamentUrlFromId(tournamentId);

        // load tournament metadata
        Tournament tournament = loadAcooTournament(tournamentId);

        // load tournament standings with decks
        Set<Standing> standings = acooBroker.loadTournamentStandingsAndDecks(url, tournament);
        logger.debug("Total available standings: " + standings.size());
        // remove already existing standings
        Set<Standing> savestandings = new HashSet<>(standings);
        for (Standing standing : standings) {
            Standing exists = standingRepository.findByTournamentURLRankIdentity(tournament.getUrl(), standing.getRank(), standing.getIdentity().getTitle());
            if (exists != null) {
                savestandings.remove(standing);
                // if deck is added to an existing standing
                if ((exists.getDeck() == null) && (standing.getDeck() != null)) {
                    logger.trace("Updating standing: " + standing.toString());
                    standingRepository.save(standing);
                }
            } else {
                logger.trace("Saving standing: " + standing.toString());
                standingRepository.save(standing);
            }

        }
        logger.info(String.format("Saving new standings: %d", savestandings.size()));

//        tournamentRepository.save(tournament);
    }

    /**
     * Loads and saves tournament and deck data from Acco.
     * @param url Acoo tournament list URL to start from
     * @param paging use paging to access all preceeding tournament lists
     * @param filterempty do not save tournaments with no decks
     */
    public void loadAcooTournamentsFromUrl(String url, boolean paging, boolean filterempty) {
        logger.info("*** Reading tournaments on page: " + url);
        AdminData denyUrls = adminDataRepository.getDenyUrls();
        Set<Integer> tournamentIds = acooBroker.loadTournamentIdsFromUrl(url, filterempty);
        for (int tournamentId : tournamentIds) {
            if ((denyUrls == null) || (!denyUrls.getData().contains(acooBroker.tournamentUrlFromId(tournamentId))))  {
                loadAcooTournamentDecks(tournamentId);
            }
        }
        String pagination = acooBroker.getTournamentPageNextLink(url);
        if ((paging) && (!pagination.equals(""))) {
            loadAcooTournamentsFromUrl(acooBroker.ACOO_URL + pagination, true, filterempty);
        }
    }

    /**
     * Checks DB and solves errors.
     * Checks decks validity. Check tournament data. Calculate tournament cardpool validity if not defined.
     */
    public List<VerificationProblem> checkDataValidity() {
//        logDBCount();
        List<VerificationProblem> list = new ArrayList<>();
        logger.info("Checking data validity");

        // check decks
        List<Deck> decks = deckRepository.getAllDecks();
        logger.info("Checking deck validity");
        for (Deck deck : decks) {
            logger.debug(String.format("Checking validity: %s", deck.toString()));
            String problem = deckValidator.isValidDeck(deck);
            if (problem.equals("")) {
                logger.debug("OK");
            } else {
                list.add(new VerificationProblem(deck.getName(), deck.getUrl(), "validity", problem));
            }
        }

        // check standings
        logger.info("Checking IDs");
        List<Standing> standings = standingRepository.getAllStanding();
        for (Standing standing : standings) {
            if (standing.getIdentity().getTitle().equals("The Shadow: Pulling the Strings")) {
                Tournament tournament = standing.getTournament();
                if (tournament != null) {
                    logger.warn(String.format("Unknown identity during tournament import (#%d): %s",
                            standing.getRank(), tournament.getUrl()));
                    list.add(new VerificationProblem(tournament.getName(), tournament.getUrl(),
                            "wrong ID", "rank: " + standing.getRank()));
                }
            }
        }

        // check tournaments
        List<Tournament> tournaments = tournamentRepository.getAllTournaments();
        List<Tournament> tournamentsABR = new ArrayList<>();
        Date nulldate = new Date(0);
        logger.info("Checking tournament cardpool");
        for (Tournament tournament : tournaments) {
            // check wrong date
//            if (tournament.getDate().equals(nulldate)) {
//                logger.warn(String.format("ERROR - Wrong date: %s", tournament.toString()));
//                list.add(new VerificationProblem(tournament.getName(), tournament.getUrl(),
//                        "wrong date", ""));
//            }
            // check wrong cardpool
            String oldname = tournament.getCardpool().getName();
            CardPack fix = guessCardPool(tournament);
            if ((!oldname.equals(fix.getName())) && (fix.later(tournament.getCardpool()))) {
                list.add(new VerificationProblem(tournament.getName(), tournament.getUrl(),
                        "wrong t. cardpool", "should be: " + fix.getName()));
                logger.warn(String.format("ERROR - Wrong cardpool: %s - new cardpool: %s", tournament.toString(), fix.getName()));
                List<Deck> tDecks = deckRepository.findByTournamentUrl(tournament.getUrl());
                for (Deck aDeck : tDecks) {
                    if (aDeck.getUpto().later(tournament.getCardpool())) {
                        logger.warn("Wrong deck: " + aDeck.getUrl());
                        list.add(new VerificationProblem(aDeck.getName(), aDeck.getUrl(), "wrong deck",
                                "updated to:" + aDeck.getUpto().toString() + " from " + tournament.getCardpool().toString()));
                    }
                }
//                tournament.setCardpool(fix);
//                tournamentRepository.save(tournament);
            }
            if (tournament.getUrl().contains("alwaysberunning")) {
                tournamentsABR.add(tournament);
            }
        }

        // checking for duplicated decks, stimhack-acoo
//        List<Deck> decksStimhack = new ArrayList<>();
//        List<Deck> decksAcoo = new ArrayList<>();
//        for (Deck deck : decks) {
//            if (deck.getUrl().contains("stimhack")) {
//                decksStimhack.add(deck);   // stimhack
//            } else {
//                Standing standing = standingRepository.findByDeckUrl(deck.getUrl());
//                if ((deck.getUrl().contains("acoo")) && (standing != null) && (standing.getRank() == 1)) {
//                    decksAcoo.add(deck);   // acoo
//                }
//            }
//        }
//        for (Deck deck3 : decksAcoo) {
//            for (Deck deck2 : decksStimhack) {
//                if (deck3.equals(deck2)) {
//                    Tournament tournament3 = tournamentRepository.getTournamentByDeckUrl(deck3.getUrl());
//                    Tournament tournament2 = tournamentRepository.getTournamentByDeckUrl(deck2.getUrl());
//                    if (tournament3.getCardpool().getName().equals(tournament2.getCardpool().getName())) {
//                        logger.warn("WARNING - matching decks:");
//                        logger.warn(deck3.toString());
//                        logger.warn(deck2.toString());
//                        list.add(new VerificationProblem(deck3.getName(), deck3.getUrl(), "duplicate",
//                                String.format("(%d) %s", tournament3.getPlayerNumber(), tournament3.getName())));
//                        list.add(new VerificationProblem(deck2.getName(), deck2.getUrl(), "duplicate",
//                                String.format("(%d) %s", tournament2.getPlayerNumber(), tournament2.getName())));
//                    }
//                }
//            }
//        }

        // check for ABR duplication
//        for (int i = 0; i < decksABR.size() - 1; i++) {
//            for (int u = i + 1; u < decksABR.size(); u++) {
//                if (decksABR.get(i).getUrl().equals(decksABR.get(u).getUrl())) {
//                    list.add(new VerificationProblem(decksABR.get(u).getName(), decksABR.get(u).getUrl(), "duplicate deck", ""));
//                    logger.warn(String.format("ERROR - duplicate ABR deck: %s", decksABR.get(u).getUrl()));
//                }
//            }
//        }

        logger.info("Checking ABR duplication");
        for (int i = 0; i < tournamentsABR.size() - 1; i++) {
            for (int u = i + 1; u < tournamentsABR.size(); u++) {
                if (tournamentsABR.get(i).getUrl().equals(tournamentsABR.get(u).getUrl())) {
                    list.add(new VerificationProblem(tournamentsABR.get(u).getName(), tournamentsABR.get(u).getUrl(), "duplicate  tournament", ""));
                    logger.warn(String.format("ERROR - duplicate ABR tournament: %s", tournamentsABR.get(u).getUrl()));
                }
            }
            List<Standing> standingsABT = standingRepository.findByTournamentURL(tournamentsABR.get(i).getUrl());
            for (int u = 0; u < standingsABT.size() - 1; u++) {
                for (int e = u + 1; e < standingsABT.size(); e++) {
                    if ((standingsABT.get(u).getDeck() != null) && (standingsABT.get(e).getDeck() != null) &&
                            (standingsABT.get(u).getDeck().getUrl().equals(standingsABT.get(e).getDeck().getUrl()))) {
                        list.add(new VerificationProblem(standingsABT.get(e).getDeck().getName(), tournamentsABR.get(i).getUrl(), "duplicate  deck", ""));
                        logger.warn(String.format("ERROR - duplicate ABR deck: %s", standingsABT.get(e).getDeck().getName()));
                    }
                }
            }
        }

        return list;
    }

    /**
     * Deletes all statistical data from DB.
     */
    public void resetStats() {
        logDBStatCount();
        logger.info("Deleting calculated Statistics from database.");
        Map<String, Object> emptyparams = new HashMap<>();
        template.query("MATCH (n:DPStatistics) OPTIONAL MATCH (n)-[r]-(c:CountDeckStands) DELETE n,r,c", emptyparams);
        template.query("MATCH (n:CardPoolStats) OPTIONAL MATCH (n)-[r]-(c:CardPool) DELETE n,r,c", emptyparams);
        template.query("MATCH (c:CardAverage) OPTIONAL MATCH (c)-[r]-() DELETE c,r", emptyparams);
        template.query("MATCH (n:IdentityAverage) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
        template.query("MATCH (n:IdentityAverage) OPTIONAL MATCH (n)-[r]-(c:CardAverage) DELETE n,r,c", emptyparams);
        template.query("MATCH (t:TournamentDrilldown) OPTIONAL MATCH (t)-[r]-() " +
                "DELETE t,r", emptyparams);
        template.query("MATCH (n:CardUsageStat) OPTIONAL MATCH (n)-[r]-(c:CardUsage) DELETE n,r,c", emptyparams);
        template.query("MATCH (c:CardCombo) OPTIONAL MATCH (c)-[r]-() DELETE c,r", emptyparams);
        template.query("MATCH (c:DPDecks) OPTIONAL MATCH (c)-[r]-() DELETE c,r", emptyparams);
        template.query("MATCH (c:CardUsage) OPTIONAL MATCH (c)-[r]-() DELETE c,r", emptyparams);
        template.query("MATCH (c:CardStat) OPTIONAL MATCH (c)-[r]-() DELETE c,r", emptyparams);
        template.query("MATCH (c:StandingDeckCount) OPTIONAL MATCH (c)-[r]-() DELETE c,r", emptyparams);
        template.query("MATCH (c:StandingDeckCountID) OPTIONAL MATCH (c)-[r]-() DELETE c,r", emptyparams);
        template.query("MATCH (c:MDSEntry) OPTIONAL MATCH (c)-[r]-() DELETE c,r", emptyparams);
        template.query("MATCH (m:MatchCount) OPTIONAL MATCH (m)-[r]-() DELETE m,r", emptyparams);
        // troublemaker nodes
        template.query("MATCH (n:CountDeckStands) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
        template.query("MATCH (n:CardUsage) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
        logDBStatCount();
    }

    /**
     * Deletes all statistical data from DB for DP.
     */
    public void resetStatsDP(String DPName) {
        // TODO: CardUsage and DPDecks grows in numbers
        logDBStatCount();

        Map<String, Object> emptyparams = new HashMap<>();
        if (lastThree.isInLastThree(DPName)) {
            template.query("MATCH (c:CardStat)-[r:TOP]-(u:CardUsage) DELETE u,r", emptyparams);
            template.query("MATCH (c:CardStat)-[r]-(u:CardCombo) DELETE u,r", emptyparams);
            resetStatsDP(LAST_3);
        }

        logger.info("Deleting calculated Statistics from database: " + DPName);
        template.query("MATCH (n:DPStatistics {packTitle: '" + DPName + "'})-[r]-(c:CountDeckStands) DELETE c,r", emptyparams);
        template.query("MATCH (n:DPStatistics) WHERE n.packTitle = '" + DPName + "' DELETE n", emptyparams);

        template.query("MATCH (n:IdentityAverage {cardpool: '" + DPName + "'})-[r]-(c:CardAverage) DELETE c,r", emptyparams);
        template.query("MATCH (n:IdentityAverage {cardpool: '" + DPName + "'})-[r]-(c:MDSEntry) DELETE c,r", emptyparams);
        template.query("MATCH (n:IdentityAverage {cardpool: '" + DPName + "'}) DELETE n", emptyparams);

        template.query("MATCH (n:CardPoolStats)-[r]-(c:CardPool {title: '" + DPName + "'}) DELETE c,r", emptyparams);
        template.query("MATCH (n:CardPoolStats) OPTIONAL MATCH (n)-[r]-(c:CardPool) DELETE n,r,c", emptyparams);

        template.query("MATCH (n:CardUsageStat {cardpackname: '" + DPName + "'})-[r]-(c:CardUsage) DELETE c,r", emptyparams);
        template.query("MATCH (n:CardUsageStat {cardpackname: '" + DPName + "'}) DELETE n", emptyparams);

        template.query("MATCH (t:TournamentDrilldown {packTitle: '" + DPName + "'})-[r]-(c:CardAverage) DELETE c,r", emptyparams);
        template.query("MATCH (t:TournamentDrilldown {packTitle: '" + DPName + "'})-[r]-(c:StandingDeckCount) DELETE c,r", emptyparams);
        template.query("MATCH (t:TournamentDrilldown {packTitle: '" + DPName + "'})-[r]-(c:StandingDeckCountID) DELETE c,r", emptyparams);
        template.query("MATCH (t:TournamentDrilldown {packTitle: '" + DPName + "'})-[r]-(c:StandingDeckCountID) DELETE c,r", emptyparams);
        template.query("MATCH (t:TournamentDrilldown {packTitle: '" + DPName + "'})-[r]-(c:CardUsage) DELETE c,r", emptyparams);
        template.query("MATCH (t:TournamentDrilldown {packTitle: '" + DPName + "'}) DELETE t", emptyparams);

        template.query("MATCH (c:CardStat)-[r:OVERTIME]-(u:CardUsage {cardpacktitle: '" + DPName + "'}) DELETE u,r", emptyparams);
        template.query("MATCH (c:CardStat)-[r]-(u:DPDecks {dptitle: '" + DPName + "'}) DELETE u,r", emptyparams);

        logDBStatCount();
    }

    /**
     * Deletes deck with URL, deletes standing as well
     * @param url deck URL
     */
    public void deleteDeck(String url) {
        Map<String, Object> emptyparams = new HashMap<>();
//        template.query(String.format("MATCH (d:Deck {url: \"%s\"})<-[r1:IS_DECK]-(s:Standing)-[r2]-() " +
//                "DELETE d,r1,s,r2", url), emptyparams);
//        template.query(String.format("MATCH (d:Deck {url: \"%s\"})<-[r1:IS_DECK]-(s:Standing) " +
//                "DELETE d,r1,s", url), emptyparams);
//        template.query(String.format("MATCH (d:Deck {url: \"%s\"}) OPTIONAL MATCH (d)-[r]-()" +
//                "DELETE d,r", url), emptyparams);
        template.query(String.format("MATCH (d:Deck {url: \"%s\"})<-[:IS_DECK]-(s:Standing) " +
                "OPTIONAL MATCH (d)-[r]-(), (s)-[r2]-() DELETE d,r,s,r2", url), emptyparams);
    }

    /**
     * Deletes tournament and associated decks and standings with tournament URL
     * @param url tounament URL
     */
    public void deleteTournament(String url) {
        Map<String, Object> emptyparams = new HashMap<>();
        template.query(String.format(
                "MATCH (t:Tournament {url: \"%s\"})<-[:IN_TOURNAMENT]-(m:Match) " +
                        "OPTIONAL MATCH (m)-[r2]-() DELETE m,r2", url), emptyparams);
        template.query(String.format(
                "MATCH (t:Tournament {url: \"%s\"})<-[:IN_TOURNAMENT]-(s:Standing)-[:IS_DECK]->(d:Deck) " +
                        "OPTIONAL MATCH (d)-[r3]-() DELETE d,r3", url), emptyparams);
        template.query(String.format(
                "MATCH (t:Tournament {url: \"%s\"})<-[:IN_TOURNAMENT]-(s:Standing) " +
                        "OPTIONAL MATCH (t)-[r1]-(), (s)-[r2]-() DELETE t,s,r1,r2", url), emptyparams);
        template.query(String.format(
                "MATCH (t:Tournament {url: \"%s\"}) " +
                        "OPTIONAL MATCH (t)-[r1]-() DELETE t,r1", url), emptyparams);
    }

    /**
     * Tries to guess tournament cardpool based on cards used.
     * @return latest card pack used in tournament
     */
    private CardPack guessCardPool(Tournament tournament) {
        CardPack result = new CardPack("dummy", "dummy", 0, 0);
        List<Standing> standings = standingRepository.findByTournamentURL(tournament.getUrl());
        for (Standing standing : standings) {
            if (standing.getDeck() != null) {
                if (standing.getDeck().getUpto().later(result)) {
                    result = standing.getDeck().getUpto();
                }
            } else {
                if (standing.getIdentity().getCardPack().later(result)) {
                    result = standing.getIdentity().getCardPack();
                }
            }
        }
        return result;
    }

    public void detectPostMWL(boolean reorg) {
        logger.info("Detecting post-MWL tournaments.");
        Date nulldate = new Date(0);
        Date date2016 = new Date(0);
        Date dateMWL = new Date(0);
        try {
            date2016 = df.parse("2015.12.31.");
            dateMWL = df.parse("2016.02.01.");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Tournament> tournaments = tournamentRepository.getTournamentsByCardpool("Data and Destiny");
        CardPack cardPack = cardPackRepository.findByName("D&D, post-MWL");
        for (Tournament tournament : tournaments) {
            List<Deck> decks = deckRepository.findByTournamentUrl(tournament.getUrl());
            if (decks.size() > 0) {
//                logger.info("Tournament - " + tournament.getUrl());
                boolean MWLOK = true;
                for (Deck deck : decks) {
                    if (!deckValidator.isInfluenceOK(deck, true)) {
                        MWLOK = false;
                        break;
                    }
                }
                if ( (MWLOK) && ((tournament.getDate().equals(nulldate)) || (tournament.getDate().after(date2016))) ) {
                    logger.info(String.format("MWL OK tournament (%d decks): %s", decks.size(), tournament.getUrl()));
                    if (reorg) {
                        tournament.setCardpool(cardPack);
                        tournamentRepository.save(tournament);
                    }
                }
            } else {
                // later than MWL deadline, just standings
                if (tournament.getDate().after(dateMWL)) {
                    logger.info(String.format("MWL-date tournament: %s", tournament.getUrl()));
                    if (reorg) {
                        tournament.setCardpool(cardPack);
                        tournamentRepository.save(tournament);
                    }
                }
            }
        }
    }

    public void updateLastUpdateDate() {
        AdminData update = new AdminData("lastUpdate", df2.format(new Date()));
        adminDataRepository.save(update);
    }

    public void loadABRTournamentsForPack(String packcode) {
        AdminData denyUrls = adminDataRepository.getDenyUrls();
        // get tournaments
        Set<Tournament> tournaments = abrBroker.getTournamentIDsForPack(packcode);

        int index = 1;
        for (Tournament tournament : tournaments) {
            if ((denyUrls == null) || (!denyUrls.getData().contains(tournament.getUrl()))) {
                Tournament tExists = tournamentRepository.findByUrl(tournament.getUrl());

                // saving new tournament
                if (tExists == null) {
                    logger.info("New tournament (" + tournament.getCardpool().getCode() + "), saving: " + tournament.getName());
                    tournamentRepository.save(tournament);
                } else {
                    logger.info("Tournament is already in DB. Not saving! - " + tExists.getName());
                }

                // get standings with decks
                Set<Standing> standings = abrBroker.getStandings(tournament);
                int savedStandings = 0, savedDecks = 0;
                for (Standing standing : standings) {
                    Standing sExists = standingRepository.findByTournamentURLRankIdentity(tournament.getUrl(),
                            standing.getRank(), standing.getIdentity().toString());

                    if (sExists == null) {
                        // save new deck
                        if (standing.getDeck() != null) {
                            deckRepository.save(standing.getDeck());
                            logger.info("Saving new deck: " + standing.getDeck().toString());
                            savedDecks++;
                        }
                        // save new standing
                        standingRepository.save(standing);
                        savedStandings++;
                    } else {
                        // adding deck to existing standing without deck
                        if ((sExists.getDeck() == null) && (standing.getDeck() != null)) {
                            deckRepository.save(standing.getDeck());
                            logger.info("Saving new deck: " + standing.getDeck().toString());
                            savedDecks++;
                            sExists.setDeck(standing.getDeck());
                            standingRepository.save(sExists);
                        }
                    }
                }

                // get NRTM matches
                loadMatchesForABRTournament(tournament.getId() - 100000);

                logger.info("New standings: " + savedStandings + " | new decks: " + savedDecks +
                        " | progress: " + index + " / " + tournaments.size());
            } else {
                logger.info("Tournament skipped: " + tournament.getName() +
                        " | progress: " + index + " / " + tournaments.size());
            }
            index++;
        }
        logger.info("Import finished for: " + packcode);
    }

    public void migrateStandingsAfterRotation() {
        logger.info("Migrating decks to post rotation");
        List<Standing> standings = standingRepository.getAllStanding();
        int i = 1;
        for (Standing standing : standings) {
            Deck deck = standing.getDeck();

            if (deck != null) {
                // migrate decks
                if (!deckRepository.exists(deck.getId())) {
                    logger.error("This is very weird. This deck does not exists: " + deck.getName());
                }
                if (deck.hasOldCard()) {
                    logger.info(i + "/" + standings.size() + " Migrating: " + deck.getName() + " - "
                            + standing.getTournament().getCardpool().getName());
                    netrunnerDBBroker.updateDeckWithCore2(deck);
                    deckRepository.save(deck);
                } else {
                    logger.info(i + "/" + standings.size() + " Nothing to migrate: " + deck.getName() + " - "
                            + standing.getTournament().getCardpool().getName());
                }
            } else {
                // migrate just standings
                if (standing.getIdentity().getTitle().contains(" (old)")) {
                    logger.info(i + "/" + standings.size() + " Migrating ID");
                    standing.setIdentity(netrunnerDBBroker.updateCardWithCore2(standing.getIdentity()));
                    standingRepository.save(standing);
                }
            }
            i++;
        }
    }

    public void loadMatchesForABRTournament(int tournamentID) {

        List<Match> matches = matchRepository.findForTournamentURL(abrBroker.URL_TOURNAMENT + tournamentID);

        if (matches.size() == 0) {
            matches = abrBroker.getMatchesForTournament(tournamentID);
            if (matches.size() > 0) {
                logger.info("Saving new matches: " + matches.size());
                matchRepository.save(matches);
            } else {
                logger.warn("No matches were available: " + matches.size() + " matches");
            }
        } else {
            logger.info("Matches were already saved");
        }
    }
}