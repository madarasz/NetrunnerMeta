import com.madarasz.netrunnerstats.Application;
import com.madarasz.netrunnerstats.Operations;
import com.madarasz.netrunnerstats.database.DOs.stats.CardPoolStats;
import com.madarasz.netrunnerstats.database.DOs.stats.CardUsageStat;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardPool;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import com.madarasz.netrunnerstats.database.DRs.stats.CardUsageStatsRepository;
import com.madarasz.netrunnerstats.springMVC.controllers.CPController;
import com.madarasz.netrunnerstats.springMVC.controllers.CardController;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by madarasz on 11/19/15.
 * Unit tests for testing statistical functions and Spring MVC controllers.
 * TODO: should do something with mocking.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
@WebAppConfiguration
@ConfigurationProperties(prefix = "testdb")
public class StatisticsTest {

    @Autowired
    Operations operations;

    @Autowired
    Neo4jOperations template;

    @Autowired
    GraphDatabase graphDatabase;

    @Autowired
    CPController cpController;

    @Autowired
    CardController cardController;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    CardUsageStatsRepository cardUsageStatsRepository;

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        System.out.println("**************** Setting up ****************");
        operations.cleanDB();
        operations.loadNetrunnerDB();
        operations.loadAcooTournamentDecks(731);
        operations.logDBCount();
        operations.resetStats();
        System.out.println(String.format("**************** Running test: %s ****************", name.getMethodName()));
    }

    @After
    public void tearDown() {
        operations.logDBStatCount();
        System.out.println(String.format("**************** Ended test: %s ****************", name.getMethodName()));
    }

    @Test
    public void cardPoolStatsTest() {
        cpController.getCardPools();
        // check count
        Assert.assertEquals("CardPoolStats not created.", 1, template.count(CardPoolStats.class));
        Assert.assertEquals("CardPool not created.", 1, template.count(CardPool.class));
        // check values
        CardPoolStats cardPoolStats = cardPoolStatsRepository.find();
        CardPool cardPool = cardPoolStats.getSortedCardpool().get(0);
        Assert.assertEquals("Incorrect tournament number in CardPoolStats.", 1, cardPool.getTournamentnum());
        Assert.assertEquals("Incorrect deck number in CardPoolStats.", 14, cardPool.getDecknum());
        Assert.assertEquals("Incorrect standing number in CardPoolStats.", 76, cardPool.getStandingsnum());
        // check duplication
        cpController.getCardPools();
        Assert.assertEquals("CardPoolStats duplicated.", 1, template.count(CardPoolStats.class));
        Assert.assertEquals("CardPool duplicated.", 1, template.count(CardPool.class));
    }

    @Test
    public void mostUsedCardsTest() {
        cardController.getAllDeckInfos("Cardpack", "runner", "Old Hollywood");
        cardController.getAllDeckInfos("Cardpack", "corp", "Old Hollywood");
        cardController.getAllDeckInfos("Cardpool", "runner", "Old Hollywood");
        cardController.getAllDeckInfos("Cardpool", "corp", "Old Hollywood");
        // check count
        Assert.assertEquals("CardUsageStat not created.", 2, template.count(CardUsageStat.class));
        Assert.assertEquals("CardUsage not created.", 49, template.count(CardUsage.class));
        // check values
        CardUsageStat pool = cardUsageStatsRepository.findByCardPoolName("Old Hollywood");
        CardUsageStat pack = cardUsageStatsRepository.findByCardPackName("Old Hollywood");
        String packrunner = pack.getSortedCards("runner").get(0).getCardtitle();
        ArrayList<String> prPossible = new ArrayList<>(Arrays.asList("Trope", "Film Critic"));
        String poolcorp = pool.getSortedCards("corp").get(0).getCardtitle();
        ArrayList<String> pcPossible = new ArrayList<>(
                Arrays.asList("Jackson Howard", "Wraparound", "Hedge Fund", "Cyberdex Virus Suite", "Enigma"));
        Assert.assertTrue("Card usage not calculated right.", prPossible.contains(packrunner));
        Assert.assertTrue("Card usage not calculated right.", pcPossible.contains(poolcorp));
        // check duplication
        cardController.getAllDeckInfos("Cardpack", "runner", "Old Hollywood");
        cardController.getAllDeckInfos("Cardpack", "corp", "Old Hollywood");
        cardController.getAllDeckInfos("Cardpool", "runner", "Old Hollywood");
        cardController.getAllDeckInfos("Cardpool", "corp", "Old Hollywood");
        Assert.assertEquals("CardUsageStat duplicated.", 2, template.count(CardUsageStat.class));
        Assert.assertEquals("CardUsage duplicated.", 49, template.count(CardUsage.class));
    }
}
