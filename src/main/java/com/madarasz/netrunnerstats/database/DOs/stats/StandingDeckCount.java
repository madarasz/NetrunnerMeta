package com.madarasz.netrunnerstats.database.DOs.stats;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by imadaras on 4/8/16.
 * Represents standing and deck counts for IDs or factions.
 */
@NodeEntity
public class StandingDeckCount {
    @GraphId
    private Long id;
    protected String title;
    protected int allStandingCount;
    protected int topStandingCount;
    protected int allDeckCount;
    protected int topDeckCount;

    public StandingDeckCount() {
    }

    public StandingDeckCount(String title, int allStandingCount, int topStandingCount, int allDeckCount, int topDeckCount) {
        this.title = title;
        this.allStandingCount = allStandingCount;
        this.topStandingCount = topStandingCount;
        this.allDeckCount = allDeckCount;
        this.topDeckCount = topDeckCount;
    }

    public String getTitle() {
        return title;
    }

    public int getAllStandingCount() {
        return allStandingCount;
    }

    public int getTopStandingCount() {
        return topStandingCount;
    }

    public int getAllDeckCount() {
        return allDeckCount;
    }

    public int getTopDeckCount() {
        return topDeckCount;
    }
}
