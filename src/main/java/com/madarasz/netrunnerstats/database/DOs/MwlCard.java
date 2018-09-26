package com.madarasz.netrunnerstats.database.DOs;

import com.madarasz.netrunnerstats.helper.Enums;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Node for MWL card entries
 * Created by madarasz on 2018-03-22.
 */
@NodeEntity
public class MwlCard {
    @GraphId private Long id;
    private Card card;
    private Enums.MwlType type;
    private int value;

    public MwlCard() {
    }

    public MwlCard(Card card, Enums.MwlType type, int value) {
        this.card = card;
        this.type = type;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public Card getCard() {
        return card;
    }

    public Enums.MwlType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return card + " | " + type.toString() + ": " + value;
    }

}
