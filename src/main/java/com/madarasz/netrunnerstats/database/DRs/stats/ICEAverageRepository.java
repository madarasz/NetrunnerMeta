package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.ICEAverage;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Created by madarasz on 01/02/16.
 * Repository for Identity Average statistics
 */
public interface ICEAverageRepository extends GraphRepository<ICEAverage>,
        RelationshipOperationsRepository<ICEAverage> {

    @Query("MATCH (i:ICEAverage {sidecode: {0}, cardpool: {1}}) RETURN i LIMIT 1")
    ICEAverage findBySidecodeCardpool(String sidecode, String cardpool);

}
