package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madadarasz on 2015.11.08..
 * For counting statistics on deck and standings numbers
 */
@NodeEntity
public class CountDeckStands {
    @GraphId
    private Long id;
    private String faction;
    private int allStandingCount;
    private int topStandingCount;

    public CountDeckStands() {
    }

    public CountDeckStands(String faction, int allStandingCount, int topStandingCount) {
        this.faction = faction;
        this.allStandingCount = allStandingCount;
        this.topStandingCount = topStandingCount;
    }

    public String getFaction() {
        return faction;
    }

    public int getAllStandingCount() {
        return allStandingCount;
    }

    public int getTopStandingCount() {
        return topStandingCount;
    }
}
