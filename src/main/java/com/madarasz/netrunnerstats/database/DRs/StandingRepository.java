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

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing {topdeck: true}) RETURN COUNT(s)")
    int countTopByCardPool(String cardpoolname);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing)-[:IS_IDENTITY]->(c:Card {side_code: {1}}) RETURN COUNT(s)")
    int countByCardPoolSidecode(String cardpoolname, String side_code);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing {topdeck: true})-[:IS_IDENTITY]->(c:Card {side_code: {1}}) RETURN COUNT(s)")
    int countTopByCardPoolSidecode(String cardpoolname, String side_code);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing)-[:IS_IDENTITY]->(c:Card {title: {1}}) RETURN COUNT(s)")
    int countByCardPoolId(String cardpoolname, String identity);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing {topdeck: true})-[:IS_IDENTITY]->(c:Card {title: {1}}) RETURN COUNT(s)")
    int countTopByCardPoolId(String cardpoolname, String identity);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing {topdeck: true})-[:IS_IDENTITY]->(c:Card {faction_code: {1}}) RETURN COUNT(s)")
    int countTopByCardPoolFaction(String cardpoolname, String faction);

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

    @Query("MATCH (t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing) WHERE t.url =~ 'http://stimhack.*' RETURN COUNT(s)")
    int countStimhackStandings();

    @Query("MATCH (t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing) WHERE t.url =~ 'http://www.acoo.*' RETURN COUNT(s)")
    int countAcooStandings();

    @Query("MATCH (s1:Standing)-[:IN_TOURNAMENT]->(t:Tournament)<-[:IN_TOURNAMENT]-(s2:Standing) " +
            "WHERE s2.rank = s1.rank AND s2.identity = s1.identity AND s1.id <> s2.id RETURN COUNT(s2)")
    int countDoubledStandings();

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing)-[:IS_IDENTITY]->(c:Card {side_code: {1}}) " +
            "RETURN c.title AS category, COUNT(*) as count, c.side_code AS side_code " +
            "ORDER BY side_code DESC, count DESC")
    List<StatCounts> getIdentityStatsByCardPoolSide(String cardpoolName, String sideCode);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing)-[:IS_IDENTITY]->(c:Card {side_code: {1}}) " +
            "RETURN c.faction_code AS category, COUNT(*) AS count, c.side_code AS side_code " +
            "ORDER BY side_code DESC, count DESC")
    List<StatCounts> getFactionStatsByCardPoolSide(String cardpoolName, String sideCode);

    @Query("MATCH (p:CardPack)<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing)-[:IS_IDENTITY]->(c:Card {side_code: {3}}) " +
            "WHERE p.name IN [{0}, {1}, {2}] " +
            "RETURN c.title AS category, COUNT(*) as count, c.side_code AS side_code ")
    List<StatCounts> getIdentityStatsBy3CardPoolSide(String cpName1, String cpName2, String cpName3, String sideCode);

    @Query("MATCH (p:CardPack)<-[:POOL]-(t:Tournament)<-[:IN_TOURNAMENT]-(s:Standing)-[:IS_IDENTITY]->(c:Card {side_code: {3}}) " +
            "WHERE p.name IN [{0}, {1}, {2}] " +
            "RETURN c.faction_code AS category, COUNT(*) AS count, c.side_code AS side_code ")
    List<StatCounts> getFactionStatsBy3CardPoolSide(String cpName1, String cpName2, String cpName3, String sideCode);
}
