package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.database.DOs.*;
import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.database.DOs.stats.*;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.*;
import com.madarasz.netrunnerstats.database.DRs.*;
import com.madarasz.netrunnerstats.brokers.AcooBroker;
import com.madarasz.netrunnerstats.brokers.NetrunnerDBBroker;
import com.madarasz.netrunnerstats.brokers.StimhackBroker;
import com.madarasz.netrunnerstats.helper.DeckValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Handling all database loading, manupilating operations of the application.
 * Created by istvan on 2015-06-11.
 */
@Component
public class Operations {

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
    DeckValidator deckValidator;

    @Autowired
    NetrunnerDBBroker netrunnerDBBroker;

    @Autowired
    AcooBroker acooBroker;

    @Autowired
    StimhackBroker stimhackBroker;

    @Autowired
    Neo4jOperations template;

    /**
     * Wipes DB clean
     */
    public void cleanDB() {
        System.out.println("Cleaning DB.");
        Map<String, Object> emptyparams = new HashMap<String, Object>();
        template.query("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
        // troublemaker nodes
        template.query("MATCH ()-[h:HAS_CARD]->() DELETE h", emptyparams);
        template.query("MATCH (n:CountDeckStands) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
        logDBCount();
    }

    /**
     * Logs DB node, relationship count
     */
    public void logDBCount() {
        System.out.println(String.format("Cards: %d, CardPacks: %d, Decks: %d, Standings: %d, Tournaments: %d, Deck-card relations: %d",
                template.count(Card.class), template.count(CardPack.class), template.count(Deck.class), template.count(Standing.class), template.count(Tournament.class),
                template.count(DeckHasCard.class)));
    }

    /**
     * Logs DB Stats node, relationship count
     */
    public void logDBStatCount() {
        System.out.println(String.format("CardPoolStats %d, CardPool: %d, DP statistics: %d, CountDeckStands: %d, " +
                "IdentityMDS: %d, MDSEntry: %d, DeckInfos: %d, DeckInfo: %d, DP Identities: %d, DP Identity: %d, \n" +
                "CardUsageStat: %d, CardUsage: %d, IdentityAverage: %d, CardAverage: %d",
                template.count(CardPoolStats.class), template.count(CardPool.class),
                template.count(DPStatistics.class), template.count(CountDeckStands.class),
                template.count(IdentityMDS.class), template.count(MDSEntry.class),
                template.count(DeckInfos.class), template.count(DeckInfo.class),
                template.count(DPIdentities.class), template.count(DPIdentity.class),
                template.count(CardUsageStat.class), template.count(CardUsage.class),
                template.count(IdentityAverage.class), template.count(CardAverage.class)));
    }

    /**
     * Loads card pack and card information to DB from NetrunnerDB
     */
    public void loadNetrunnerDB() {
        // load and save card packs
        Set<CardPack> allCardPacks = netrunnerDBBroker.readSets();
        int found = 0;
        for (CardPack cardPack : allCardPacks) {
            if (cardPackRepository.findByCode(cardPack.getCode()) == null) {
                cardPackRepository.save(cardPack);
//                System.out.println("Found pack: " + cardPack.toString());
                found++;
            }
        }
        System.out.println("Found new card packs: " + found);

        // load and save cards
        Set<Card> allCards = netrunnerDBBroker.readCards();
        found = 0;
        for (Card card : allCards) {
            if (cardRepository.findByTitle(card.getTitle()) == null) {
                cardRepository.save(card);
//                System.out.println("Found card: " + card.toString());
                found++;
            }
        }
        System.out.println("Found new cards: " + found);
    }

    /**
     * Loads deck from NetrunnerDB
     * @param deckId deckID in NetrunnerDB
     * @return deck object
     */
    public Deck loadNetrunnerDbDeck(int deckId) {
        String url = netrunnerDBBroker.deckUrlFromId(deckId);
        Deck exists = deckRepository.findByUrl(url);
        if (exists != null) {
            System.out.println("Deck is already in DB. Not saving!");
            return exists;
        } else {
            Deck deck = netrunnerDBBroker.readDeck(deckId);
            System.out.println("Saving new deck! - " + deck.toString());
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
            System.out.println("Deck is already in DB. Not saving!");
            return exists;
        } else {
            Deck deck = acooBroker.readDeck(deckId);
            System.out.println("Saving new deck! - " + deck.toString());
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
            System.out.println("Decks are already in DB. Not saving!");
            return new HashSet<Deck>();
        } else {
            Set<Deck> decks = stimhackBroker.readDeck(url);
            for (Deck deck : decks) {
                System.out.println("Saving new deck! - " + deck.toString());
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
            System.out.println("Tournament is already in DB. Not saving!");
            return exists;
        } else {
            System.out.println("Parsing tournament at: " + url);
            // decks
            Set<Deck> decks = loadStimhackDecks(url);
            // tournament
            Tournament tournament = stimhackBroker.readTournament(url);
            // standings for top1
            for (Deck deck : decks) {
                Standing standing = new Standing(tournament, 1, deck.getIdentity(), true, deck.getIdentity().isRunner(), deck);
                System.out.println("Saving standing! - " + standing.toString());
                standingRepository.save(standing);
            }
            System.out.println("Saving new tournament! - " + tournament.toString());
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
        Set<Tournament> result = new HashSet<Tournament>();
        Set<String> urls = stimhackBroker.getTournamentURLs(cardpoolName);
        System.out.println(String.format("%d stimhack tournaments found for cardpool: %s", urls.size(), cardpoolName));
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
    // TODO: Tournament is not refreshed if already exists
    public Tournament loadAcooTournament(int tournamentId) {
        String url = acooBroker.tournamentUrlFromId(tournamentId);
        Tournament exists = tournamentRepository.findByUrl(url);
        if (exists != null) {
            System.out.println("Tournament is already in DB. Not saving!");
            return exists;
        } else {
            Tournament tournament = acooBroker.readTournament(tournamentId);
            System.out.println("Saving new tournament! - " + tournament.toString());
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
//        System.out.println("Total available standings: " + standings.size());
        // remove already existing standings
        Set<Standing> savestandings = new HashSet<Standing>(standings);
        for (Standing standing : standings) {
            Standing exists = standingRepository.findByTournamentURLRankIdentity(tournament.getUrl(), standing.getRank(), standing.getIdentity().getTitle());
            if (exists != null) {
                savestandings.remove(standing);
            } else {
//                System.out.println("Saving stanging: " + standing.toString());
                standingRepository.save(standing);
            }

        }
        System.out.println(String.format("Saving new standings: %d", savestandings.size()));

        tournamentRepository.save(tournament);
    }

    /**
     * Loads and saves tournament and deck data from Acco.
     * @param url Acoo tournament list URL to start from
     * @param paging use paging to access all preceeding tournament lists
     * @param filterempty do not save tournaments with no decks
     */
    public void loadAcooTournamentsFromUrl(String url, boolean paging, boolean filterempty) {
        System.out.println("*** Reading tournaments on page: " + url);
        Set<Integer> tournamentIds = acooBroker.loadTournamentIdsFromUrl(url, filterempty);
        for (int tournamentId : tournamentIds) {
            loadAcooTournamentDecks(tournamentId);
        }
        String pagination = acooBroker.getTournamentPageNextLink(url);
        if ((paging) && (!pagination.equals(""))) {
            loadAcooTournamentsFromUrl(acooBroker.ACOO_URL + pagination, true, filterempty);
        }
    }

    // TODO: not to be used
    public void generateArchetype(String name, String cardpool, String identity, boolean filtertop) {
        List<Deck> deckList;
        if (filtertop) {
            deckList = deckRepository.filterTopByIdentityAndCardPool(identity, cardpool);
        } else {
            deckList = deckRepository.filterByIdentityAndCardPool(identity, cardpool);
        }
        Archetype archetype = new Archetype(name, deckList, cardRepository.findByTitle(identity));
        System.out.println(archetype.toString());
    }

    /**
     * Checks DB and solves errors.
     * Checks decks validity. Check tournament data. Calculate tournament cardpool validity if not defined.
     */
    public void checkDataValidity() {
        logDBCount();
        System.out.println("Checking data validity");

        // check decks
        List<Deck> decks = deckRepository.getAllDecks();
        for (Deck deck : decks) {
//            System.out.println(String.format("Checking validity: %s", deck.toString()));
            if (deckValidator.isValidDeck(deck)) {
//                System.out.println("OK");
            }
        }

        // check standings
        List<Standing> standings = standingRepository.getAllStanding();
        for (Standing standing : standings) {
            if (standing.getIdentity().getTitle().equals("The Shadow: Pulling the Strings")) {
                System.out.println(String.format("Unknown identity during tournament import (#%d): %s",
                        standing.getRank(), standing.getTournament().getUrl()));
            }
        }

        // check tournaments
        List<Tournament> tournaments = tournamentRepository.getAllTournaments();
        Date nulldate = new Date(0);
        for (Tournament tournament : tournaments) {
            // check wrong date
            if (tournament.getDate().equals(nulldate)) {
                System.out.println(String.format("ERROR - Wrong date: %s", tournament.toString()));
            }
            // check wrong cardpool
            String oldname = tournament.getCardpool().getName();
            CardPack fix = guessCardPool(tournament);
            if ((!oldname.equals(fix.getName())) && (fix.later(tournament.getCardpool()))) {
                System.out.println(String.format("ERROR - Wrong cardpool: %s - new cardpool: %s", tournament.toString(), fix.getName()));
                tournament.setCardpool(fix);
                tournamentRepository.save(tournament);
            }
        }

        // checking for duplicated decks, stimhack-acoo
        List<Deck> decks2 = new ArrayList<>();
        List<Deck> decks3 = new ArrayList<>();
        for (Deck deck : decks) {
            if (deck.getUrl().contains("stimhack")) {
                decks2.add(deck);   // stimhack
            } else {
                decks3.add(deck);   // acoo
            }
        }
        for (Deck deck3 : decks3) {
            for (Deck deck2 : decks2) {
                if (deck3.equals(deck2)) {
                    System.out.println("WARNING - matching decks:");
                    System.out.println(deck3.toString());
                    System.out.println(deck2.toString());
                }
            }
        }
    }

    /**
     * Deletes all statistical data from DB.
     */
    public void resetStats() {
        logDBStatCount();
        System.out.println("Deleting calculated Statistics from database.");
        Map<String, Object> emptyparams = new HashMap<String, Object>();
        template.query("MATCH (n:DPStatistics) OPTIONAL MATCH (n)-[r]-(c:CountDeckStands) DELETE n,r,c", emptyparams);
        template.query("MATCH (n:IdentityMDS) OPTIONAL MATCH (n)-[r]-(c:MDSEntry) DELETE n,r,c", emptyparams);
        template.query("MATCH (n:CardPoolStats) OPTIONAL MATCH (n)-[r]-(c:CardPool) DELETE n,r,c", emptyparams);
        template.query("MATCH (n:DeckInfos) OPTIONAL MATCH (n)-[r]-(c:DeckInfo) DELETE n,r,c", emptyparams);
        template.query("MATCH (n:DPIdentities) OPTIONAL MATCH (n)-[r]-(c:DPIdentity) DELETE n,r,c", emptyparams);
        template.query("MATCH (n:CardUsageStat) OPTIONAL MATCH (n)-[r]-(c:CardUsage) DELETE n,r,c", emptyparams);
        template.query("MATCH (n:IdentityAverage) OPTIONAL MATCH (n)-[r]-(c:CardAverage) DELETE n,r,c", emptyparams);
        // troublemaker nodes
        template.query("MATCH (n:CountDeckStands) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
        template.query("MATCH (n:CardUsage) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
        logDBStatCount();
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
}