package com.madarasz.netrunnerstats.DOs;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * Node for tournament standings
 * Created by madarasz on 2015.08.10..
 */
@NodeEntity
public class Standing {
    @GraphId
    Long id;
    @RelatedTo(type = "IN_TOURNAMENT") @Fetch private Tournament tournament;
    private int rank;
    @RelatedTo(type = "IS_IDENTITY") @Fetch private Card identity;
    private boolean top;

    public Standing() {
    }

    public Standing(Tournament tournament, int rank, Card identity, boolean top) {
        this.tournament = tournament;
        this.rank = rank;
        this.identity = identity;
        this.top = top;
    }
}
