package com.madarasz.netrunnerstats.DRs;

import com.madarasz.netrunnerstats.DOs.Deck;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/**
 * Repository for deck nodes
 * Created by madarasz on 10/06/15.
 */
public interface DeckRepository extends GraphRepository<Deck>, RelationshipOperationsRepository<Deck> {
    Deck findByUrl(String url);
}
