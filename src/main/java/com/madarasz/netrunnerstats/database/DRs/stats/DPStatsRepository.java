package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.DPStatistics;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

import java.util.List;

/**
 * Created by madarasz on 11/10/15.
 * Repository for Data Pack statistics
 */
public interface DPStatsRepository extends GraphRepository<DPStatistics>, RelationshipOperationsRepository<DPStatistics> {

    @Query("MATCH (d:DPStatistics {packTitle: {0}}) RETURN d LIMIT 1")
    DPStatistics findByPackTitle(String packTitle);

    @Query("MATCH (d:DPStatistics) RETURN d")
    List<DPStatistics> findAllStats();
}
