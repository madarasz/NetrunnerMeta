package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.CardCycle;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Repository for card pack nodes
 * Created by madarasz on 2015-06-08.
 */
public interface CardCycleRepository extends GraphRepository<CardCycle>, RelationshipOperationsRepository<CardCycle> {

    CardCycle findByName(String name);

    CardCycle findByCode(String code);

    CardCycle findByCyclenumber(int cyclenumber);

}
