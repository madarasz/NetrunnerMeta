package com.madarasz.netrunnerstats.database.DRs;

import com.madarasz.netrunnerstats.database.DOs.Tournament;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

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
}
