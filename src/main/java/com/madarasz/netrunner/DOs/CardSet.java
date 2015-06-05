package com.madarasz.netrunner.DOs;

import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by jenkins on 05/06/15.
 */
@NodeEntity
public class CardSet {
    private String name;
    @Indexed(unique=true)
    private String code;
    private int number;
    private int cyclenumber;

    public CardSet(String name, String code, int number, int cyclenumber) {
        this.name = name;
        this.code = code;
        this.number = number;
        this.cyclenumber = cyclenumber;
    }

    @Override
    public String toString() {
        return name + " (" + code + ") [" + number + "/" + cyclenumber + "]";
    }
}
