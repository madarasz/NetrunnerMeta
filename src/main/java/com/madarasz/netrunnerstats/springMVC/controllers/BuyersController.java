package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Created by madarasz on 02/18/16.
 * Controller for the swag
 */
@Controller
public class BuyersController {

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    @Autowired
    CardRepository cardRepository;

    // html output
    @RequestMapping(value="/Buyers-Guide", method = RequestMethod.GET)
    public String getBuyers(Map<String, Object> model) {
        model.put("pageTitle", "Buyers Guide - Know the Meta - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        return "Buyers";
    }

    @RequestMapping(value="/Buyers-Guide/side/{side}", method = RequestMethod.GET)
    public String getBuyersSide(@PathVariable(value="side") String side, Map<String, Object> model) {
        model.put("pageTitle", "Buyers Guide - Know the Meta - Android: Netrunner");
        model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
        model.put("side", side);
        return "Buyers";
    }

    @RequestMapping(value="/Buyers-Guide/identity/{id}", method = RequestMethod.GET)
    public String getBuyersId(@PathVariable(value="id") String id, Map<String, Object> model) {
        Card card = cardRepository.findByTitle(id);
        if (card != null) {
            model.put("pageTitle", "Buyer's Guide - Know the Meta - Android: Netrunner");
            model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
            model.put("id", id);
            model.put("imgsrc", card.getImageSrc());
            model.put("pack", card.getCardPack().getName());
            model.put("faction", card.getFaction_code());
            model.put("side", card.isRunner() ? "runner" : "corp");
        }
        return "Buyers";
    }

}
