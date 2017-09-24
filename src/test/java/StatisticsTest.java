import com.madarasz.netrunnerstats.Application;
import com.madarasz.netrunnerstats.Operations;
import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.stats.*;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.*;
import com.madarasz.netrunnerstats.database.DRs.stats.*;
import com.madarasz.netrunnerstats.springMVC.controllers.*;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
@WebAppConfiguration
@ConfigurationProperties(prefix = "testdb")
public class StatisticsTest {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsTest.class);

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
    DPController dpController;

    @Autowired
    MDSController mdsController;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    CardUsageStatsRepository cardUsageStatsRepository;

    @Autowired
    DPStatsRepository dpStatsRepository;

    @Autowired
    Statistics statistics;

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() {
        logger.info("**************** Setting up ****************");
        operations.cleanDB();
        operations.loadNetrunnerDB();
        operations.loadAcooTournamentDecks(731);
        operations.logDBCount();
        operations.resetStats();
        logger.info(String.format("**************** Running test: %s ****************", name.getMethodName()));
    }

    @After
    public void tearDown() {
        operations.logDBStatCount();
        logger.info(String.format("**************** Ended test: %s ****************", name.getMethodName()));
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
    //TODO:update
    public void mostUsedCardsTest() {
        operations.loadAcooTournamentDecks(790);
//        cardController.getMostUsedCards("Cardpack", "runner", "Old Hollywood");
//        cardController.getMostUsedCards("Cardpack", "corp", "Old Hollywood");
        statistics.getMostUsedCardsForCardpool("runner", "Old Hollywood");
        statistics.getMostUsedCardsForCardpool("corp", "Old Hollywood");
//        cardController.getMostUsedCards("Cardpack", "runner", "The Universe of Tomorrow");
//        cardController.getMostUsedCards("Cardpack", "corp", "The Universe of Tomorrow");
        statistics.getMostUsedCardsForCardpool("runner", "The Universe of Tomorrow");
        statistics.getMostUsedCardsForCardpool("corp", "The Universe of Tomorrow");
        // check count
//        Assert.assertEquals("CardUsageStat are not created.", 4, template.count(CardUsageStat.class));
        Assert.assertEquals("CardUsage are not created.", 160, template.count(CardUsage.class));
        // check values - deck counts
        Assert.assertEquals("Deck counts incorrect", 2,
                statistics.getDeckNumberFromCardpoolOnward("The Universe of Tomorrow", "corp", true, false));
        Assert.assertEquals("Deck counts incorrect", 5,
                statistics.getDeckNumberFromCardpoolOnward("The Universe of Tomorrow", "corp", false, false));
        Assert.assertEquals("Deck counts incorrect", 6,
                statistics.getDeckNumberFromCardpoolOnward("Old Hollywood", "corp", true, false));
        Assert.assertEquals("Deck counts incorrect", 12,
                statistics.getDeckNumberFromCardpoolOnward("Old Hollywood", "corp", false, false));
        // check values
//        CardUsageStat pool = cardUsageStatsRepository.findByCardPoolName("Old Hollywood");
        CardUsageStat pack = cardUsageStatsRepository.findByCardPackName("Old Hollywood");
        String packrunner = pack.getSortedCards("runner").get(0).getCardtitle();
        ArrayList<String> prPossible = new ArrayList<>(Arrays.asList("Trope", "Film Critic"));
//        String poolcorp = pool.getSortedCards("corp").get(0).getCardtitle();
        ArrayList<String> pcPossible = new ArrayList<>(
                Arrays.asList("Jackson Howard", "Wraparound", "Hedge Fund", "Cyberdex Virus Suite", "Enigma"));
        Assert.assertTrue("Card usage is not calculated right.", prPossible.contains(packrunner));
//        Assert.assertTrue("Card usage is not calculated right.", pcPossible.contains(poolcorp));
        // check duplication
//        cardController.getMostUsedCards("Cardpack", "runner", "Old Hollywood");
//        cardController.getMostUsedCards("Cardpack", "corp", "Old Hollywood");
        statistics.getMostUsedCardsForCardpool("runner", "Old Hollywood");
        statistics.getMostUsedCardsForCardpool("corp", "Old Hollywood");
//        Assert.assertEquals("CardUsageStat is duplicated.", 4, template.count(CardUsageStat.class));
        Assert.assertEquals("CardUsage is duplicated.", 160, template.count(CardUsage.class));
    }

    @Test
    // TODO
    public void deckInfoTest() {
//        List<DeckInfo> deckInfo = deckController.getAllDeckInfos("Near-Earth Hub: Broadcast Center", "Old Hollywood");
//        DeckInfos deckInfos = statistics.getDeckInfos("Near-Earth Hub: Broadcast Center", "Old Hollywood");
//        deckController.getAllDeckInfos("Noise: Hacker Extraordinaire", "Old Hollywood");
//        // check count
//        Assert.assertEquals("DeckInfos are not created", 2, template.count(DeckInfos.class));
//        Assert.assertEquals("DeckInfo are not created", 3, template.count(DeckInfo.class));
//        // check values
//        Assert.assertEquals("DeckInfo count per identity is not OK.", 2, deckInfo.size());
//        Assert.assertEquals("DeckInfos cardpool is not OK.", "Old Hollywood", deckInfos.getCardpoolname());
//        Assert.assertEquals("DeckInfos indentity is not OK.", "Near-Earth Hub: Broadcast Center", deckInfos.getIdentitytitle());
//        Assert.assertEquals("DeckInfo url is not correct.", "http://www.acoo.net/deck/13824" ,deckInfo.get(0).getUrl());
//        Assert.assertTrue("DeckInfo digest is not correct.", deckInfo.get(0).getDigest().contains("Bucha"));
//        Assert.assertTrue("DeckInfo digest is not correct.", deckInfo.get(0).getDigest().contains("Vladislav"));
//        Assert.assertTrue("DeckInfo digest is not correct.", deckInfo.get(0).getDigest().contains("49 cards"));
//        Assert.assertTrue("DeckInfo digest is not correct.", deckInfo.get(0).getDigest().contains("#6 / 38"));
//        Assert.assertTrue("DeckInfo digest is not correct.", deckInfo.get(0).getDigest().contains("Slovakia"));
//        Assert.assertTrue("DeckInfo HtmlDigest is not correct.", deckInfo.get(0).getHtmlDigest().contains("Operation (11)"));
//        Assert.assertTrue("DeckInfo HtmlDigest is not correct.", deckInfo.get(0).getHtmlDigest().contains("acoo.net"));
//        Assert.assertTrue("DeckInfo HtmlDigest is not correct.", deckInfo.get(0).getHtmlDigest().contains("#6 / 38"));
//        Assert.assertTrue("DeckInfo HtmlDigest is not correct.", deckInfo.get(0).getHtmlDigest().contains("Slovakia"));
//        // check duplication
//        deckController.getAllDeckInfos("Near-Earth Hub: Broadcast Center", "Old Hollywood");
//        deckController.getAllDeckInfos("Noise: Hacker Extraordinaire", "Old Hollywood");
//        Assert.assertEquals("DeckInfos are duplicated", 2, template.count(DeckInfos.class));
//        Assert.assertEquals("DeckInfo is duplicated", 3, template.count(DeckInfo.class));

    }

    @Test
    // TODO
    public void dataPackTest() {
        // testing DPStatistics and CountDeckStands
//        dpController.getDPTopDataTable("Compare", "runner", "faction", "Old Hollywood");
//        DataTable dataTable = dpController.getDPTopDataTable("Compare", "corp", "identity", "Old Hollywood");
//        // check count
//        Assert.assertEquals("DP statistics are not created", 2, template.count(DPStatistics.class));
//        Assert.assertEquals("CountDeckStands are not created", 50, template.count(CountDeckStands.class));
//        // check values
//        Assert.assertEquals("DataTable values are not correct.",
//                "Near-Earth Hub: Broadcast Center", ((CellString)dataTable.getRows().get(0).getC().get(0)).getV());
//        Assert.assertEquals("DataTable values are not correct.",
//                "36.8%", dataTable.getRows().get(0).getC().get(1).getF());
//        Assert.assertEquals("DataTable values are not correct.",
//                "63.6%", dataTable.getRows().get(0).getC().get(2).getF());
//        // check duplication
//        dpController.getDPTopDataTable("Top", "runner", "identity", "Old Hollywood");
//        dpController.getDPTopDataTable("All", "corp", "faction", "Old Hollywood");
//        Assert.assertEquals("DP statistics are duplicated", 2, template.count(DPStatistics.class));
//        Assert.assertEquals("CountDeckStands are duplicated", 50, template.count(CountDeckStands.class));
//
//        // testing DPIdentities and DPIdentity
//        dpController.getDPIdentities("runner", "Old Hollywood");
//        List<DPIdentity> identities = dpController.getDPIdentities("corp", "Old Hollywood");
//        // check count
//        Assert.assertEquals("DP Identities are not created", 2, template.count(DPIdentities.class));
//        Assert.assertEquals("DP Identity objects are not created", 13, template.count(DPIdentity.class));
//        // check values
//        Assert.assertEquals("DP Identity data is not correct.",
//                "Near-Earth Hub: Broadcast Center", identities.get(0).getTitle());
//        Assert.assertEquals("DP Identity data is not correct.",
//                "/MDSIdentity/Old Hollywood/Near-Earth Hub: Broadcast Center", identities.get(0).getUrl());
//        Assert.assertEquals("DP Identity data is not correct.", 2, identities.get(0).getTopdecknum());
//        Assert.assertEquals("DP Identity data is not correct.", 2, identities.get(0).getDecknum());
//        // check duplication
//        dpController.getDPIdentities("runner", "Old Hollywood");
//        dpController.getDPIdentities("corp", "Old Hollywood");
//        Assert.assertEquals("DP Identities are duplicated", 2, template.count(DPIdentities.class));
//        Assert.assertEquals("DP Identity objects are duplicated", 13, template.count(DPIdentity.class));
    }

    @Test
    // TODO
    public void mdsTest() {
        // MDS calculation
//        DataTable dataTable = mdsController.getMDSDataTable("Near-Earth Hub: Broadcast Center", "Old Hollywood");
//        // check count
//        Assert.assertEquals("IdentityMDS is not created", 1, template.count(IdentityMDS.class));
//        Assert.assertEquals("MDSEntries are not created", 2, template.count(MDSEntry.class));
//        // check values
//        Assert.assertTrue("MDSEntries are not correct",
//                ((CellNumber)dataTable.getRows().get(0).getC().get(0)).getV() +
//                        ((CellNumber)dataTable.getRows().get(1).getC().get(0)).getV() < 0.01);
//        Assert.assertTrue("MDSEntries are not correct",
//                ((CellNumber)dataTable.getRows().get(0).getC().get(1)).getV() +
//                        ((CellNumber)dataTable.getRows().get(1).getC().get(1)).getV() < 0.01);
//        Assert.assertTrue("MDSEntries are not correct",
//                ((CellString)dataTable.getRows().get(0).getC().get(3)).getV().contains("Bucha"));
//        Assert.assertTrue("MDSEntries are not correct",
//                ((CellString)dataTable.getRows().get(1).getC().get(3)).getV().contains("FAstrobiotics"));
//        // check duplication
//        mdsController.getMDSDataTable("Near-Earth Hub: Broadcast Center", "Old Hollywood");
//        Assert.assertEquals("IdentityMDS is duplicated", 1, template.count(IdentityMDS.class));
//        Assert.assertEquals("MDSEntries are duplicated", 2, template.count(MDSEntry.class));
//
//        // average deck calculation
//        List<CardAverage> average = mdsController.getDeckAverage("Near-Earth Hub: Broadcast Center", "Old Hollywood");
//        // check count
//        Assert.assertEquals("IdentityAverage is not created", 1, template.count(IdentityAverage.class));
//        Assert.assertEquals("CardAverages are not created", 29, template.count(CardAverage.class));
//        // check values
//        ArrayList<String> possible = new ArrayList<>(Arrays.asList("Project Beale", "AstroScript Pilot Program"));
//        Assert.assertTrue("CardAverages are not correct", possible.contains(average.get(0).getCardtitle()));
//        Assert.assertEquals("CardAverages are not correct", "50.0%", average.get(28).getUsing());
//        Assert.assertEquals("CardAverages are not correct", "0.50", average.get(28).getAverage());
//        Assert.assertEquals("CardAverages are not correct", "1.00", average.get(28).getAverageifused());
//        // check duplication
//        mdsController.getDeckAverage("Near-Earth Hub: Broadcast Center", "Old Hollywood");
//        Assert.assertEquals("IdentityAverage is duplicated", 1, template.count(IdentityAverage.class));
//        Assert.assertEquals("CardAverages are duplicated", 29, template.count(CardAverage.class));
    }
}
