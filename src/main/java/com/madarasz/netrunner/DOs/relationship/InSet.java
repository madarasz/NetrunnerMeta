package com.madarasz.netrunner.DOs.relationship;

import com.madarasz.netrunner.DOs.node.Card;
import com.madarasz.netrunner.DOs.node.CardSet;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * Created by madarasz on 05/06/15.
 */
@RelationshipEntity(type = "IN_SET")
public class InSet {
    @StartNode
    private Card card;
    @EndNode
    private CardSet cardSet;
}
