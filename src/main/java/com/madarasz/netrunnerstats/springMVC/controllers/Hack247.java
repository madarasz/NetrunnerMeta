package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.stats.CardStat;
import com.madarasz.netrunnerstats.database.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.database.DRs.stats.CardPoolStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by madarasz on 11/22/15.
 * Controller class for card information.
 * ("/" is required at the end of URLs to get card names with dots right)
 * TODO: THIS IS AN UGLY HACK FOR "24/7 Newscycle"
 */
@Controller
public class Hack247 {

    @Autowired
    Statistics statistics;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;

    @Autowired
    CardPoolStatsRepository cardPoolStatsRepository;

    @RequestMapping(value="/JSON/Cards/24/{target}/card.json", method = RequestMethod.GET)
    public @ResponseBody
    CardStat getCardJSON(@PathVariable(value="target") String target) {
        return statistics.getCardStats("24/" + target);
    }

    // html output
    @RequestMapping(value="/Cards/24/{title}/", method = RequestMethod.GET)
    public String getCardStat247(@PathVariable(value="title") String title, Map<String, Object> model) {
        title = "24/" + title;
        Card card = cardRepository.findByTitle(title);
        if (card == null) {
            return "404";
        } else {
            model.put("title", title);
            model.put("dp", card.getCardPack().getName());
            model.put("faction", "icon-" + card.getFaction_code());
            model.put("imgsrc", card.getImageSrc());
            model.put("pageTitle", card.getTitle() + " - Know the Meta - Android: Netrunner");
            model.put("cardpools", cardPoolStatsRepository.getCardPoolNames());
            if (card.getType_code().equals("identity")) {
                model.put("identity", true);
                model.put("toptitle", "Most used cards with this identity");
            } else {
                model.put("identity", false);
                model.put("toptitle", "Used with identity");
            }
            return "CardStat";
        }
    }

}
