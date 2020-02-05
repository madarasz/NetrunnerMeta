package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.CardPoolStats;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

import java.util.List;

/**
 * Repository for card pack nodes
 * Created by madarasz on 2015-06-08.
 */
public interface CardPoolStatsRepository extends GraphRepository<CardPoolStats>,
        RelationshipOperationsRepository<CardPoolStats> {

    // there is only one
    @Query("MATCH (c:CardPoolStats) RETURN c LIMIT 1")
    CardPoolStats find();

    @Query("MATCH (c:CardPool) RETURN c.title ORDER BY c.cyclenumber DESC, c.dpnumber DESC")
    List<String> getCardPoolNames();
}
