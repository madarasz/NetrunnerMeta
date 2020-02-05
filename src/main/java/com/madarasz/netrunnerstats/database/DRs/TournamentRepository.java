package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.Tournament;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

import java.util.Date;
import java.util.List;

/** Repository for tournaments
 * Created by madarasz on 2015-06-12.
 */
public interface TournamentRepository extends GraphRepository<Tournament>, RelationshipOperationsRepository<Tournament> {

    Tournament findById(int id);
    Tournament findByUrl(String url);

    @Query("MATCH (t:Tournament) return t")
    List<Tournament> getAllTournaments();

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(:Tournament) RETURN COUNT(p)")
    int countByCardpool(String cardpoolName);

    @Query("MATCH (t:Tournament) WHERE t.url =~ 'http://stimhack.*' RETURN COUNT(t)")
    int countStimhackTournaments();

    @Query("MATCH (t:Tournament) WHERE t.url =~ 'http://www.acoo.*' RETURN COUNT(t)")
    int countAcooTournaments();

    @Query("MATCH (t:Tournament) WHERE t.url =~ 'http://stimhack.*' RETURN MAX(t.date)")
    Date getLastStimhackTournamentDate();

    @Query("MATCH (t:Tournament) WHERE t.url =~ 'http://www.acoo.*' RETURN MAX(t.date)")
    Date getLastAcooTournamentDate();

    @Query("MATCH (t:Tournament)<-[:IN_TOURNAMENT]-(:Standing)-[:IS_DECK]->(:Deck {url: {0}}) RETURN t LIMIT 1")
    Tournament getTournamentByDeckUrl(String url);

    @Query("MATCH (p:CardPack {name: {0}})<-[:POOL]-(t:Tournament) RETURN DISTINCT t")
    List<Tournament> getTournamentsByCardpool(String cardpool);
}
