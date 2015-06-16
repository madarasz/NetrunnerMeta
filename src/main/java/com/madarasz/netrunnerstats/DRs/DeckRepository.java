package com.madarasz.netrunnerstats.DRs;

import com.madarasz.netrunnerstats.DOs.Deck;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

import java.util.List;

/**
 * Repository for deck nodes
 * Created by madarasz on 10/06/15.
 */
public interface DeckRepository extends GraphRepository<Deck>, RelationshipOperationsRepository<Deck> {
    Deck findByUrl(String url);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card) WHERE (i.faction_code={0}) return d")
    List<Deck> filterByFaction(String faction_code);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card) WHERE (i.title={0}) return d")
    List<Deck> filterByIdentity(String title);

    @Query("MATCH (p:CardPack)<-[:UP_TO]-(d:Deck)-[:IDENTITY]->(i:Card) WHERE (i.title={0}) AND (p.name={1}) return d")
    List<Deck> filterByIdentityAndCardPool(String title, String cardpoolname);
}
