package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.database.DOs.CardPack;
import com.madarasz.netrunnerstats.database.DRs.CardPackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by madarasz on 08/25/16.
 * Controller for the amazon stuff
 */
@Controller
public class AmazonController {

    private static final Logger logger = LoggerFactory.getLogger(AmazonController.class);

    @Autowired
    Neo4jOperations template;

    @Autowired
    CardPackRepository cardPackRepository;

    // html output
    @RequestMapping(value="/muchadmin/amazon", method = RequestMethod.GET)
    public String getBlogAdmin(Map<String, Object> model) {
        Map<String, Object> emptyparams = new HashMap<>();
        model.put("pageTitle", "Know the Meta - Android: Netrunner");
        model.put("packCount", template.count(CardPack.class));
        model.put("packEntries", cardPackRepository.getSortedPacks());
        return "AdminAmazon";
    }

    // update Amazon data
    @RequestMapping(value="/muchadmin/amazon/update", method = RequestMethod.POST)
    public String create(@RequestParam(value = "packName") String packName,
                         @RequestParam(value = "amazonHtml") String amazonHtml,
                         final RedirectAttributes redirectAttributes) {
        try {
            CardPack cardPack = cardPackRepository.findByName(packName);
            cardPack.setAmazonHtml(amazonHtml);
            cardPackRepository.save(cardPack);
            redirectAttributes.addFlashAttribute("successMessage", "Amazon data updated");
        } catch (Exception ex) {
            logger.error("logged exception", ex);
            redirectAttributes.addFlashAttribute("errorMessage","Error updating Amazon data.");
        }
        return "redirect:/muchadmin/amazon";
    }

    // pass cardpack data with Amazon html
    @RequestMapping(value="/JSON/Cardpack/{DPName}", method = RequestMethod.GET)
    public @ResponseBody
    CardPack getPack(@PathVariable(value = "DPName") String DPName) {
        return cardPackRepository.findByName(DPName);
    }
}
