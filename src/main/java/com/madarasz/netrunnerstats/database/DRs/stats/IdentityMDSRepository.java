package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.IdentityMDS;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Created by madarasz on 11/14/15.
 */
public interface IdentityMDSRepository extends GraphRepository<IdentityMDS>, RelationshipOperationsRepository<IdentityMDS> {

    @Query("MATCH (i:IdentityMDS {dpname: {0}, identitytitle: {1}}) RETURN i LIMIT 1")
    IdentityMDS findByDpnameIdentitytitle(String dpname, String identitytitle);

}

