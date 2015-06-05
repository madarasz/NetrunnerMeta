package com.madarasz.netrunner.DOs.node;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

/**
 * Created by madarasz on 05/06/15.
 */
@NodeEntity
public class DeckArchetype {
    private String name;
    @RelatedTo(type = "USES_CARD")
    private Set<Card> cards;
}
