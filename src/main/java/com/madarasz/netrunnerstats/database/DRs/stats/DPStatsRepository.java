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

    @Query("MATCH (d:DPStatistics {dpname: {0}, top: true}) RETURN d LIMIT 1")
    DPStatistics findByDpnameOnlyTop(String DPname);

    @Query("MATCH (d:DPStatistics {dpname: {0}, top: false}) RETURN d LIMIT 1")
    DPStatistics findByDpnameAll(String DPname);

    @Query("MATCH (d:DPStatistics) RETURN d")
    List<DPStatistics> findAllStats();
}
