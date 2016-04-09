package com.madarasz.netrunnerstats.database.DOs.stats;

import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 4/8/16.
 * Represents standing and deck counts for IDs.
 */
@NodeEntity
public class StandingDeckCountID extends StandingDeckCount {
    private String faction;

    public StandingDeckCountID(String title, String faction, int allStandingCount, int topStandingCount, int allDeckCount, int topDeckCount) {
        this.title = title;
        this.faction = faction;
        this.allStandingCount = allStandingCount;
        this.topStandingCount = topStandingCount;
        this.allDeckCount = allDeckCount;
        this.topDeckCount = topDeckCount;
    }

    public StandingDeckCountID() {

    }

    public String getFaction() {
        return faction;
    }
}
