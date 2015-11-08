package com.madarasz.netrunnerstats.DOs.stats;

/**
 * Created by madadarasz on 2015.11.08..
 * For counting statistics on deck and standings numbers
 */
public class CountDeckStands {
    private String title;
    private int decknum;
    private int standingnum;

    public CountDeckStands(String title, int decknum, int standingnum) {
        this.title = title;
        this.decknum = decknum;
        this.standingnum = standingnum;
    }

    public String getTitle() {
        return title;
    }

    public int getDecknum() {
        return decknum;
    }

    public int getStandingnum() {
        return standingnum;
    }
}
