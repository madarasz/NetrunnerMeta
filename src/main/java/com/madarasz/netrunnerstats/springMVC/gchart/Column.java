package com.madarasz.netrunnerstats.springMVC.gchart;

/**
 * Created by madarasz on 11/9/15.
 * Object representation of the "cols" property in Google Charts.
 */
public class Column {
    private String id;
    private String label;
    private String pattern;
    private String type;

    public Column(String id, String label, String pattern, String type) {
        this.id = id;
        this.label = label;
        this.pattern = pattern;
        this.type = type;
    }

    public Column(String label, String type) {
        this.label = label;
        this.type = type;
        this.id = "";
        this.pattern = "";
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getPattern() {
        return pattern;
    }

    public String getType() {
        return type;
    }
}
