package com.madarasz.netrunnerstats.database.DOs.admin;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 12/3/15.
 * Store list of URLs to avoid while importing
 */
@NodeEntity
public class AdminData {
    @GraphId private Long id;
    private String data;
    @Indexed(unique=true) private String function;

    public AdminData() {
        data = "";
    }

    public AdminData(String function, String data) {
        this.function = function;
        this.data = data;
    }

    public String getData() {
        if (data == null) {
            return "";
        } else {
            return data;
        }
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public boolean isIn(String subtext) {
        return getData().contains(subtext);
    }
}
