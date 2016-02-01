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

    @Query("MATCH (p:CardPack {cyclenumber: {0}})<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck) RETURN COUNT(DISTINCT d)")
    int countByCycleNum(int cycleNumber);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck) RETURN COUNT(DISTINCT d)")
    int countByCardpool(String cardpackName);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing {topdeck: true})-->(d:Deck) RETURN COUNT(DISTINCT d)")
    int countTopByCardpool(String cardpackName);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck)-[:IDENTITY]->(:Card {side_code: {1}}) RETURN COUNT(d)")
    int countByCardpoolAndSide(String cardpackName, String side);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing {topdeck: true})-->(d:Deck)-[:IDENTITY]->(:Card {side_code: {1}}) RETURN COUNT(d)")
    int countTopByCardpoolAndSide(String cardpackName, String side);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck)-->(:Card {title: {1}}) RETURN COUNT(DISTINCT d)")
    int countByCardpoolUsingCard(String cardpackName, String cardTitle);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck)-->(:Card {title: {1}}) RETURN DISTINCT d")
    List<Deck> findByCardpoolUsingCard(String cardpackName, String cardTitle);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck)-[:IDENTITY]->(:Card {side_code: {1}}) " +
            "RETURN DISTINCT d")
    List<Deck> findByCardpoolAndSide(String cardpackName, String sidecode);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<--(s:Standing)-->(d:Deck)-->(:Card {title: {1}}) " +
            "WITH s, t, d ORDER BY (1000 * s.rank / t.playerNumber) ASC LIMIT 10 RETURN d")
    List<Deck> findBestByCardpoolUsingCard(String cardpackName, String cardTitle);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck)-->(:Card {title: {1}}) " +
            "WHERE (d:Deck)-->(:Card {title: {2}}) RETURN COUNT(DISTINCT d)")
    int countByCardpoolUsingCardBoth(String cardpackName, String cardTitle, String cardTitle2);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck) " +
            "WHERE (d:Deck)-->(:Card {title: {2}}) OR (d:Deck)-->(:Card {title: {1}}) RETURN COUNT(DISTINCT d)")
    int countByCardpoolUsingCardOneOf(String cardpackName, String cardTitle, String cardTitle2);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<--(:Standing {topdeck: true})-->(d:Deck)-->(:Card {title: {1}}) RETURN COUNT(DISTINCT d)")
    int countTopByCardpoolUsingCard(String cardpackName, String cardTitle);

    @Query("MATCH (d:Deck) return d")
    List<Deck> getAllDecks();

    @Query("MATCH (c:Card {code: {0}})<--(d:Deck) RETURN COUNT(DISTINCT d)")
    int countByUsingCard(String cardcode);

    @Query("MATCH (c:Card {code: {0}})<--(d:Deck)<-[:IS_DECK]-(s:Standing {topdeck: true}) RETURN COUNT(d)")
    int countTopByUsingCard(String cardcode);

    @Query("MATCH (d:Deck) WHERE d.url =~ 'http://stimhack.*' RETURN COUNT(d)")
    int countStimhackDecks();

    @Query("MATCH (d:Deck) WHERE d.url =~ 'http://www.acoo.*' RETURN COUNT(d)")
    int countAcooDecks();

    @Query("MATCH (d:Deck) WHERE d.url =~ 'http://netrunnerdb.*' RETURN COUNT(d)")
    int countNetrunnerDBDecks();

    @Query("MATCH (:Tournament {url: {0}})<--(:Standing)-->(d:Deck) RETURN d")
    List<Deck> findByTournamentUrl(String url);

    @Query("MATCH (d:Deck) WHERE NOT (d)<--(:Standing) RETURN d")
    List<Deck> getAllDecksWithoutRel();
}
