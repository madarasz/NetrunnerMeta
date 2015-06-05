package com.madarasz.netrunner.DOs.node;

import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Date;

/**
 * Created by madarasz on 05/06/15.
 */
@NodeEntity
public class Tournament {
    @RelatedTo(type = "LEGALITY")
    private CardSet legality;
    private String location;
    private Date date;
    private int players;
}
