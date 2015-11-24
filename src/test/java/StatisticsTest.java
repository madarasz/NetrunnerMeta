import com.madarasz.netrunnerstats.Application;
import com.madarasz.netrunnerstats.Operations;
import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.CardPoolStats;
import com.madarasz.netrunnerstats.database.DOs.stats.CardUsageStat;
import com.madarasz.netrunnerstats.database.DOs.stats.DeckInfos;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardPool;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.DeckInfo;
import com.madarasz.netrunnerstats.database.DRs.stats.*;
import com.madarasz.netrunnerstats.springMVC.controllers.*;
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
import java.util.List;

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
    DeckController deckController;

    @Autowired
    DPController dpController;

    @Autowired
    MDSController mdsController;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    CardUsageStatsRepository cardUsageStatsRepository;

    @Autowired
    DeckInfosRepository deckInfosRepository;

    @Autowired
    DPIdentitiesRepository dpIdentitiesRepository;

    @Autowired
    DPStatsRepository dpStatsRepository;

    @Autowired
    IdentityMDSRepository identityMDSRepository;

    @Autowired
    Statistics statistics;

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
        Assert.assertEquals("CardPoolStats is not created.", 1, template.count(CardPoolStats.class));
        Assert.assertEquals("CardPool is not created.", 1, template.count(CardPool.class));
        // check values
        CardPoolStats cardPoolStats = cardPoolStatsRepository.find();
        CardPool cardPool = cardPoolStats.getSortedCardpool().get(0);
        Assert.assertEquals("Incorrect tournament number in CardPoolStats.", 1, cardPool.getTournamentnum());
        Assert.assertEquals("Incorrect deck number in CardPoolStats.", 14, cardPool.getDecknum());
        Assert.assertEquals("Incorrect standing number in CardPoolStats.", 76, cardPool.getStandingsnum());
        // check duplication
        cpController.getCardPools();
        Assert.assertEquals("CardPoolStats are duplicated.", 1, template.count(CardPoolStats.class));
        Assert.assertEquals("CardPool is duplicated.", 1, template.count(CardPool.class));
    }

    @Test
    public void mostUsedCardsTest() {
        cardController.getMostUsedCards("Cardpack", "runner", "Old Hollywood");
        cardController.getMostUsedCards("Cardpack", "corp", "Old Hollywood");
        cardController.getMostUsedCards("Cardpool", "runner", "Old Hollywood");
        cardController.getMostUsedCards("Cardpool", "corp", "Old Hollywood");
        // check count
        Assert.assertEquals("CardUsageStat are not created.", 2, template.count(CardUsageStat.class));
        Assert.assertEquals("CardUsage are not created.", 49, template.count(CardUsage.class));
        // check values
        CardUsageStat pool = cardUsageStatsRepository.findByCardPoolName("Old Hollywood");
        CardUsageStat pack = cardUsageStatsRepository.findByCardPackName("Old Hollywood");
        String packrunner = pack.getSortedCards("runner").get(0).getCardtitle();
        ArrayList<String> prPossible = new ArrayList<>(Arrays.asList("Trope", "Film Critic"));
        String poolcorp = pool.getSortedCards("corp").get(0).getCardtitle();
        ArrayList<String> pcPossible = new ArrayList<>(
                Arrays.asList("Jackson Howard", "Wraparound", "Hedge Fund", "Cyberdex Virus Suite", "Enigma"));
        Assert.assertTrue("Card usage is not calculated right.", prPossible.contains(packrunner));
        Assert.assertTrue("Card usage is not calculated right.", pcPossible.contains(poolcorp));
        // check duplication
        cardController.getMostUsedCards("Cardpack", "runner", "Old Hollywood");
        cardController.getMostUsedCards("Cardpack", "corp", "Old Hollywood");
        cardController.getMostUsedCards("Cardpool", "runner", "Old Hollywood");
        cardController.getMostUsedCards("Cardpool", "corp", "Old Hollywood");
        Assert.assertEquals("CardUsageStat is duplicated.", 2, template.count(CardUsageStat.class));
        Assert.assertEquals("CardUsage is duplicated.", 49, template.count(CardUsage.class));
    }

    @Test
    public void deckInfoTest() {
        List<DeckInfo> deckInfo = deckController.getAllDeckInfos("Near-Earth Hub: Broadcast Center", "Old Hollywood");
        DeckInfos deckInfos = statistics.getDeckInfos("Near-Earth Hub: Broadcast Center", "Old Hollywood");
        deckController.getAllDeckInfos("Noise: Hacker Extraordinaire", "Old Hollywood");
        // check count
        Assert.assertEquals("DeckInfos are not created", 2, template.count(DeckInfos.class));
        Assert.assertEquals("DeckInfo are not created", 3, template.count(DeckInfo.class));
        // check values
        Assert.assertEquals("DeckInfo count per identity is not OK.", 2, deckInfo.size());
        Assert.assertEquals("DeckInfos cardpool is not OK.", "Old Hollywood", deckInfos.getCardpoolname());
        Assert.assertEquals("DeckInfos indentity is not OK.", "Near-Earth Hub: Broadcast Center", deckInfos.getIdentitytitle());
        Assert.assertEquals("DeckInfo url is not correct.", "http://www.acoo.net/deck/13824" ,deckInfo.get(0).getUrl());
        Assert.assertTrue("DeckInfo digest is not correct.", deckInfo.get(0).getDigest().contains("Bucha"));
        Assert.assertTrue("DeckInfo digest is not correct.", deckInfo.get(0).getDigest().contains("Vladislav"));
        Assert.assertTrue("DeckInfo digest is not correct.", deckInfo.get(0).getDigest().contains("49 cards"));
        Assert.assertTrue("DeckInfo digest is not correct.", deckInfo.get(0).getDigest().contains("#6 / 38"));
        Assert.assertTrue("DeckInfo digest is not correct.", deckInfo.get(0).getDigest().contains("Slovakia"));
        Assert.assertTrue("DeckInfo HtmlDigest is not correct.", deckInfo.get(0).getHtmlDigest().contains("Operation (11)"));
        Assert.assertTrue("DeckInfo HtmlDigest is not correct.", deckInfo.get(0).getHtmlDigest().contains("acoo.net"));
        Assert.assertTrue("DeckInfo HtmlDigest is not correct.", deckInfo.get(0).getHtmlDigest().contains("#6 / 38"));
        Assert.assertTrue("DeckInfo HtmlDigest is not correct.", deckInfo.get(0).getHtmlDigest().contains("Slovakia"));
        // check duplication
        deckController.getAllDeckInfos("Near-Earth Hub: Broadcast Center", "Old Hollywood");
        deckController.getAllDeckInfos("Noise: Hacker Extraordinaire", "Old Hollywood");
        Assert.assertEquals("DeckInfos are duplicated", 2, template.count(DeckInfos.class));
        Assert.assertEquals("DeckInfo is duplicated", 3, template.count(DeckInfo.class));

    }
}
