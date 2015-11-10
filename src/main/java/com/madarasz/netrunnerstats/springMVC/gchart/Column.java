package com.madarasz.netrunnerstats.springMVC.gchart;

/**
 * Created by madarasz on 11/9/15.
 * Object representation of the "cols" property in Google Charts.
 */
public class Column {
    private String id;  // OPTIONAL
    private String label;   // OPTIONAL
    private String pattern; // OPTIONAL - for formatting
    private String role; // OPTIONAL - style
    private String type;    // REQUIRED - boolean, number, string, date, datetime, timeofday

    public Column(String id, String label, String pattern, String type, String role) {
        this.id = id;
        this.label = label;
        this.pattern = pattern;
        this.type = type;
        this.role = role;
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

    public String getRole() {
        return role;
    }
}
