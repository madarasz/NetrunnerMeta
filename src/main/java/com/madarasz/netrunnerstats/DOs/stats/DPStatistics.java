package com.madarasz.netrunnerstats.DOs.stats;

import com.madarasz.netrunnerstats.DOs.stats.entries.CountDeckStands;
import com.madarasz.netrunnerstats.helper.CountDeckStatsComparator;
import org.springframework.data.neo4j.annotation.*;

import java.util.*;

/**
 * Created by madarasz on 2015.11.08..
 * Output data for Data Pack statistics on factions and indentities
 */
@NodeEntity
public class DPStatistics {
    @GraphId
    private Long id;
    @Indexed(unique=true) private String dpname; // TODO: actually link to Card Pack
    private int decknum;
    private int statnum;
    @RelatedTo(type = "RFACTION") private @Fetch Set<CountDeckStands> runnerFactions;
    @RelatedTo(type = "RIDENTITY") private @Fetch Set<CountDeckStands> runnerIdentities;
    @RelatedTo(type = "CFACTION") private @Fetch Set<CountDeckStands> corpFactions;
    @RelatedTo(type = "CIDENTITY") private @Fetch Set<CountDeckStands> corpIdentities;

    public DPStatistics() {
    }

    public DPStatistics(String DPname, int decknum, int statnum) {
        this.statnum = statnum;
        this.dpname = DPname;
        this.decknum = decknum;
        runnerFactions = new HashSet<CountDeckStands>();
        runnerIdentities = new HashSet<CountDeckStands>();
        corpFactions = new HashSet<CountDeckStands>();
        corpIdentities = new HashSet<CountDeckStands>();
    }

    public void addRunnerFaction(String title, int decks, int standings, String colorcode) {
        runnerFactions.add(new CountDeckStands(title, decks, standings, colorcode));
    }

    public void addCorpFaction(String title, int decks, int standings, String colorcode) {
        corpFactions.add(new CountDeckStands(title, decks, standings, colorcode));
    }

    public void addRunnerIdentity(String title, int decks, int standings, String colorcode) {
        runnerIdentities.add(new CountDeckStands(title, decks, standings, colorcode));
    }

    public void addCorpIdentity(String title, int decks, int standings, String colorcode) {
        corpIdentities.add(new CountDeckStands(title, decks, standings, colorcode));
    }


    public String getDPname() {
        return dpname;
    }

    public void setDPname(String dpname) {
        this.dpname = dpname;
    }

    public int getDecknum() {
        return decknum;
    }

    public void setDecknum(int decknum) {
        this.decknum = decknum;
    }

    public int getStatnum() {
        return statnum;
    }

    public void setStatnum(int statnum) {
        this.statnum = statnum;
    }

    public List<CountDeckStands> getSortedRunnerFactions() {
        return sortSet(runnerFactions);
    }

    public List<CountDeckStands> getSortedRunnerIdentities() {
        return sortSet(runnerIdentities);
    }

    public List<CountDeckStands> getSortedCorpFactions() {
        return sortSet(corpFactions);
    }

    public List<CountDeckStands> getSortedCorpIdentities() {
        return sortSet(corpIdentities);
    }

    private List<CountDeckStands> sortSet(Set<CountDeckStands> inputset) {
        CountDeckStatsComparator comparator = new CountDeckStatsComparator();
        List<CountDeckStands> list = new ArrayList<CountDeckStands>(inputset);
        list.sort(comparator);
        return list;
    }
}
