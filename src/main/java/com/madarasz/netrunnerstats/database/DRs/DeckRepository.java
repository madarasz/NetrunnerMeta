package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import java.util.List;

/**
 * Repository for deck nodes
 * Created by madarasz on 10/06/15.
 */
public interface DeckRepository extends GraphRepository<Deck>, RelationshipOperationsRepository<Deck> {
    Deck findByUrl(String url);

    @Query("MATCH (i:Card {faction_code: {0}})<-[:IDENTITY]-(d:Deck) return d")
    List<Deck> filterByFaction(String faction_code);

    @Query("MATCH (i:Card {title: {0}})<-[:IDENTITY]-(d:Deck) return d")
    List<Deck> filterByIdentity(String title);

    @Query("MATCH (i:Card {title: {0}})<-[:IDENTITY]-(d:Deck)<-[:IS_DECK]-(:Standing)" +
            "-[:IN_TOURNAMENT]->(:Tournament)-[:POOL]->(p:CardPack {name: {1}}) " +
            "RETURN d")
    List<Deck> filterByIdentityAndCardPool(String title, String cardpoolname);

    @Query("MATCH (i:Card {title: {0}})<-[:IDENTITY]-(d:Deck)<-[:IS_DECK]-(s:Standing {topdeck: true})" +
            "-[:IN_TOURNAMENT]->(:Tournament)-[:POOL]->(p:CardPack {name: {1}}) " +
            "RETURN d")
    List<Deck> filterTopByIdentityAndCardPool(String title, String cardpoolname);

    @Query("MATCH (i:Card {title: {0}})<-[:IDENTITY]-(d:Deck)<-[:IS_DECK]-(:Standing)" +
            "-[:IN_TOURNAMENT]->(:Tournament)-[:POOL]->(p:CardPack {name: {1}}) " +
            "RETURN COUNT(d)")
    int countByIdentityAndCardPool(String title, String cardpoolname);

    @Query("MATCH (i:Card {faction_code: {1}})<-[:IDENTITY]-(d:Deck)<-[:IS_DECK]-(s:Standing)" +
            "-[:IN_TOURNAMENT]->(:Tournament)-[:POOL]->(p:CardPack {name: {0}}) " +
            "RETURN COUNT(d)")
    int countByCardPoolAndFaction(String cardpoolname, String factionName);

    @Query("MATCH (i:Card {title: {0}})<-[:IDENTITY]-(d:Deck)<-[:IS_DECK]-(s:Standing {topdeck: true})" +
            "-[:IN_TOURNAMENT]->(:Tournament)-[:POOL]->(p:CardPack {name: {1}}) " +
            "RETURN COUNT(d)")
    int countTopByIdentityAndCardPool(String title, String cardpoolname);

    @Query("MATCH (i:Card {faction_code: {1}})<-[:IDENTITY]-(d:Deck)<-[:IS_DECK]-(s:Standing {topdeck: true})" +
            "-[:IN_TOURNAMENT]->(:Tournament)-[:POOL]->(p:CardPack {name: {0}}) " +
            "RETURN COUNT(d)")
    int countTopByCardPoolAndFaction(String cardpoolname, String factionName);

    int countByIdentity(Card card);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card {faction_code: {0}}) RETURN COUNT(d)")
    int countByFaction(String faction);

    @Query("MATCH (p:CardPack {cyclenumber: {0}})<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck) RETURN COUNT(d)")
    int countByCycle(int cycleNumber);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck) RETURN COUNT(d)")
    int countByCardpool(String cardpackName);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing {topdeck: true})-->(d:Deck) RETURN COUNT(d)")
    int countTopByCardpool(String cardpackName);

    @Query("MATCH (d:Deck) return d")
    List<Deck> getAllDecks();

    @Query("MATCH (c:Card {code: {0}})<--(d:Deck) RETURN COUNT(d)")
    int countByUsingCard(String cardcode);

    @Query("MATCH (c:Card {code: {0}})<--(d:Deck)<-[:IS_DECK]-(s:Standing {topdeck: true}) RETURN COUNT(d)")
    int countTopByUsingCard(String cardcode);

    @Query("MATCH (d:Deck) WHERE d.url =~ 'http://stimhack.*' RETURN COUNT(d)")
    int countStimhackDecks();

    @Query("MATCH (d:Deck) WHERE d.url =~ 'http://www.acoo.*' RETURN COUNT(d)")
    int countAcooDecks();

    @Query("MATCH (d:Deck) WHERE d.url =~ 'http://netrunnerdb.*' RETURN COUNT(d)")
    int countNetrunnerDBDecks();
}
