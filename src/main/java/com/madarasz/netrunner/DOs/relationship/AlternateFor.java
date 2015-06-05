package com.madarasz.netrunner.DOs.relationship;

import com.madarasz.netrunner.DOs.node.AltCardName;
import com.madarasz.netrunner.DOs.node.Card;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * Created by madarasz on 05/06/15.
 */
@RelationshipEntity(type = "ALTERNATE_FOR")
public class AlternateFor {
    @StartNode
    private AltCardName altCardName;
    @EndNode
    private Card card;
}
