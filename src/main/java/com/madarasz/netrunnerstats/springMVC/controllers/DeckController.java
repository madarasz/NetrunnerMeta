package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.Statistics;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.DeckInfo;
import com.madarasz.netrunnerstats.database.DRs.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by madarasz on 11/10/15.
 * Controller class for deck related information.
 */
@Controller
public class DeckController {

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    Statistics statistics;

    // JSON output
    @RequestMapping(value="/JSON/Deck", params = {"url"}, method = RequestMethod.GET)
    public @ResponseBody
    DeckInfo getDeckInfo (@RequestParam String url) {
        Deck deck = deckRepository.findByUrl(url);
        if (deck != null) {
            return statistics.getDeckInfo(deck);
        } else {
            return new DeckInfo();
        }
    }

    // JSON output
    @RequestMapping(value="/JSON/Deck/{DPName}/{identity}", method = RequestMethod.GET)
    public @ResponseBody
    List<DeckInfo> getAllDeckInfos(@PathVariable(value="identity") String identity, @PathVariable(value="DPName") String DPName) {
        return statistics.getDeckInfos(identity, DPName).getSortedInfos();
    }
}
