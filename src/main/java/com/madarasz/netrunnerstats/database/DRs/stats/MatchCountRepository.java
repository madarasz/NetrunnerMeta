package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.MatchCount;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Repository for match infos nodes
 * Created by madarasz on 2017-10-25.
 */
public interface MatchCountRepository extends GraphRepository<MatchCount>,
        RelationshipOperationsRepository<MatchCount> {

    @Query("MATCH (c:MatchCount)<-[:MATCHES]-(s:StandingDeckCount {title: {1}})<-[:IDS]-(t:TournamentDrilldown {packTitle: {0}}) " +
            "RETURN c LIMIT 1")
    MatchCount findByCardPackID(String cardpackname, String idtitle);
}
