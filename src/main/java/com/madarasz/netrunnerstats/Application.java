package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.database.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.database.DRs.DeckRepository;
import com.madarasz.netrunnerstats.brokers.AcooBroker;
import com.madarasz.netrunnerstats.brokers.NetrunnerDBBroker;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.template.Neo4jOperations;

/**
 * Main application class
 * Created by madarasz on 2015-06-08.
 */
@SpringBootApplication
@ConfigurationProperties(prefix = "maindb")
public class Application implements CommandLineRunner {

    @Configuration
    @EnableNeo4jRepositories(basePackages = "com.madarasz.netrunnerstats")
    static class ApplicationConfig extends Neo4jConfiguration {

        public ApplicationConfig() {
            setBasePackage("com.madarasz.netrunnerstats");
        }

        @Bean
        @Primary
        @ConfigurationProperties(prefix = "maindb")
        GraphDatabaseService graphDatabaseService() {
            return new GraphDatabaseFactory().newEmbeddedDatabase("netrunner.db");
        }

        @Bean
        @ConfigurationProperties(prefix = "testdb")
        GraphDatabaseService graphTestDatabaseService() {
            return new GraphDatabaseFactory().newEmbeddedDatabase("test.db");
        }
    }

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    NetrunnerDBBroker netrunnerDBBroker;

    @Autowired
    AcooBroker acooBroker;

    @Autowired
    GraphDatabase graphDatabase;

    @Autowired
    Neo4jOperations template;

    @Autowired
    Operations operations;

    @Autowired
    Statistics statistics;

    public void run(String... args) throws Exception {

        PossibleOperations op;
        if (args.length > 0) {
            op = PossibleOperations.valueOf(args[0]);
        } else {
            op = PossibleOperations.none;
        }

        Transaction tx = graphDatabase.beginTx();

        try {

            switch (op) {
                case loadnetrunnerdb: operations.loadNetrunnerDB(); break;
                case netrunnerdbloaddeck: operations.loadNetrunnerDbDeck(20162); break;
                case acooloaddeck: operations.loadAcooDeck(10890); break;
                case acooloadtournament: operations.loadAcooTournament(673); break;
                case acooloadtournamentdecks: operations.loadAcooTournamentDecks(673); break;
                case logdbcount: operations.logDBCount(); break;
                case acooloadpage: operations.loadAcooTournamentsFromUrl("http://www.acoo.net/tournament/set/breaker-bay/1/", true, true); break;
                case acooall:
//                    operations.cleanDB();
//                    operations.loadNetrunnerDB();
                    operations.loadAcooTournamentsFromUrl("http://www.acoo.net/tournament/set/data-and-destiny/1/", true, false);
                    operations.loadAcooTournamentsFromUrl("http://www.acoo.net/tournament/set/the-universe-of-tomorrow/1/", true, false);
                    operations.loadAcooTournamentsFromUrl("http://www.acoo.net/tournament/set/old-hollywood/1/", true, false);
                    operations.loadAcooTournamentsFromUrl("http://www.acoo.net/tournament/set/the-underway/1/", true, false);
//                    operations.loadAcooTournamentsFromUrl("http://www.acoo.net/anr-tournament-archive/1", true, false);
                    operations.logDBCount();
                    break;
                case checkdata: operations.checkDataValidity(); break;
                case getpackstats:
                    statistics.getPackStats("The Universe of Tomorrow", true);
                    break;
                case getpackmath: statistics.getPackMath("Near-Earth Hub: Broadcast Center", "Old Hollywood"); break;
                case cleandb: operations.cleanDB(); break;
                case stimhackdecks:
                    operations.loadStimhackDecks("http://stimhack.com/national-warsaw-poland-72-players/");
                    break;
                case stimhacktournament:
                    operations.loadStimhackTournament("http://stimhack.com/gnk-madison-wi-14-players/");
                    break;
                case stimhackpacktournament:
                    operations.loadStimhackPackTournaments("Old Hollywood");
                    operations.loadStimhackPackTournaments("The Universe of Tomorrow");
                    operations.loadStimhackPackTournaments("Data and Destiny");
                    operations.loadStimhackPackTournaments("Data & Destiny");
                    operations.loadStimhackPackTournaments("The Underway");
                    operations.logDBCount();
                    break;
                case resetstats:
                    operations.resetStats();
                    break;
                case mostusedcard:
                    statistics.getMostUsedCardsForCardPack("Data and Destiny");
                    break;
                case mostusedcardperpool:
                    statistics.getMostUsedCardsForCardpool("Data and Destiny");
                    break;
                case average:
                    statistics.getIdentityAverage("Near-Earth Hub: Broadcast Center", "Data and Destiny");
                    break;
            }

            tx.success();
        } finally {
            tx.close();
        }

    }
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    public enum PossibleOperations {
        cleandb, checkdata, loadnetrunnerdb, logdbcount,
        netrunnerdbloaddeck,
        acooloaddeck, acooloadtournament, acooloadtournamentdecks, acooloadpage, acooall,
        stimhackdecks, stimhacktournament, stimhackpacktournament,
        getpackstats, getpackmath, resetstats, mostusedcard, mostusedcardperpool, average,
        none
    }
}
