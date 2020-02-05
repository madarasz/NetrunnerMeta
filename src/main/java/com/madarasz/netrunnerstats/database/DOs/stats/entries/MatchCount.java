package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Represents match counts for IDs.
 */
@NodeEntity
public class MatchCount {
    @GraphId
    private Long id;
    protected int allMatchCount;
    protected int winMatchCount;
    protected int timedWinMatchCount;
    protected int tieMatchCount;

    public MatchCount() {
    }

    public MatchCount(int allMatchCount, int winMatchCount, int timedWinMatchCount, int tieMatchCount) {
        this.allMatchCount = allMatchCount;
        this.winMatchCount = winMatchCount;
        this.timedWinMatchCount = timedWinMatchCount;
        this.tieMatchCount = tieMatchCount;
    }

    public int getAllMatchCount() {
        return allMatchCount;
    }

    public int getWinMatchCount() {
        return winMatchCount;
    }

    public int getTimedWinMatchCount() {
        return timedWinMatchCount;
    }

    public int getTieMatchCount() {
        return tieMatchCount;
    }
}
