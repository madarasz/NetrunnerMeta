package com.madarasz.netrunner.DOs.relationship;

import com.madarasz.netrunner.DOs.node.Card;
import com.madarasz.netrunner.DOs.node.DeckArchetype;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * Created by madarasz on 05/06/15.
 */
@RelationshipEntity(type = "USES_CARD")
public class UsesCard {
    private float quantity;
    @StartNode
    private DeckArchetype deckArchetype;
    @EndNode
    private Card card;
}
