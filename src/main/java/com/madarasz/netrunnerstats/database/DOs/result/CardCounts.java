package com.madarasz.netrunnerstats.database.DOs.result;

import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.annotation.ResultColumn;

/**
 * Created by madarasz on 2015.11.23..
 */
@QueryResult
public interface CardCounts {
    @ResultColumn("title")
    public String getTitle();
    @ResultColumn("count")
    public int getCount();
    @ResultColumn("cardpack")
    public String getCardpack();
    @ResultColumn("faction")
    public String getFaction();
}
