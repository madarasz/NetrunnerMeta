package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.admin.BlogEntry;
import com.madarasz.netrunnerstats.database.DRs.BlogRepository;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by madarasz on 1/1/16.
 * RSS helper class for blog.
 */
@Component("rssViewer")
public class RssViewer extends AbstractRssFeedView {

    @Autowired
    BlogRepository blogRepository;

    @Override
    protected Channel newFeed() {
        Channel channel = new Channel("rss_2.0");
        channel.setTitle("Know the Meta");
        channel.setDescription("Blog anout the Android Netrunner meta");
        channel.setLink("http://www.knowthemeta.com/RSS/blog");
        return channel;
    }

    @Override
    protected List<Item> buildFeedItems(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        List<BlogEntry> blogEntries = blogRepository.getAll();
        List<Item> items = new ArrayList<>();
        for (BlogEntry blogEntry : blogEntries) {
            Item item = new Item();
            Content content = new Content();
            content.setValue(blogEntry.getTeaser().replaceAll("<[^>]+>", ""));
            item.setContent(content);
            item.setTitle(blogEntry.getTitle());
            item.setLink("http://www.knowthemeta.com/Blog/" + blogEntry.getUrl());
            item.setPubDate(blogEntry.getDate());
            items.add(item);
        }
        return items;
    }
}
