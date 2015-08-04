package com.madarasz.netrunnerstats.DRs;

import com.madarasz.netrunnerstats.DOs.CardPack;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Repository for card pack nodes
 * Created by madarasz on 2015-06-08.
 */
public interface CardPackRepository extends GraphRepository<CardPack>, RelationshipOperationsRepository<CardPack> {

    CardPack findByName(String name);

    CardPack findByCode(String code);

    CardPack findByCyclenumberAndNumber(int cyclenumber, int number);
}
