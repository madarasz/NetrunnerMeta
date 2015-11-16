package com.madarasz.netrunnerstats.database.DOs.stats.entries;

/**
 * Created by madarasz on 11/10/15.
 * For deck information digest.
 */
public class DeckInfo {
    private String shortHtmlDigest;
    private String htmlDigest;
    private String digest;
    private String url;

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
