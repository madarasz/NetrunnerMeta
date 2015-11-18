package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.DPIdentities;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Created by madarasz on 11/14/15.
 * Data repository class for DPIndetities
 */
public interface DPIdentitiesRepository extends GraphRepository<DPIdentities>,
        RelationshipOperationsRepository<DPIdentities> {

    @Query("MATCH (d:DPIdentities {cardpool: {0}, sidecode: {1}}) RETURN d LIMIT 1")
    DPIdentities findByCardpoolSidecode(String cardpool, String sidecode);

}

