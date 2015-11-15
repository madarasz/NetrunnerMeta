package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.stats.CardPoolStats;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Repository for card pack nodes
 * Created by madarasz on 2015-06-08.
 */
public interface CardPoolStatsRepository extends GraphRepository<CardPoolStats>,
        RelationshipOperationsRepository<CardPoolStats> {

    // there is only one
    @Query("MATCH (c:CardPoolStats) RETURN c LIMIT 1")
    CardPoolStats find();
}
