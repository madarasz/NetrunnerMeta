package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import com.madarasz.netrunnerstats.brokers.NetrunnerDBBroker;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;

import java.io.File;
import java.util.Set;

/**
 * Created by madarasz on 2015-06-08.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Configuration
    @EnableNeo4jRepositories(basePackages = "com.madarasz.netrunnerstats")
    static class ApplicationConfig extends Neo4jConfiguration {

        public ApplicationConfig() {
            setBasePackage("com.madarasz.netrunnerstats");
        }

        @Bean
        GraphDatabaseService graphDatabaseService() {
            return new GraphDatabaseFactory().newEmbeddedDatabase("netrunner.db");
        }
    }

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;

    @Autowired
    NetrunnerDBBroker netrunnerDBBroker;

    @Autowired
    GraphDatabase graphDatabase;


    public void run(String... args) throws Exception {

        PossibleOperations op = PossibleOperations.valueOf(args[0]);

        Transaction tx = graphDatabase.beginTx();

        try {

            switch (op) {
                case loadnetrunnerdb: loadNetrunnerDB(false); break;
                case updatenetrunnerdb: loadNetrunnerDB(true); break;
                case testnetrunnerdb: testNetrunnerDb(); break;
                case loadnetrunnerdbdeck: loadNetrunnerDbDeck(); break;
            }

            tx.success();
        } finally {
            tx.close();
        }

    }
    public static void main(String[] args) throws Exception {
        if (args[0].equals(PossibleOperations.deletedb)) {
            FileUtils.deleteRecursively(new File("netrunner.db"));
        }
        SpringApplication.run(Application.class, args);
    }

    public enum PossibleOperations {
        deletedb, loadnetrunnerdb, updatenetrunnerdb, testnetrunnerdb, loadnetrunnerdbdeck
    }

    public void loadNetrunnerDB(boolean merge) {
        Set<CardPack> allCardPacks = netrunnerDBBroker.readSets();
        int found = 0;
        for (CardPack cardPack : allCardPacks) {
            if ((!merge) || (cardPackRepository.findByCode(cardPack.code).equals(null))) {
                cardPackRepository.save(cardPack);
                System.out.println("Found pack: " + cardPack.toString());
                found++;
            }
        }
        System.out.println("Found new card packs: " + found);

        Set<Card> allCards = netrunnerDBBroker.readCards();
        found = 0;
        for (Card card : allCards) {
            if ((!merge) || (cardRepository.findByTitle(card.title).equals(null))) {
                cardRepository.save(card);
                System.out.println("Found card: " + card.toString());
                found++;
            }
        }
        System.out.println("Found new cards: " + found);
    }

    public void testNetrunnerDb() {
        CardPack whatset = cardPackRepository.findByName("Core Set");
        System.out.println(whatset.toString());
        Card whatcard = cardRepository.findByTitle("Account Siphon");
        System.out.println(whatcard.toString());
    }

    public void loadNetrunnerDbDeck() {
        netrunnerDBBroker.readDeck(21538);
    }
}
