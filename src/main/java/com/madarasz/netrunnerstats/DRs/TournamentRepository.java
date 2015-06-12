package com.madarasz.netrunnerstats.DRs;

import com.madarasz.netrunnerstats.DOs.Tournament;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

/** Repository for tournaments
 * Created by madarasz on 2015-06-12.
 */
public interface TournamentRepository extends GraphRepository<Tournament>, RelationshipOperationsRepository<Tournament> {
    Tournament findById(int id);
    Tournament findByUrl(String url);
}
