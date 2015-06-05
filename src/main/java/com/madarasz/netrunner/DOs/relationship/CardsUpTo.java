package com.madarasz.netrunner.DOs.relationship;

import com.madarasz.netrunner.DOs.node.CardSet;
import com.madarasz.netrunner.DOs.node.Deck;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * Created by madarasz on 05/06/15.
 */
@RelationshipEntity(type = "CARDS_UP_TO")
public class CardsUpTo {
    @StartNode
    private Deck deck;
    @EndNode
    private CardSet cardSet;
}
