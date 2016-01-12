package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 1/8/16.
 * Card combos for a certain card.
 */
@NodeEntity
public class CardCombo {
    @GraphId
    private Long id;
    private String title;
    private String pack;
    private int score;

    public CardCombo() {
    }

    public CardCombo(String title, String pack, int score) {
        this.title = title;
        this.pack = pack;
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public String getPack() {
        return pack;
    }

    public int getScore() {
        return score;
    }
}
