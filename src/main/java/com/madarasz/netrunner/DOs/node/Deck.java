package com.madarasz.netrunner.DOs.node;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

/**
 * Created by madarasz on 05/06/15.
 */
@NodeEntity
public class Deck {
    @GraphId
    private long id; // TODO: how to set this?
    @RelatedTo(type = "HAS_CARD")
    private Set<Card> cards;
    @RelatedTo(type = "FOUND_AT")
    private Set<DeckURL> found_at;
    @RelatedTo(type = "RELATED_ARCHETYPE")
    private DeckArchetype archetype;
    @RelatedTo(type = "PLAYED_AT")
    private Tournament tournament;
    @RelatedTo(type = "CARDS_UP_TO")
    private CardSet cardSet;
    private String player_name;
}
