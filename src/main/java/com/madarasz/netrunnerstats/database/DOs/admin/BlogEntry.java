package com.madarasz.netrunnerstats.database.DOs.admin;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by madarasz on 12/22/15.
 * For blog entries
 */
@NodeEntity
public class BlogEntry {
    @GraphId private Long id;
    private String title;
    private String text;
    private String teaser;
    private String imageUri;
    private Date date;
    @Indexed(unique=true) private String url;

    public BlogEntry() {
    }

    public BlogEntry(String title, String text, String teaser, String imageUri, Date date, String url) {
        this.title = title;
        this.text = text;
        this.teaser = teaser;
        this.imageUri = imageUri;
        this.date = date;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getTeaser() {
        return teaser;
    }

    public String getImageUri() {
        return imageUri;
    }

    public Date getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public void setText(String text) {
        this.text = text;
    }

    // totally fake
    public String getFormattedDate() {
        if (date == null) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.");
        return dateFormat.format(date);
    }
}
