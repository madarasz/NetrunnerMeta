package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 11/12/15.
 * Statistical information on cardpool data per datapack.
 */
@NodeEntity
public class CardPool {
    @GraphId
    private Long id;
    private String title;
    private int tournamentnum;
    private int decknum;
    private int standingsnum;
    private int matchesnum;
    private int dpnumber;
    private int cyclenumber;

    public CardPool() {
    }

    public CardPool(String title, int tournamentnum, int decknum, int standingsnum, int matchesnum, int dpnumber, int cyclenumber) {
        this.title = title;
        this.tournamentnum = tournamentnum;
        this.decknum = decknum;
        this.standingsnum = standingsnum;
        this.matchesnum = matchesnum;
        this.dpnumber = dpnumber;
        this.cyclenumber = cyclenumber;
    }

    public String getTitle() {
        return title;
    }

    public int getTournamentnum() {
        return tournamentnum;
    }

    public int getDecknum() {
        return decknum;
    }

    public int getStandingsnum() {
        return standingsnum;
    }

    public int getMatchesnum() {
        return matchesnum;
    }

    public int getDpnumber() {
        return dpnumber;
    }

    public int getCyclenumber() {
        return cyclenumber;
    }
}
