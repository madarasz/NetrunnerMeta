package com.madarasz.netrunnerstats.database.DRs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.DeckInfos;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Repository for deck infos nodes
 * Created by madarasz on 2015-06-08.
 */
public interface DeckInfosRepository extends GraphRepository<DeckInfos>,
        RelationshipOperationsRepository<DeckInfos> {

    @Query("MATCH (d:DeckInfos {cardpoolname: {0}, identitytitle: {1}}) RETURN d LIMIT 1")
    DeckInfos findByCardpoolIdentityname(String cardpool, String identity);
}
