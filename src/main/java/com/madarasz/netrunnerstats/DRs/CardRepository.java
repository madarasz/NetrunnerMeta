package com.madarasz.netrunnerstats.DRs;

import com.madarasz.netrunnerstats.DOs.Card;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by madarasz on 2015-06-08.
 */
public interface CardRepository extends GraphRepository<Card>, RelationshipOperationsRepository<Card> {

    Card findByCode(String code);
    Card findByTitle(String title);

    Iterable<Card> findByCardPackCode(String code);
}
