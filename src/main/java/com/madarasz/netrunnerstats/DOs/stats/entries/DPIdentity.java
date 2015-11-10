package com.madarasz.netrunnerstats.DOs.stats.entries;

/**
 * Created by madarasz on 11/10/15.
 * Statistical entry for identities for a data pack.
 */
public class DPIdentity {
    private String title;
    private String url;
    private int decknum;
    private int topdecknum;

    public DPIdentity(String title, String url, int decknum, int topdecknum) {
        this.title = title;
        this.url = url;
        this.decknum = decknum;
        this.topdecknum = topdecknum;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getDecknum() {
        return decknum;
    }

    public int getTopdecknum() {
        return topdecknum;
    }
}
