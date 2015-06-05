package com.madarasz.netrunner.DOs.node;

import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by madarasz on 05/06/15.
 */
@NodeEntity
public class IgnoreCardName {
    @Indexed(unique=true)
    private String ignore_name;
}
