package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * Created by madarasz on 4/8/16.
 * Represents standing and deck counts for IDs.
 */
@NodeEntity
public class StandingDeckCountID extends StandingDeckCount {
    private String faction;
    @RelatedTo(type = "MATCHES") private @Fetch MatchCount matches;

    public StandingDeckCountID(String title, String faction, int allStandingCount, int topStandingCount,
                               int allDeckCount, int topDeckCount, MatchCount matches) {
        this.title = title;
        this.faction = faction;
        this.allStandingCount = allStandingCount;
        this.topStandingCount = topStandingCount;
        this.allDeckCount = allDeckCount;
        this.topDeckCount = topDeckCount;
        this.matches = matches;
    }

    public StandingDeckCountID() {

    }

    public String getFaction() {
        return faction;
    }

    public MatchCount getMatches() {
        return matches;
    }
}
