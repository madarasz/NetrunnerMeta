package com.madarasz.netrunnerstats.database.DOs.stats.entries;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 11/10/15.
 * For deck information digest.
 */
@NodeEntity
public class DeckInfo {
    @GraphId
    private Long id;
    private String shortHtmlDigest;
    private String htmlDigest;
    private String digest;
    @Indexed private String url;

    public DeckInfo() {
    }

    public DeckInfo(String shortHtmlDigest, String htmlDigest, String digest, String url) {
        this.shortHtmlDigest = shortHtmlDigest;
        this.htmlDigest = htmlDigest;
        this.digest = digest;
        this.url = url;
    }

    public String getShortHtmlDigest() {
        return shortHtmlDigest;
    }

    public String getHtmlDigest() {
        return htmlDigest;
    }

    public String getDigest() {
        return digest;
    }

    public String getUrl() {
        return url;
    }


}
