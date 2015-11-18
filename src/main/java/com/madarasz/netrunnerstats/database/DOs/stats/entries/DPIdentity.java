package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 11/10/15.
 * Statistical entry for identities for a data pack.
 */
@NodeEntity
public class DPIdentity {
    @GraphId
    private Long id;
    private String title;
    private String url;
    private int decknum;
    private int topdecknum;

    public DPIdentity() {
    }

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
