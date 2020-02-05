package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.database.DOs.admin.BlogEntry;
import com.madarasz.netrunnerstats.database.DRs.BlogRepository;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by madarasz on 11/27/15.
 * Controller for the blog pages
 */
@Controller
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    @Autowired
    Neo4jOperations template;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.");

    // html output
    @RequestMapping(value="/muchadmin/blog", method = RequestMethod.GET)
    public String getBlogAdmin(Map<String, Object> model) {
        Map<String, Object> emptyparams = new HashMap<>();
        template.query("MATCH (n:BlogEntry {url: ''}) DELETE n", emptyparams);
        model.put("pageTitle", "Know the Meta - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        model.put("blogCount", template.count(BlogEntry.class));
        model.put("blogEntries", blogRepository.getAll());
        model.put("blog", new BlogEntry());
        return "AdminBlog";
    }

    // html output
    @RequestMapping(value="/muchadmin/blog/{url}", method = RequestMethod.GET)
    public String getBlogAdminEdit(@PathVariable(value="url") String url, Map<String, Object> model) {
        model.put("pageTitle", "Know the Meta - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        model.put("blogCount", template.count(BlogEntry.class));
        model.put("blogEntries", blogRepository.getAll());
        model.put("blog", blogRepository.getbyURL(url));
        return "AdminBlog";
    }

    // html output
    @RequestMapping(value="/Blog", method = RequestMethod.GET)
    public String getBlogs(Map<String, Object> model) {
        model.put("pageTitle", "Know the Meta - Blog entries - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        model.put("blogEntries", blogRepository.getAll());
        return "BlogEntries";
    }

    @RequestMapping(value="/Blog/{url}", method = RequestMethod.GET)
    public String getBlog(@PathVariable(value="url") String url, Map<String, Object> model) {
        BlogEntry blogEntry = blogRepository.getbyURL(url);
        if (url == null) {
            return "404";
        }

        model.put("pageTitle", blogEntry.getTitle() + " - Know the Meta - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        model.put("blog", blogEntry);
        return "Blog";
    }

    // add/edit blog
    @RequestMapping(value="/muchadmin/blog/AddBlog", method = RequestMethod.POST)
    public String create(@RequestParam(value = "title") String title,
                         @RequestParam(value = "url") String url,
                         @RequestParam(value = "image") String image,
                         @RequestParam(value = "date") String date,
                         @RequestParam(value = "teaser") String teaser,
                         @RequestParam(value = "text") String text,
                         @RequestParam(value = "author") String author,
                         @RequestParam(value = "pack") String pack,
                         final RedirectAttributes redirectAttributes) {
        try {
            BlogEntry blogEntry = new BlogEntry(title, text, teaser, image, dateFormat.parse(date), url, author, pack);
            blogRepository.save(blogEntry);
            redirectAttributes.addFlashAttribute("successMessage", "Blog entry added / modified");
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage","Error creating or editing blog entry.");
        }
        return "redirect:/muchadmin/blog";
    }

    // delete blog
    @RequestMapping(value="/muchadmin/blog/Delete/{url}", method = RequestMethod.GET)
    public String delete(@PathVariable(value="url") String url,
                         final RedirectAttributes redirectAttributes) {
        try {
            blogRepository.delete(blogRepository.getbyURL(url));
            redirectAttributes.addFlashAttribute("successMessage", "Blog entry deleted");
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage","Error deleting blog entry.");
        }
        return "redirect:/muchadmin/blog";
    }

    @RequestMapping(value="/JSON/Blog/Teasers", method = RequestMethod.GET)
    public @ResponseBody List<BlogEntry> getTeasers() {
        List<BlogEntry> blogEntries = blogRepository.getAll();
        for (BlogEntry blogEntry : blogEntries) {
            blogEntry.setText(""); // for smaller load
        }
        return blogEntries;
    }
}
