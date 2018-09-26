package com.madarasz.netrunnerstats.database.DOs;

import com.madarasz.netrunnerstats.helper.Enums;
import org.springframework.data.neo4j.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Node for MWL entries
 * Created by madarasz on 2018-03-22.
 */
@NodeEntity
public class Mwl {
    @GraphId private Long id;
    private String name;
    private String date_start;
    @RelatedTo(type = "IDENTITY") @Fetch private Set<MwlCard> cards;

    public Mwl() {
        this.cards = new HashSet<>();
    }

    public Mwl(Long id, String name, String date_start) {
        this.id = id;
        this.name = name;
        this.date_start = date_start;
        this.cards = new HashSet<>();
    }

    public MwlCard addCard(Card card, Enums.MwlType type, int value) {
        MwlCard mwlCard = new MwlCard(card, type, value);
        this.cards.add(mwlCard);
        return mwlCard;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate_start() {
        return date_start;
    }

    public Set<MwlCard> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return name;
    }

}
