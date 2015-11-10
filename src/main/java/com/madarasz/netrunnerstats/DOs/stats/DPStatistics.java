package com.madarasz.netrunnerstats.DOs.stats;

import com.madarasz.netrunnerstats.DOs.stats.entries.CountDeckStands;
import org.springframework.data.neo4j.annotation.*;

import java.util.HashSet;
import java.util.Set;

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

    public Set<CountDeckStands> getRunnerFactions() {
        return runnerFactions;
    }

    public Set<CountDeckStands> getRunnerIdentities() {
        return runnerIdentities;
    }

    public Set<CountDeckStands> getCorpFactions() {
        return corpFactions;
    }

    public Set<CountDeckStands> getCorpIdentities() {
        return corpIdentities;
    }
}
