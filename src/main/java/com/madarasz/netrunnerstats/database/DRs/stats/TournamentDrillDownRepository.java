package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.TournamentDrilldown;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Created by madarasz on 11/14/15.
 * Data repository class for DPIndetities
 */
public interface TournamentDrillDownRepository extends GraphRepository<TournamentDrilldown>,
        RelationshipOperationsRepository<TournamentDrilldown> {

    @Query("MATCH (t:TournamentDrilldown {packTitle: {0}, sideCode: {1}}) RETURN t LIMIT 1")
    TournamentDrilldown findByCardpoolSidecode(String cardpool, String sidecode);

}

