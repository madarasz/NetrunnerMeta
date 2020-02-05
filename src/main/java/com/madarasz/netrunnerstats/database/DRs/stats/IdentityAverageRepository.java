package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.IdentityAverage;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Created by madarasz on 11/10/15.
 * Repository for Identity Average statistics
 */
public interface IdentityAverageRepository extends GraphRepository<IdentityAverage>,
        RelationshipOperationsRepository<IdentityAverage> {

    @Query("MATCH (i:IdentityAverage {identity: {0}, cardpool: {1}}) RETURN i LIMIT 1")
    IdentityAverage findIdentityCardPool(String identity, String cardpool);

}
