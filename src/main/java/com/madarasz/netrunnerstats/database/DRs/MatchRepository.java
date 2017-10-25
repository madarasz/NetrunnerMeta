package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.Match;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

import java.util.List;

/**
 * Repository for card nodes
 * Created by madarasz on 2015-06-08.
 */
public interface MatchRepository extends GraphRepository<Match>, RelationshipOperationsRepository<Match> {

    @Query("MATCH (m:Match)-[:IN_TOURNAMENT]->(:Tournament)-[:POOL]->(p:CardPack {name: {0}}) RETURN m")
    List<Match> findForPool(String cardpoolname);

    @Query("MATCH (c:Card {title: {1}})<--(m:Match)-[:IN_TOURNAMENT]->(:Tournament)-[:POOL]->(p:CardPack {name: {0}}) " +
            "RETURN m")
    List<Match> findForPoolID(String cardpoolname, String idtitle);

    @Query("MATCH (m:Match)-[:IN_TOURNAMENT]->(:Tournament)-[:POOL]->(p:CardPack {name: {0}}) RETURN COUNT(m)")
    int countForPool(String cardpoolname);

    @Query("MATCH (m:Match)-[:IN_TOURNAMENT]->(t:Tournament {url: {0}}) RETURN m")
    List<Match> findForTournamentURL(String tournamentURL);
}
