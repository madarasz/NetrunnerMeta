package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.CardStat;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Repository for card pack nodes
 * Created by madarasz on 2015-06-08.
 */
public interface CardStatRepository extends GraphRepository<CardStat>,
        RelationshipOperationsRepository<CardStat> {

    @Query("MATCH (c:CardStat {title: {0}}) RETURN c LIMIT 1")
    CardStat findbyTitle(String cardTitle);
}
