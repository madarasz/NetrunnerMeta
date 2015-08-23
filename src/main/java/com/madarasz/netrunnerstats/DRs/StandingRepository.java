package com.madarasz.netrunnerstats.DRs;

import com.madarasz.netrunnerstats.DOs.Standing;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Repository for Standing node
 * Created by madarasz on 2015.08.10..
 */
public interface StandingRepository extends GraphRepository<Standing>, RelationshipOperationsRepository<Standing> {

    @Query("MATCH (s:Standing)<-[:IN_TOURNAMENT]-(t:Tournament) WHERE (t.url={0}) RETURN COUNT(d)")
    int countByTournamentURL(String tournamentURL);
}
