package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CountDeckStands;
import org.springframework.data.neo4j.annotation.*;

import java.util.*;

/**
 * Created by madarasz on 2015.11.08..
 * Output data for Data Pack statistics on factions.
 * Used for factions over time.
 */
@NodeEntity
public class DPStatistics {
    @GraphId
    private Long id;
    @Indexed private String packTitle;
    private int dpNum;
    private int cycleNum;
    private int runnerAllStandingCount;
    private int corpAllStandingCount;
    private int runnerTopStandingCount;
    private int corpTopStandingCount;
    @RelatedTo(type = "RFACTION") private @Fetch Set<CountDeckStands> runners;
    @RelatedTo(type = "CFACTION") private @Fetch Set<CountDeckStands> corps;

    public DPStatistics() {
        this.runners = new HashSet<>();
        this.corps = new HashSet<>();
    }

    public DPStatistics(String packTitle, int dpNum, int cycleNum, int runnerAllStandingCount, int corpAllStandingCount, int runnerTopStandingCount, int corpTopStandingCount) {
        this.packTitle = packTitle;
        this.dpNum = dpNum;
        this.cycleNum = cycleNum;
        this.runnerAllStandingCount = runnerAllStandingCount;
        this.corpAllStandingCount = corpAllStandingCount;
        this.runnerTopStandingCount = runnerTopStandingCount;
        this.corpTopStandingCount = corpTopStandingCount;
        this.runners = new HashSet<>();
        this.corps = new HashSet<>();
    }

    public String getPackTitle() {
        return packTitle;
    }

    public int getDpNum() {
        return dpNum;
    }

    public int getCycleNum() {
        return cycleNum;
    }

    public int getRunnerAllStandingCount() {
        return runnerAllStandingCount;
    }

    public int getCorpAllStandingCount() {
        return corpAllStandingCount;
    }

    public int getRunnerTopStandingCount() {
        return runnerTopStandingCount;
    }

    public int getCorpTopStandingCount() {
        return corpTopStandingCount;
    }

    public void addRunnerFaction(String faction, int allStandingCount, int topStandingCount) {
        runners.add(new CountDeckStands(faction, allStandingCount, topStandingCount));
    }

    public void addCorpFaction(String faction, int allStandingCount, int topStandingCount) {
        corps.add(new CountDeckStands(faction, allStandingCount, topStandingCount));
    }

    public Set<CountDeckStands> getRunners() {
        return runners;
    }

    public Set<CountDeckStands> getCorps() {
        return corps;
    }

}