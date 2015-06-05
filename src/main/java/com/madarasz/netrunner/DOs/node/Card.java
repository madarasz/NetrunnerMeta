package com.madarasz.netrunner.DOs.node;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * Created by madarasz on 05/06/15.
 */
@NodeEntity
public class Card {
    @GraphId
    private int code; // NOT STRING
    @Indexed(unique=true)
    private String title;
    private String type_code; // identity, event, hardware, program, resource, agenda, asset, operation, ice, upgrade
    private String subtype_code;
    private String text;
    private String faction_code; // neutral, shaper, criminal, anarch, jinteki, haas-bioroid, weyland-consortium, nbn
    @RelatedTo(type = "IN_SET")
    private CardSet cardSet;
    private String side_code; // runner, corp
    private boolean uniqueness;
    private boolean limited;

    private int baselink;
    private int influencelimit;
    private int minimumdecksize;

    private int cost;
    private int factioncost;

    private int memoryunits;
    private int strength;

    private int advancementcost;
    private int agendapoints;

    private int trash;

    @Override
    public String toString() {
        return title + " (" + code + ") /" + faction_code + "/";
    }
}
