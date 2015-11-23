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

    @Query("MATCH (p:CardPack {name: {0}})<-[:IN_SET]-(c:Card) RETURN c")
    List<Card> findByCardPackName(String code);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament)<-[:IN_TOURNAMENT]-(:Standing)-[:IS_DECK]->(:Deck)-[:HAS_CARD]->(c:Card {side_code: {1}})-[:IN_SET]->(cp:CardPack) " +
            "RETURN c.title AS title, COUNT(c) AS count, cp.name AS cardpack ORDER BY count DESC LIMIT 10")
    List<CardCounts> findMostPopularCardsByCardPack(String cardpackname, String sidecode);
}
