package com.madarasz.netrunnerstats.database.DOs.result;

import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.annotation.ResultColumn;

/**
 * Created by madarasz on 2015.08.25..
 */
@QueryResult
public interface StatCounts{
    @ResultColumn("category")
    public String getCategory();
    @ResultColumn("count")
    public int getCount();
    @ResultColumn("side_code")
    public String getSideCode();
}
