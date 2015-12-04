package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.admin.AdminData;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Repository for admindata
 * Created by madarasz on 2015-12-03.
 */
public interface AdminDataRepository extends GraphRepository<AdminData>, RelationshipOperationsRepository<AdminData> {

    AdminData findByFunction(String function);

    @Query("MATCH (a:AdminData {function: 'denyUrls'}) RETURN a LIMIT 1")
    AdminData getDenyUrls();
}
