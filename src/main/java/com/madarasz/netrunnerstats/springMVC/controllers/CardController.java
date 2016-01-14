package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.stats.CardStat;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.helper.Cycle;
import com.madarasz.netrunnerstats.helper.gchart.DataTable;
import com.madarasz.netrunnerstats.helper.gchartConverter.CardToOverTimeGchart;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by madarasz on 11/22/15.
 * Controller class for card information.
 * ("/" is required at the end of URLs to get card names with dots right)
 */
@Controller
public class CardController {

    @Autowired
    Statistics statistics;

    @Autowired
    CardToOverTimeGchart cardToOverTimeGchart;

    @Autowired
    CardRepository cardRepository;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CardController.class);

    // JSON output
    @RequestMapping(value="/JSON/Cards/{target}/{sidecode}/{DPName}", method = RequestMethod.GET)
    public @ResponseBody
    List<CardUsage> getMostUsedCards(
            @PathVariable(value="target") String target,
            @PathVariable(value="sidecode") String sidecode,
            @PathVariable(value="DPName") String DPName) {
        switch (target) {
            case "Cardpack":
                return statistics.getMostUsedCardsFromCardPack(DPName).getSortedCards(sidecode);
            case "Cardpool":
                return statistics.getMostUsedCardsForCardpool(DPName).getSortedCards(sidecode);
            default:
                return new ArrayList<>();
        }
    }

    @RequestMapping(value="/JSON/Cards/{target}/", method = RequestMethod.GET)
    public @ResponseBody
    CardStat getCardJSON(@PathVariable(value="target") String target) {
        return statistics.getCardStats(target);
    }

    @RequestMapping(value="/JSON/Cards/Overtime/{target}/", method = RequestMethod.GET)
    public @ResponseBody
    DataTable getCardOverTimeJSON(@PathVariable(value="target") String target) {
        return cardToOverTimeGchart.converter(target);
    }

    @RequestMapping(value="/JSON/Cardpacks", method = RequestMethod.GET)
    public @ResponseBody List<Cycle> getCardPacks() {
        return statistics.getDPStructure();
    }

    // html output
    @RequestMapping(value="/Cards/{title}/", method = RequestMethod.GET)
    public String getCardStat(@PathVariable(value="title") String title, Map<String, Object> model) {
        Card card = cardRepository.findByTitle(title);
        if (card == null) {
            logger.error("No such card name: " + title);
            return "404";
        } else {
            model.put("title", title);
            model.put("dp", card.getCardPack().getName());
            model.put("faction", "icon-" + card.getFaction_code());
            model.put("imgsrc", card.getImageSrc());
            model.put("pageTitle", card.getTitle() + " - Know the Meta - Android: Netrunner");
            if (card.getType_code().equals("identity")) {
                model.put("toptitle", "Most used cards with this identity");
            } else {
                model.put("toptitle", "Mostly used with identity");
            }
            return "CardStat";
        }
    }

    // html output
    @RequestMapping(value="/Cards", method = RequestMethod.GET)
    public String getCards(Map<String, Object> model) {
        model.put("pageTitle", "Cards - Know the Meta - Android: Netrunner");
        return "Cards";
    }
}
