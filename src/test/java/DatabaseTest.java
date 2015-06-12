import com.madarasz.netrunnerstats.Application;
import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import com.madarasz.netrunnerstats.DRs.DeckRepository;
import com.madarasz.netrunnerstats.Operations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by madarasz on 2015-06-11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
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
    Neo4jOperations template;

    @Test
    public void createDB() {
        // populate DB
        operations.cleanDB();
        populateDB();
        Assert.assertTrue("No cards in DB.", template.count(Card.class) > 0);
        Assert.assertTrue("No card packs in DB.", template.count(CardPack.class) > 0);
        Assert.assertTrue("No decks in DB.", template.count(Deck.class) > 0);
        Assert.assertTrue("No deck-card relationships in DB.", template.count(DeckHasCard.class) > 0);
    }

    @Test
    public void updateDB() {
        // populate DB
        operations.cleanDB();
        populateDB();
        long countCard = template.count(Card.class);
        long countCardPack = template.count(CardPack.class);
        long countDeck = template.count(Deck.class);
        long countDeckHasCard = template.count(DeckHasCard.class);
        // try readding same data
        populateDB();

        Assert.assertTrue("New cards should not be added.", countCard == template.count(Card.class));
        Assert.assertTrue("New card packs should not be added.", countCardPack == template.count(CardPack.class));
        Assert.assertTrue("New decks should not be added.", countDeck == template.count(Deck.class));
        Assert.assertTrue("New deck-card relationships should not be added.", countDeckHasCard == template.count(DeckHasCard.class));
    }

    @Test
    public void retrieveData() {
        // populate DB
        operations.cleanDB();
        populateDB();

        // positive assertions
        Assert.assertTrue("Could not retrieve card.", cardRepository.findByTitle("Account Siphon") != null);
        Assert.assertTrue("Could not retrieve card pack.", cardPackRepository.findByName("Core Set") != null);
        Assert.assertTrue("Could not retrieve deck.", deckRepository.findByUrl("http://netrunnerdb.com/api//en/decklist/20162") != null);
        Assert.assertTrue("Could not retrieve deck.", deckRepository.findByUrl("http://www.acoo.net/deck/10890") != null);
        // negative assertions
        Assert.assertTrue("Could retrieve non-existent card.", cardRepository.findByTitle("I love Siphon") == null);
        Assert.assertTrue("Could not retrieve non-existent card pack.", cardPackRepository.findByName("No such pack") == null);
        Assert.assertTrue("Could not retrieve non-existent deck.", deckRepository.findByUrl("http://www.google.com") == null);
    }

    private void populateDB() {
        operations.loadNetrunnerDB();
        operations.loadAcooDeck(10890);
        operations.loadNetrunnerDbDeck(20162);
        operations.logDBCount();
    }
}
