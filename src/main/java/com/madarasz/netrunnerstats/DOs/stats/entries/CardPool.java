package com.madarasz.netrunnerstats.DOs.stats.entries;

/**
 * Created by madarasz on 11/12/15.
 */
public class CardPool {
    private String title;
    private int tournamentnum;
    private int decknum;
    private int standingsnum;

    public CardPool() {
    }

    public CardPool(String title, int tournamentnum, int decknum, int standingsnum) {
        this.title = title;
        this.tournamentnum = tournamentnum;
        this.decknum = decknum;
        this.standingsnum = standingsnum;
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
}
