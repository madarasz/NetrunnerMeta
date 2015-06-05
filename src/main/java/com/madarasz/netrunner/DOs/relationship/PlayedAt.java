package com.madarasz.netrunner.DOs.relationship;

import com.madarasz.netrunner.DOs.node.Deck;
import com.madarasz.netrunner.DOs.node.Tournament;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * Created by madarasz on 05/06/15.
 */
@RelationshipEntity(type = "PLAYED_AT")
public class PlayedAt {
    private int standing;
    @StartNode
    private Deck deck;
    @EndNode
    private Tournament tournament;
}
