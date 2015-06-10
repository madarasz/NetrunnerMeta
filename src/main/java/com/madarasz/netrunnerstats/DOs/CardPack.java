package com.madarasz.netrunnerstats.DOs;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

/**
 * Created by madarasz on 2015-06-08.
 */
@NodeEntity
public class CardPack {
    @GraphId Long id;
    private String name;
    public String code;
    private int number;
    private int cyclenumber;

    public CardPack() {
    }

    public CardPack(String name, String code, int number, int cyclenumber) {
        this.name = name;
        this.code = code;
        this.number = number;
        this.cyclenumber = cyclenumber;
    }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
