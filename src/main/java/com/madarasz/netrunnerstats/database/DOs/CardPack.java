package com.madarasz.netrunnerstats.database.DOs;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Node for Netrunner data packs
 * Created by madarasz on 2015-06-08.
 */
@NodeEntity
public class CardPack {
    @GraphId Long id;
    @Indexed(unique=true) private String name;
    private String code;
    private String amazonHtml;
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

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getNumber() {
        return number;
    }

    public int getCyclenumber() {
        return cyclenumber;
    }

    public String getAmazonHtml() {
        return amazonHtml;
    }

    public void setAmazonHtml(String amazonHtml) {
        this.amazonHtml = amazonHtml;
    }

    /**
     * Checks if this cardPack came out later than the other pack.
     * @param cardPack other pack
     * @return is it later
     */
    public boolean later(CardPack cardPack) {
        return ((cyclenumber > cardPack.getCyclenumber()) ||
                ((cyclenumber == cardPack.getCyclenumber()) && (number > cardPack.getNumber())));
    }

    @Override
    public String toString() {
        return name;
    }
}
