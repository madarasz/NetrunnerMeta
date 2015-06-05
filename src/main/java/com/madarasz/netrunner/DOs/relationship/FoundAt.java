package com.madarasz.netrunner.DOs.relationship;

import com.madarasz.netrunner.DOs.node.Deck;
import com.madarasz.netrunner.DOs.node.DeckURL;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * Created by madarasz on 05/06/15.
 */
@RelationshipEntity(type = "FOUND_AT")
public class FoundAt {
    @StartNode
    private Deck deck;
    @EndNode
    private DeckURL deckURL;
}
