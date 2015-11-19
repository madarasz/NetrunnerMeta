package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.Standing;
import com.madarasz.netrunnerstats.database.DOs.result.StatCounts;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

import java.util.List;

/**
 * Repository for Standing node
 * Created by madarasz on 2015.08.10..
 */
public interface StandingRepository extends GraphRepository<Standing>, RelationshipOperationsRepository<Standing> {

    @Query("MATCH (t:Tournament {url: {0}})<-[:IN_TOURNAMENT]-(s:Standing) RETURN COUNT(s)")
    int countByTournamentURL(String tournamentURL);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing) RETURN COUNT(s)")
    int countByCardPool(String cardpoolname);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing {topdeck: TRUE})-[:IS_IDENTITY]->(c:Card) " +
            "RETURN c.title AS category, COUNT(*) as count, c.side_code AS side_code " +
            "ORDER BY side_code DESC, count DESC")
    List<StatCounts> getTopIdentityStatsByCardPool(String cardpoolName);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing {topdeck: TRUE})-[:IS_IDENTITY]->(c:Card) " +
            "RETURN c.faction_code AS category, COUNT(*) AS count, c.side_code AS side_code " +
            "ORDER BY side_code DESC, count DESC")
    List<StatCounts> getTopFactionStatsByCardPool(String cardpoolName);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing)-[:IS_IDENTITY]->(c:Card) " +
            "RETURN c.title AS category, COUNT(*) as count, c.side_code AS side_code " +
            "ORDER BY side_code DESC, count DESC")
    List<StatCounts> getIdentityStatsByCardPool(String cardpoolName);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing)-[:IS_IDENTITY]->(c:Card) " +
            "RETURN c.faction_code AS category, COUNT(*) AS count, c.side_code AS side_code " +
            "ORDER BY side_code DESC, count DESC")
    List<StatCounts> getFactionStatsByCardPool(String cardpoolName);

    @Query("MATCH (t:Tournament {url: {0}})<-[:IN_TOURNAMENT]-(s:Standing {rank: {1}})-[:IS_IDENTITY]->(c:Card {title: {2}}) " +
            "RETURN s LIMIT 1")
    Standing findByTournamentURLRankIdentity(String url, int rank, String identityname);

    @Query("MATCH (s:Standing) return s")
    List<Standing> getAllStanding();

    @Query("MATCH (s:Standing)-[:IS_DECK]->(d:Deck {url: {0}}) RETURN s LIMIT 1")
    Standing findByDeckUrl(String url);

    @Query("MATCH (t:Tournament {url: {0}})<-[:IN_TOURNAMENT]-(s:Standing) RETURN s")
    List<Standing> findByTournamentURL(String url);
}
