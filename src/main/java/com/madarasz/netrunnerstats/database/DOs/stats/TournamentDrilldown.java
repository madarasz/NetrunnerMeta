package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardAverage;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;
import com.madarasz.netrunnerstats.helper.AverageDigest;
import org.springframework.data.neo4j.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 4/7/16.
 * Represents all the data in tournament drilldown.
 */
@NodeEntity
public class TournamentDrilldown {
    @GraphId
    private Long id;
    @Indexed private String packTitle;
    @Indexed private String sideCode;
    private int cycleNum;
    private int dpNum;
    private int allStandingCount;
    private int topStandingCount;
    private int allDeckCount;
    private int topDeckCount;
    @RelatedTo(type = "FACTIONS") private @Fetch Set<StandingDeckCount> factions;
    @RelatedTo(type = "IDS") private @Fetch Set<StandingDeckCountID> ids;
    @RelatedTo(type = "ICE") private @Fetch Set<CardAverage> ice;   // ice or breakers
    @RelatedTo(type = "CARDS") private @Fetch Set<CardUsage> mostUsedCards;

    public TournamentDrilldown() {
        this.factions = new HashSet<>();
        this.ids = new HashSet<>();
        this.ice = new HashSet<>();
        this.mostUsedCards = new HashSet<>();
    }

    public TournamentDrilldown(String packTitle, String sideCode, int cycleNum, int dpNum,
                               int allStandingCount, int topStandingCount, int allDeckCount, int topDeckCount) {
        this.packTitle = packTitle;
        this.sideCode = sideCode;
        this.cycleNum = cycleNum;
        this.dpNum = dpNum;
        this.allStandingCount = allStandingCount;
        this.topStandingCount = topStandingCount;
        this.allDeckCount = allDeckCount;
        this.topDeckCount = topDeckCount;
        this.factions = new HashSet<>();
        this.ids = new HashSet<>();
        this.ice = new HashSet<>();
        this.mostUsedCards = new HashSet<>();
    }

    public String getPackTitle() {
        return packTitle;
    }

    public String getSideCode() {
        return sideCode;
    }

    public int getCycleNum() {
        return cycleNum;
    }

    public int getDpNum() {
        return dpNum;
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

    public Set<StandingDeckCount> getFactions() {
        return factions;
    }

    public Set<StandingDeckCountID> getIds() {
        return ids;
    }

    public List<CardAverage> getIce() {
        AverageDigest averageDigest = new AverageDigest();
        return averageDigest.getICESortedAverages(ice);
    }

    public Set<CardUsage> getMostUsedCards() {
        return mostUsedCards;
    }

    public void addFaction(StandingDeckCount standingDeckCount) {
        this.factions.add(standingDeckCount);
    }

    public void addId(StandingDeckCountID standingDeckCountID) {
        this.ids.add(standingDeckCountID);
    }

    public void addIce(List<CardAverage> cardAverage) {
        this.ice.addAll(cardAverage);
    }

    public void addMostUsedCard(List<CardUsage> cardUsage) {
        this.mostUsedCards.addAll(cardUsage);
    }
}
