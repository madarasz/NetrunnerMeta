package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.CardUsageStat;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Repository for deck infos nodes
 * Created by madarasz on 2015-06-08.
 */
public interface CardUsageStatsRepository extends GraphRepository<CardUsageStat>,
        RelationshipOperationsRepository<CardUsageStat> {

    @Query("MATCH (c:CardUsageStat {cardpackname: {0}, cardpool: {1}}) RETURN c LIMIT 1")
    CardUsageStat findByCardPackName(String cardpackname, boolean cardpool);
}
