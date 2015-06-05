package com.madarasz.netrunner.DOs.node;

import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * Created by madarasz on 05/06/15.
 */
@NodeEntity
public class AltCardName {
    @Indexed(unique=true)
    private String altname;

    @RelatedTo(type = "ALTERNATE_FOR")
    private Card card;

    public AltCardName(String altname, Card card) {
        this.altname = altname;
        this.card = card;
    }
}
