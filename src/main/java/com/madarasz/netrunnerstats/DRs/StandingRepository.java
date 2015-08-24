package com.madarasz.netrunnerstats.DRs;

import com.madarasz.netrunnerstats.DOs.Standing;
import com.madarasz.netrunnerstats.DOs.result.StatCounts;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

import java.util.List;

/**
 * Repository for Standing node
 * Created by madarasz on 2015.08.10..
 */
public interface StandingRepository extends GraphRepository<Standing>, RelationshipOperationsRepository<Standing> {

    @Query("MATCH (s:Standing)<-[:IN_TOURNAMENT]-(t:Tournament) WHERE (t.url={0}) RETURN COUNT(d)")
    int countByTournamentURL(String tournamentURL);

    @Query("MATCH (p:CardPack)<-[:POOL]-(t:Tournament)-[:IS_STANDING]->(s:Standing)-[:IS_IDENTITY]->(c:Card) WHERE (p.name={0}) AND (s.topdeck=TRUE) RETURN c as identity, COUNT(*) as count ORDER BY count DESC")
    List<StatCounts> getTopIdentityStatsByCardPool(String cardpoolName);
}
