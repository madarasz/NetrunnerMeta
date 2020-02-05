package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.helper.RssViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by madarasz on 1/1/16.
 * Controller for the RSS blog feed.
 */
@Controller
public class RSSController {

    @Autowired
    RssViewer rssViewer;

    @RequestMapping(value = "/RSS/blog", method = RequestMethod.GET)
    public RssViewer getBlogRss() {
        return rssViewer;
    }
}
