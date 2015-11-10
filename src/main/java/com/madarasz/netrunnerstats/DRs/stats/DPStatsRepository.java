package com.madarasz.netrunnerstats.DRs.stats;

import com.madarasz.netrunnerstats.DOs.stats.DPStatistics;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Created by madarasz on 11/10/15.
 * Repository for Data Pack statistics
 */
public interface DPStatsRepository extends GraphRepository<DPStatistics>, RelationshipOperationsRepository<DPStatistics> {

    DPStatistics findByDpname(String DPname);
}
