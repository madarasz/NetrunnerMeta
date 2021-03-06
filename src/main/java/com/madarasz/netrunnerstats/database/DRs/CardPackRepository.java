package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.CardPack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

import java.util.List;

/**
 * Repository for card pack nodes
 * Created by madarasz on 2015-06-08.
 */
public interface CardPackRepository extends GraphRepository<CardPack>, RelationshipOperationsRepository<CardPack> {

    CardPack findByName(String name);

    CardPack findByCode(String code);

    CardPack findByCyclenumberAndNumber(int cyclenumber, int number);

    @Query("MATCH (p:CardPack)<-[:POOL]-(:Tournament) RETURN DISTINCT p")
    List<CardPack> findWithStandings();

    @Query("MATCH (p:CardPack) WHERE p.cyclenumber > 0 AND p.name NOT LIKE '%MWL%' RETURN DISTINCT p " +
            "ORDER BY p.cyclenumber ASC, p.number ASC")
    List<CardPack> getSortedPacks();

    // TODO: slow, REPLACE
    @Query("MATCH (p:CardPack {cyclenumber: {0}})<-[:IN_SET]-(:Card)<-[:HAS_CARD]-(:Deck) WITH DISTINCT p " +
            "WHERE p.name <> 'D&D, post-MWL' " + // post-MWL hack
            "RETURN p.name ORDER BY p.number ASC")
    List<String> getSortedPackNamesInCycle(int cycleNumber);

    // has at least one card in a deck
    @Query("MATCH (p:CardPack)<-[:IN_SET]-(:Card)-[:HAS_CARD]-(:Deck) WITH DISTINCT p " +
            "RETURN p.name ORDER BY p.cyclenumber ASC, p.number ASC")
    List<String> getSortedPackNames();
}
