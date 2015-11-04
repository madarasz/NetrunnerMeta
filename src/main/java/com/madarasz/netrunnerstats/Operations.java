package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.DOs.*;
import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.DOs.result.StatCounts;
import com.madarasz.netrunnerstats.DRs.*;
import com.madarasz.netrunnerstats.brokers.AcooBroker;
import com.madarasz.netrunnerstats.brokers.NetrunnerDBBroker;
import com.madarasz.netrunnerstats.brokers.StimhackBroker;
import com.madarasz.netrunnerstats.helper.MultiDimensionalScaling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Handling all the possible operations of the application.
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
    NetrunnerDBBroker netrunnerDBBroker;

    @Autowired
    AcooBroker acooBroker;

    @Autowired
    StimhackBroker stimhackBroker;

    @Autowired
    Neo4jOperations template;

    @Autowired
    MultiDimensionalScaling multiDimensionalScaling;

    /**
     * Wipes DB clean
     */
    public void cleanDB() {
        System.out.println("Cleaning DB.");
        Map<String, Object> emptyparams = new HashMap<String, Object>();
        template.query("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
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
            // decks
            Set<Deck> decks = loadStimhackDecks(url);
            // tournament
            Tournament tournament = stimhackBroker.readTournament(url);
            System.out.println("Saving new tournament! - " + tournament.toString());
            // standings for top1
            for (Deck deck : decks) {
                Standing standing = new Standing(tournament, 1, deck.getIdentity(), true, deck.getIdentity().isRunner(), deck);
                System.out.println("Saving standing! - " + standing.toString());
                standingRepository.save(standing);
            }
            tournamentRepository.save(tournament);
            return tournament;
        }
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
            Standing exists = standingRepository.findByTournamentURLIdentity(tournament.getUrl(), standing.getRank(), standing.getIdentity().getTitle());
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
        // check decks
        List<Deck> decks = deckRepository.getAllDecks();
        for (Deck deck : decks) {
            System.out.println(String.format("Checking validity: %s", deck.toString()));
            if (deck.isValidDeck()) {
                System.out.println("OK");
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
            CardPack fix = tournament.guessCardPool();
            if ((!oldname.equals(fix.getName())) && (fix.later(tournament.getCardpool()))) {
                System.out.println(String.format("ERROR - Wrong cardpool: %s - new cardpool: %s", tournament.toString(), fix.getName()));
                tournament.setCardpool(fix);
                tournamentRepository.save(tournament);
            }
        }

        // TODO: check for same decks
    }

    /**
     * Log statistics about deck count for each identity for a cardpool legality
     * @param cardpack name of last legal card pack
     */
    public void getPackStats(String cardpack) {
        List<Card> identities = cardRepository.findIdentities();
        for (Card card : identities) {
            String identityTitle = card.getTitle();
            int count = deckRepository.countByCardPackAndIdentity(cardpack, identityTitle);
            if (count > 0) {
                int topcount = deckRepository.countTopByCardPackAndIdentity(cardpack, identityTitle);
                System.out.println(String.format("*** %s: %d (top: %d)", identityTitle, count, topcount));
            }
        }
    }

    /**
     * Generate multidimensional scaling for identity and cardpool legality
     * @param identityName filter for identity
     * @param cardpackName filter for cardpool
     * @param top filter for top decks
     */
    public void getPackMath(String identityName, String cardpackName, boolean top) {
        ArrayList<Deck> decks;
        if (top) {
            decks = new ArrayList<Deck>(deckRepository.filterTopByIdentityAndCardPool(identityName, cardpackName));
        } else {
            decks = new ArrayList<Deck>(deckRepository.filterByIdentityAndCardPool(identityName, cardpackName));
        }
        System.out.println("*** Getting stats for NEH: " + decks.size());
        double[][] distance = multiDimensionalScaling.getDistanceMatrix(decks);
        double[][] scaling = multiDimensionalScaling.calculateMDS(distance);
        for (int i = 0; i < decks.size(); i++) {
            System.out.println(String.format("\"%s\",\"%f\",\"%f\"", decks.get(i).getUrl(), scaling[0][i], scaling[1][i]));
        }
    }

    /**
     * Log statistics about deck count for each identitz for all cardpool legality
     */
    public void getAllStats(String cardpackName) {
        System.out.println("*********************************************************************");
        System.out.println(String.format("Stats for cardpool: %s", cardpackName));
        System.out.println("*********************************************************************");
        List<StatCounts> stats = standingRepository.getTopIdentityStatsByCardPool(cardpackName);
        for (StatCounts stat : stats) {
            String identity = stat.getCategory();
            System.out.println(String.format("%s - %d (%d)", identity, stat.getCount(), deckRepository.countTopByCardPackAndIdentity(cardpackName, identity)));
        }

        System.out.println("*********************************************************************");

        stats = standingRepository.getTopFactionStatsByCardPool(cardpackName);
        for (StatCounts stat : stats) {
            String faction = stat.getCategory();
            System.out.println(String.format("%s - %d (%d)", faction, stat.getCount(), deckRepository.countTopByCardPackAndFaction(cardpackName, faction)));
        }
//        List<Card> identities = cardRepository.findIdentities();
//        long totalDecks = deckRepository.count();
//        System.out.println("*********************************************************************");
//        System.out.println(String.format("Number of all decks: %d", totalDecks));
//        for (Factions faction : Factions.values()) {
//            String factionName = faction.toString().replaceAll("_", "-");
//            int factionCount = deckRepository.countByFaction(factionName);
//            if (factionCount > 0) {
//                System.out.println(String.format("* %s: %d", factionName, factionCount));
//                for (Card card : identities) {
//                    if (card.getFaction_code().equals(factionName)) {
//                        int identityCount = deckRepository.countByIdentity(card);
//                        if (identityCount > 0) {
//                            System.out.println(String.format("** %s: %d", card.getTitle(), identityCount));
//                        }
//                    }
//                }
//            }
//        }
//
//        System.out.println("*********************************************************************");
//
//        for (CardCycles cardCycle : CardCycles.values()) {
//            String cycleName = cardCycle.toString().replaceAll("_"," ");
//            int cyclenumber = cardCycle.getCycleNumber();
//            int cycleCount = deckRepository.countByCycle(cyclenumber);
//            if (cycleCount > 0) {
//                System.out.println(String.format("* %s: %d", cycleName, cycleCount));
//                int number = 0;
//                do {
//                    number++;
//                    CardPack cardPack = cardPackRepository.findByCyclenumberAndNumber(cyclenumber, number);
//                    if (cardPack == null) {
//                        break;
//                    } else {
//                        String cardPackName = cardPack.getName();
//                        int cardpackCount = deckRepository.countByCardPack(cardPackName);
//                        if (cardpackCount > 0) {
//                            System.out.println(String.format("** %s: %d", cardPackName, cardpackCount));
//                            getPackStats(cardPackName);
//                        }
//                    }
//                } while(true);
//            }
//        }
//
//        System.out.println("*********************************************************************");
    }

    // TODO: put these enums somewhere general
    public enum Factions {
        neutral, shaper, criminal, anarch, jinteki, haas_bioroid, weyland_consortium, nbn;
    }

    public enum CardCycles {
        Promos(0), Core_Set(1), Genesis(2), Creation_and_Control(3), Spin(4), Honor_and_Profit(5), Lunar(6),
        Order_and_Chaos(7), SanSan(8), Data_and_Destiny(9);

        private final int cycleNumber;

        CardCycles(int cycleNumber) {
            this.cycleNumber = cycleNumber;
        }

        public int getCycleNumber() {
            return this.cycleNumber;
        }
    }
}