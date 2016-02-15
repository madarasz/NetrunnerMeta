package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.result.CardCounts;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import java.util.List;

/**
 * Repository for card nodes
 * Created by madarasz on 2015-06-08.
 */
public interface CardRepository extends GraphRepository<Card>, RelationshipOperationsRepository<Card> {

    Card findByCode(String code);

    // IgnoreCase does not work, workaround
    @Query("MATCH (n:Card) WHERE (UPPER(n.title)=UPPER({0})) RETURN n LIMIT 1")
    Card findByTitle(String title);

    @Query("MATCH (n:Card {type_code: 'identity'}) return n")
    List<Card> findIdentities();

    @Query("MATCH (n:Card {type_code: 'identity', side_code: {0}}) return n")
    List<Card> findIdentitiesBySide(String side_code);

    @Query("MATCH (c:Card {type_code: 'identity', side_code: {0}})<-[:IS_IDENTITY]-(:Standing {topdeck: true}) " +
            "RETURN DISTINCT c.faction_code")
    List<String> findFactionTitlesBySide(String side_code);

    @Query("MATCH (p:CardPack {name: {0}})<-[:IN_SET]-(c:Card) RETURN c")
    List<Card> findByCardPackName(String code);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<-[:IN_TOURNAMENT]-(:Standing {topdeck: true})" +
            "-[:IS_DECK]->(:Deck)-[:HAS_CARD]->(c:Card {side_code: {1}})-[:IN_SET]->(cp:CardPack) " +
            "RETURN c.title AS title, COUNT(c) AS count, cp.name AS cardpack, c.faction_code AS faction " +
            "ORDER BY count DESC LIMIT 30")
    List<CardCounts> findMostPopularCardsByCardPack(String cardpackname, String sidecode);

    @Query("MATCH (p:CardPack)<-[:POOL]-(:Tournament)<-[:IN_TOURNAMENT]-(:Standing {topdeck: true})" +
            "-[:IS_DECK]->(:Deck)-[:HAS_CARD]->(c:Card {side_code: {3}})-[:IN_SET]->(cp:CardPack) " +
            "WHERE p.name IN [{0}, {1}, {2}] " +
            "RETURN c.title AS title, COUNT(c) AS count, cp.name AS cardpack, c.faction_code AS faction " +
            "ORDER BY count DESC LIMIT 30")
    List<CardCounts> findMostPopularCardsBy3CardPack(String cardpackname1, String cardpackname2, String cardpackname3, String sidecode);
}
