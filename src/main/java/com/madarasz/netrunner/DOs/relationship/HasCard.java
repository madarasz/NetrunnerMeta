package com.madarasz.netrunner.DOs.relationship;

import com.madarasz.netrunner.DOs.node.Card;
import com.madarasz.netrunner.DOs.node.Deck;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * Created by madarasz on 05/06/15.
 */
@RelationshipEntity(type = "HAS_CARD")
public class HasCard {
    private int number;
    @StartNode
    private Deck deck;
    @EndNode
    private Card card;
}
