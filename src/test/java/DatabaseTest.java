import com.madarasz.netrunnerstats.Application;
import com.madarasz.netrunnerstats.DOs.*;
import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.DRs.*;
import com.madarasz.netrunnerstats.Operations;
import com.madarasz.netrunnerstats.brokers.AcooBroker;
import com.madarasz.netrunnerstats.brokers.NetrunnerDBBroker;
import com.madarasz.netrunnerstats.brokers.StimhackBroker;
import com.madarasz.netrunnerstats.helper.MultiDimensionalScaling;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for DB
 * Created by madarasz on 2015-06-11.
 */
// TODO: DB commits
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
@ConfigurationProperties(prefix = "testdb")
public class DatabaseTest {

    @Autowired
    Operations operations;

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
    GraphDatabase graphDatabase;

    @Autowired
    MultiDimensionalScaling multiDimensionalScaling;

    @Before
    public void prepareDB() {
        // clean DB
        operations.cleanDB();
        // TODO: proper DB wipe
//        Assert.assertTrue("Card count is not 0 after DB wipe.", template.count(Card.class) == 0);
//        Assert.assertTrue("Card pack count is not 0 after DB wipe.", template.count(CardPack.class) == 0);
//        Assert.assertTrue("Deck count is not 0 after DB wipe.", template.count(Deck.class) == 0);
//        Assert.assertTrue("Tournament count is not 0 after DB wipe.", template.count(Tournament.class) == 0);
//        Assert.assertTrue("Standing count is not 0 after DB wipe.", template.count(Standing.class) == 0);
//        Assert.assertTrue("Deck-card relationship count is not 0 after DB wipe.", template.count(DeckHasCard.class) == 0);

        // load Netrunner DB cards and card packs
        operations.loadNetrunnerDB();
        Assert.assertTrue("Card count is 0 after Netrunner DB import.", template.count(Card.class) > 0);
        Assert.assertTrue("Card pack count is 0 after Netrunner DB import", template.count(CardPack.class) > 0);
    }

    @Test
    public void loadCards() {
        Card idcard = cardRepository.findByTitle("Andromeda: Dispossessed Ristie");
        Assert.assertEquals("Card read error - side", idcard.isRunner(), true);
        Assert.assertEquals("Card read error - type_code", idcard.getType_code(), "identity");
        Assert.assertEquals("Card read error - code", idcard.getCode(), "02083");
        Assert.assertEquals("Card read error - subtype_code", idcard.getSubtype_code(), "natural");
        Assert.assertEquals("Card read error - faction_code", idcard.getFaction_code(), "criminal");
        Assert.assertEquals("Card read error - influence_limit", idcard.getInfluencelimit(), 15);
        Assert.assertEquals("Card read error - minimumdecksize", idcard.getMinimumdecksize(), 45);
        Assert.assertEquals("Card read error - baselink", idcard.getBaselink(), 1);
        Assert.assertEquals("Card read error - limited", idcard.isLimited(), false);
        Assert.assertEquals("Card read error - text", idcard.getText(), "You draw a starting hand of 9 cards.");
        Assert.assertEquals("Card read error - card pack", idcard.getCardPack().getName(), "Humanity's Shadow");

        Card agendacard = cardRepository.findByCode("06030");
        Assert.assertEquals("Card read error - side", agendacard.isRunner(), false);
        Assert.assertEquals("Card read error - title", agendacard.getTitle(), "Eden Fragment");
        Assert.assertEquals("Card read error - type_code", agendacard.getType_code(), "agenda");
        Assert.assertEquals("Card read error - agendapoints", agendacard.getAgendapoints(), 3);
        Assert.assertEquals("Card read error - advancementcost", agendacard.getAdvancementcost(), 5);
        Assert.assertEquals("Card read error - limited", agendacard.isLimited(), true);
        Assert.assertEquals("Card read error - card pack", agendacard.getCardPack().getName(), "The Spaces Between");

        Card icecard = cardRepository.findByTitle("Matrix Analyzer");
        Assert.assertEquals("Card read error - cost", icecard.getCost(), 1);
        Assert.assertEquals("Card read error - strength", icecard.getStrength(), 3);
        Assert.assertEquals("Card read error - type_code", icecard.getType_code(), "ice");
        Assert.assertEquals("Card read error - uniqueness", icecard.isUniquene(), false);
        Assert.assertEquals("Card read error - faction_cost", icecard.getFactioncost(), 2);
        Assert.assertEquals("Card read error - subtype_code", icecard.getSubtype_code(), "sentry - tracer - observer");

        Card upgradecard = cardRepository.findByTitle("Marcus Batty");
        Assert.assertEquals("Card read error - type_code", upgradecard.getType_code(), "upgrade");
        Assert.assertEquals("Card read error - uniqueness", upgradecard.isUniquene(), true);
        Assert.assertEquals("Card read error - trash", upgradecard.getTrash(), 1);

        Card programcard = cardRepository.findByTitle("Corroder");
        Assert.assertEquals("Card read error - memoryunits", programcard.getMemoryunits(), 1);
    }

    @Test
    public void loadDecks() {
        long decknum = template.count(Deck.class);
        Set<Deck> decks = new HashSet<Deck>();
        decks.add(operations.loadAcooDeck(10890));
        decks.add(operations.loadNetrunnerDbDeck(20162));
        decks.addAll(operations.loadStimhackDecks("http://stimhack.com/gnk-game-kastle-santa-clara-11-players/")); // netrunnerdb import
        decks.addAll(operations.loadStimhackDecks("http://stimhack.com/gnk-madison-wi-14-players/"));   // netrunnerdb old import
        decks.addAll(operations.loadStimhackDecks("http://stimhack.com/gnk-waylands-forge-birmingham-uk-21-players/"));   // meteor import
        decks.addAll(operations.loadStimhackDecks("http://stimhack.com/anr-pro-circuit-san-antonio-10-players/"));   // net deck import
        decks.addAll(operations.loadStimhackDecks("http://stimhack.com/the-psi-games-44-players/"));   // acoo import

        // count check
        operations.logDBCount();
        Assert.assertEquals("Deck count wrong after deck imports.", template.count(Deck.class) - decknum, 12);
        Assert.assertTrue("Deck-card relationship count is 0 after deck imports.", template.count(DeckHasCard.class) >= 276);

        // validity check
        for (Deck deck : decks) {
            Assert.assertTrue("Imported deck not valid: " + deck.getUrl(), deck.isValidDeck());
        }

        // deck multiple reentry check
        operations.loadAcooDeck(10890);
        operations.loadNetrunnerDbDeck(20162);
        operations.loadStimhackDecks("http://stimhack.com/gnk-game-kastle-santa-clara-11-players/");
        Assert.assertEquals("Deck count wrong after deck reimports.", template.count(Deck.class) - decknum, 12);
    }

    @Test
    public void loadTournaments() {
        long decknum = template.count(Deck.class);
        long tournamentnum = template.count(Tournament.class);
        Tournament stournament = operations.loadStimhackTournament("http://stimhack.com/store-champs-gamespace-kashiwagi-tokyo-9-players/"); // this imports decks as well
        Tournament atournament = operations.loadAcooTournament(784); // just swiss - this does not import decks

        // count check
        Assert.assertEquals("Tournament count wrong after tournament imports.", template.count(Tournament.class) - tournamentnum, 2);
        Assert.assertEquals("Deck count wrong after tournament imports.", template.count(Deck.class) - decknum, 2);

        // validity check
        Assert.assertEquals("Stimhack tournament import, wrong player number", stournament.getPlayerNumber(), 9);
        Assert.assertEquals("Stimhack tournament import, wrong cardpool", stournament.getCardpool().getName(), "The Universe of Tomorrow");
        Assert.assertEquals("Stimhack tournament import, wrong tournament name", stournament.getName(), "Store Champs – Gamespace Kashiwagi, Tokyo");
        Assert.assertEquals("Stimhack tournament import, wrong tournament url", stournament.getUrl(), "http://stimhack.com/store-champs-gamespace-kashiwagi-tokyo-9-players/");
        Assert.assertEquals("Acoo tournament import, wrong player number", atournament.getPlayerNumber(), 14);
        Assert.assertEquals("Acoo tournament import, wrong cardpool", atournament.getCardpool().getName(), "The Universe of Tomorrow");
        Assert.assertEquals("Acoo tournament import, wrong tournament name", atournament.getName(), "Moscow GNK 2015-11-01");
        Assert.assertEquals("Acoo tournament import, wrong tournament urk", atournament.getUrl(), acooBroker.tournamentUrlFromId(784));

        // tournament multiple reentry check
        operations.loadStimhackTournament("http://stimhack.com/store-champs-gamespace-kashiwagi-tokyo-9-players/");
        operations.loadAcooTournament(784);
        Assert.assertEquals("Tournament count wrong after tournament reimports.", template.count(Tournament.class) - tournamentnum, 2);
        Assert.assertEquals("Deck count wrong after tournament reimports.", template.count(Deck.class) - decknum, 2);
    }

    @Test
    public void loadTournamentStandings() {
        long decknum = template.count(Deck.class);
        long tournamentnum = template.count(Tournament.class);
        long standingsnum = template.count(Standing.class);

        // count check
        operations.loadAcooTournamentDecks(476); // swiss + top, 24 standings (lot missing), 14 decks
        Assert.assertEquals("Tournament count wrong after tournament imports.", template.count(Tournament.class) - tournamentnum, 1);
        Assert.assertEquals("Deck count wrong after tournament imports.", template.count(Deck.class) - decknum, 14);
        Assert.assertEquals("Standings count wrong after tournament imports.", template.count(Standing.class) - standingsnum, 24);
        Assert.assertEquals("Tournament having incorrect number of standings",
                standingRepository.countByTournamentURL(acooBroker.tournamentUrlFromId(476)), 24);

        operations.loadAcooTournamentDecks(744); // swiss + top, 2x12 standings, 0 decks
        Assert.assertEquals("Tournament count wrong after tournament imports.", template.count(Tournament.class) - tournamentnum, 2);
        Assert.assertEquals("Deck count wrong after tournament imports.", template.count(Deck.class) - decknum, 14);
        Assert.assertEquals("Standings count wrong after tournament imports.", template.count(Standing.class) - standingsnum, 48);
        Assert.assertEquals("Tournament having incorrect number of standings",
                standingRepository.countByTournamentURL(acooBroker.tournamentUrlFromId(744)), 24);

        operations.loadAcooTournamentDecks(439); // just swiss, 2x10 standings, 6 decks
        Assert.assertEquals("Tournament count wrong after tournament imports.", template.count(Tournament.class) - tournamentnum, 3);
        Assert.assertEquals("Deck count wrong after tournament imports.", template.count(Deck.class) - decknum, 20);
        Assert.assertEquals("Standings count wrong after tournament imports.", template.count(Standing.class) - standingsnum, 68);
        Assert.assertEquals("Tournament having incorrect number of standings",
                standingRepository.countByTournamentURL(acooBroker.tournamentUrlFromId(439)), 20);

        operations.loadStimhackTournament("http://stimhack.com/anr-pro-circuit-austin-tx-20-players/");
        Assert.assertEquals("Tournament count wrong after tournament imports.", template.count(Tournament.class) - tournamentnum, 4);
        Assert.assertEquals("Deck count wrong after tournament imports.", template.count(Deck.class) - decknum, 22);
        Assert.assertEquals("Standings count wrong after tournament imports.", template.count(Standing.class) - standingsnum, 70);
        Assert.assertEquals("Tournament having incorrect number of standings",
                standingRepository.countByTournamentURL("http://stimhack.com/anr-pro-circuit-austin-tx-20-players/"), 2);

        // validity
        Standing standing = standingRepository.findByTournamentURLIdentity(
                acooBroker.tournamentUrlFromId(476), 1, "Jinteki Biotech: Life Imagined");
        Standing standing2 = standingRepository.findByTournamentURLIdentity(
                "http://stimhack.com/anr-pro-circuit-austin-tx-20-players/", 1, "Andromeda: Dispossessed Ristie");
        Standing standing3 = standingRepository.findByTournamentURLIdentity(
                acooBroker.tournamentUrlFromId(744), 11, "Whizzard: Master Gamer");
        Assert.assertNotNull("Standing save is not succesfull.", standing);
        Assert.assertEquals("Runner side is incorrect in Standing save", standing.is_runner(), false);
        Assert.assertEquals("Topdeck is incorrect in Standing save", standing.isTopdeck(), true);
        Assert.assertNotNull("Standing save is not succesfull.", standing2);
        Assert.assertEquals("Runner side is incorrect in Standing save", standing2.is_runner(), true);
        Assert.assertEquals("Topdeck is incorrect in Standing save", standing2.isTopdeck(), true);
        Assert.assertNotNull("Standing save is not succesfull.", standing3);
        Assert.assertEquals("Runner side is incorrect in Standing save", standing3.is_runner(), true);
        Assert.assertEquals("Topdeck is incorrect in Standing save", standing3.isTopdeck(), false);

        // tournament multiple reentry check
        operations.loadAcooTournamentDecks(476);
        operations.loadAcooTournamentDecks(744);
        operations.loadAcooTournamentDecks(439);
        operations.loadStimhackTournament("http://stimhack.com/anr-pro-circuit-austin-tx-20-players/");
        Assert.assertEquals("Tournament count wrong after tournament imports.", template.count(Tournament.class) - tournamentnum, 4);
        Assert.assertEquals("Deck count wrong after tournament imports.", template.count(Deck.class) - decknum, 22);
        Assert.assertEquals("Standings count wrong after tournament imports.", template.count(Standing.class) - standingsnum, 70);
    }

    @Test
    public void loadAcooTournamentPage() {
        long decknum = template.count(Deck.class);
        long tournamentnum = template.count(Tournament.class);
        long standingsnum = template.count(Standing.class);

        operations.loadAcooTournamentsFromUrl("http://www.acoo.net/tournament/set/old-hollywood/1/", true, true);
        operations.logDBCount();
        long decknum2 = template.count(Deck.class);
        long tournamentnum2 = template.count(Tournament.class);
        long standingsnum2 = template.count(Standing.class);

        Assert.assertTrue("Not enough decks after tournament page import", decknum2 - decknum >= 123);
        Assert.assertTrue("Not enough tournaments after tournament page import", tournamentnum2 - tournamentnum >= 19);
        Assert.assertTrue("Not enough standings after tournament page import", standingsnum2 - standingsnum >= 958);

        operations.loadAcooTournamentsFromUrl("http://www.acoo.net/tournament/set/old-hollywood/1/", true, false);
        operations.logDBCount();
        long decknum3 = template.count(Deck.class);
        long tournamentnum3 = template.count(Tournament.class);
        long standingsnum3 = template.count(Standing.class);

        Assert.assertEquals("No new deck should be readded", decknum2, decknum3);
        Assert.assertTrue("Tournament number not OK after readd", tournamentnum3 - tournamentnum2 >= 18);
        Assert.assertTrue("Standing number not OK after readd", standingsnum3 - standingsnum2 >= 464);
    }
}

//    @Test
//    public void cardCaseCheck() {
//        populateDB(true, false);
//
//        Card card = cardRepository.findByTitle("I've had worse");
//        Assert.assertTrue("Could not retrive card by worng case.", card != null);
//        System.out.println("Retrieved card: " + card.toString());
//    }

//    @Test
//    public void unicodeRemoval() {
//        populateDB(true, false);
//        Deck deck = operations.loadAcooDeck(11282);
//        System.out.println(deck.toString());
//        Card card = cardRepository.findByTitle("Tori Hanzō");
//        System.out.println(card.toString());
//    }

//    @Test
//    public void archetypes() {
//        Transaction tx = graphDatabase.beginTx();
//        populateDB(true, false);
//        operations.loadAcooTournamentsFromUrl("http://www.acoo.net/tournament/set/breaker-bay/1/", true, true);
//        tx.success();
//        operations.logDBCount();
//
//        long decknum = template.count(Deck.class);
//        long tournamenthasdecknum = template.count(TournamentHasDeck.class);
//        Assert.assertTrue("Deck and Tournament-deck relation numbers are different.", decknum == tournamenthasdecknum);
//
//        // filter decks by faction
//        List<Deck> jintekiDecks = deckRepository.filterByFaction("jinteki");
//        List<Deck> rpDecks = deckRepository.filterByIdentityAndCardPool("Jinteki: Replicating Perfection", "Breaker Bay");
//        List<Deck> rpTopDecks = deckRepository.filterTopByIdentityAndCardPool("Jinteki: Replicating Perfection", "Breaker Bay");
//        List<Deck> andyTopDecks = deckRepository.filterTopByIdentityAndCardPool("Andromeda: Dispossessed Ristie", "Breaker Bay");
//        int jsize = jintekiDecks.size();
//        int rpsize = rpDecks.size();
//        int rptopsize = rpTopDecks.size();
//        int andytopsize = andyTopDecks.size();
//        System.out.println(String.format("Jinteki decks: %d, Jinteki RP decks: %d, Jinteki RP top decks: %d, Andy top decks: %d",
//                jsize, rpsize, rptopsize, andytopsize));
//        Assert.assertTrue("Could not find RP decks.", rpsize > 0);
//        Assert.assertTrue("There should be more Jinteki decks than RP decks.", jsize >= rpsize);
//        Assert.assertTrue("There should be more RP decks than RP top decks.", rpsize >= rptopsize);
//        Assert.assertTrue("Could not find Andy decks.", andytopsize > 0);
//
//        // archetype check
//        Archetype RP = new Archetype("RP Top", rpTopDecks, cardRepository.findByTitle("Jinteki: Replicating Perfection"));
//        String output = RP.toString();
//        System.out.println(output);
//        Assert.assertTrue("RP Architype should have Nisei.", output.contains("Nisei MK II"));
//        Archetype andy = new Archetype("Andy Top", andyTopDecks, cardRepository.findByTitle("Andromeda: Dispossessed Ristie"));
//        output = andy.toString();
//        System.out.println(output);
//        Assert.assertTrue("Andy Architype should have Cloak.", output.contains("Cloak"));
//        tx.close();
//    }

//    @Test
//    public void MDS() {
//        populateDB(true, false);
//        operations.loadAcooTournament(11949);
//        operations.loadAcooTournament(11879);
//        operations.loadAcooTournament(12022);
//        operations.loadAcooTournament(11852);
//
//        // the very same decks at 1st and 4th place
//        Deck signature_kate = deckRepository.findByUrl(acooBroker.deckUrlFromId(11949));
//        Deck signature_kate2 = deckRepository.findByUrl(acooBroker.deckUrlFromId(11879));
//        Assert.assertTrue("Distance of the same decks should be zero.", multiDimensionalScaling.getDeckDistance(signature_kate, signature_kate2) == 0);
//
//        // two very similar butchershops
//        Deck butcher1 = deckRepository.findByUrl(acooBroker.deckUrlFromId(12022));
//        Deck butcher2 = deckRepository.findByUrl(acooBroker.deckUrlFromId(11852));
//        Assert.assertTrue("Distance of the same decks should be zero.", multiDimensionalScaling.getDeckDistance(butcher1, butcher2) == 4);
//    }
