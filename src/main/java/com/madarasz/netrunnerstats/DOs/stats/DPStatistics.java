package com.madarasz.netrunnerstats.DOs.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 2015.11.08..
 */
public class DPStatistics {
    private String DPname;
    private int decknum;
    private int statnum;
    private List<CountDeckStands> runnerFactions;
    private List<CountDeckStands> runnerIdentities;
    private List<CountDeckStands> corpFactions;
    private List<CountDeckStands> corpIdentities;

    public DPStatistics(String DPname, int decknum, int statnum) {
        this.statnum = statnum;
        this.DPname = DPname;
        this.decknum = decknum;
        runnerFactions = new ArrayList<CountDeckStands>();
        runnerIdentities = new ArrayList<CountDeckStands>();
        corpFactions = new ArrayList<CountDeckStands>();
        corpIdentities = new ArrayList<CountDeckStands>();
    }

    public void addRunnerFaction(String title, int decks, int standings) {
        runnerFactions.add(new CountDeckStands(title, decks, standings, "#FF0000"));
    }

    public void addCorpFaction(String title, int decks, int standings) {
        corpFactions.add(new CountDeckStands(title, decks, standings, "#FF0000"));
    }

    public void addRunnerIdentity(String title, int decks, int standings) {
        runnerIdentities.add(new CountDeckStands(title, decks, standings, "#FF0000"));
    }

    public void addCorpIdentity(String title, int decks, int standings) {
        corpIdentities.add(new CountDeckStands(title, decks, standings, "#FF0000"));
    }


    public String getDPname() {
        return DPname;
    }

    public void setDPname(String DPname) {
        this.DPname = DPname;
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

    public List<CountDeckStands> getRunnerFactions() {
        return runnerFactions;
    }

    public List<CountDeckStands> getRunnerIdentities() {
        return runnerIdentities;
    }

    public List<CountDeckStands> getCorpFactions() {
        return corpFactions;
    }

    public List<CountDeckStands> getCorpIdentities() {
        return corpIdentities;
    }
}
