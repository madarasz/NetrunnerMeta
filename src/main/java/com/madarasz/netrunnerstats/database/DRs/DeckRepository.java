package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import java.util.List;

/**
 * Repository for deck nodes
 * Created by madarasz on 10/06/15.
 */
// TODO: erase unnecessary edge types
public interface DeckRepository extends GraphRepository<Deck>, RelationshipOperationsRepository<Deck> {
    Deck findByUrl(String url);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card) WHERE (i.faction_code={0}) return d")
    List<Deck> filterByFaction(String faction_code);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card) WHERE (i.title={0}) return d")
    List<Deck> filterByIdentity(String title);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card), (p:CardPack)<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck) " +
            "WHERE (i.title={0}) AND (p.name={1}) return d")
    List<Deck> filterByIdentityAndCardPool(String title, String cardpoolname);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card), (p:CardPack)<-[:POOL]-(:Tournament)<--(s:Standing)-->(d:Deck) " +
            "WHERE (i.title={0}) AND (p.name={1}) AND (s.topdeck=true) return d")
    List<Deck> filterTopByIdentityAndCardPool(String title, String cardpoolname);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card), (p:CardPack)<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck) " +
            "WHERE (i.title={0}) AND (p.name={1}) RETURN COUNT(d)")
    int countByIdentityAndCardPool(String title, String cardpoolname);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card), (p:CardPack)<-[:POOL]-(:Tournament)<--(s:Standing)-->(d:Deck) " +
            "WHERE (i.title={0}) AND (p.name={1}) AND (s.topdeck=true) RETURN COUNT(d)")
    int countTopByIdentityAndCardPool(String title, String cardpoolname);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card), (p:CardPack)<-[:POOL]-(:Tournament)<--(s:Standing)-->(d:Deck) WHERE (i.title={0}) AND (p.cyclenumber={1}) return d")
    List<Deck> filterByIdentityAndCardCycle(String title, int cyclenumber);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card), (p:CardPack)<-[:POOL]-(:Tournament)<--(s:Standing)-->(d:Deck) WHERE (i.title={0}) AND (p.cyclenumber={1}) AND (s.topdeck=true) return d")
    List<Deck> filterTopByIdentityAndCardCycle(String title, int cyclenumber);

    @Query("MATCH (d:Deck) return d")
    List<Deck> getAllDecks();

    int countByIdentity(Card card);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card) WHERE (i.faction_code={0}) RETURN COUNT(d)")
    int countByFaction(String faction);

    @Query("MATCH (p:CardPack)<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck) WHERE (p.cyclenumber={0}) RETURN COUNT(d)")
    int countByCycle(int cycleNumber);

    @Query("MATCH (p:CardPack)<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck) WHERE (p.name={0}) RETURN COUNT(d)")
    int countByCardpool(String cardpackName);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card), (p:CardPack)<-[:POOL]-(:Tournament)<--(:Standing)-->(d:Deck) WHERE (p.name={0}) AND (i.title={1}) RETURN COUNT(d)")
    int countByCardPackAndIdentity(String cardpackName, String identityName);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card), (p:CardPack)<-[:POOL]-(:Tournament)<--(s:Standing)-->(d:Deck) WHERE (p.name={0}) AND (i.title={1}) AND (s.topdeck=true) RETURN COUNT(d)")
    int countTopByCardPackAndIdentity(String cardpackName, String identityName);

    @Query("MATCH (d:Deck)-[:IDENTITY]->(i:Card), (p:CardPack)<-[:POOL]-(:Tournament)<--(s:Standing)-->(d:Deck) WHERE (p.name={0}) AND (i.faction_code={1}) AND (s.topdeck=true) RETURN COUNT(d)")
    int countTopByCardPackAndFaction(String cardpackName, String factionName);
}
