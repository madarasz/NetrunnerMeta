package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.admin.BlogEntry;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

import java.util.List;

/**
 * Repository for admindata
 * Created by madarasz on 2015-12-03.
 */
public interface BlogRepository extends GraphRepository<BlogEntry>, RelationshipOperationsRepository<BlogEntry> {

    @Query("MATCH (b:BlogEntry) RETURN b ORDER BY b.date DESC")
    List<BlogEntry> getAll();

    @Query("MATCH (b:BlogEntry {url: {0}}) RETURN b")
    BlogEntry getbyURL(String url);
}
