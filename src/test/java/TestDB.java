import com.madarasz.netrunnerstats.Application;
import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
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
public class TestDB {

    @Autowired
    Operations operations;

    @Autowired
    Neo4jOperations template;

    @Test
    public void TestDB() {
        operations.cleanDB();
        operations.loadNetrunnerDB(false);
        operations.loadAcooDeck(10890);
        operations.loadNetrunnerDbDeck(20162);
        operations.logDBCount();
        Assert.assertTrue("No cards in DB.", template.count(Card.class) > 0);
        Assert.assertTrue("No card Packs in DB.", template.count(CardPack.class) > 0);
        Assert.assertTrue("No decks in DB.", template.count(Deck.class) > 0);
        Assert.assertTrue("No deck-card relationships in DB.", template.count(DeckHasCard.class) > 0);
    }
}
