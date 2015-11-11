package com.madarasz.netrunnerstats.springMVC.controllers;

import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.stats.DeckInfos;
import com.madarasz.netrunnerstats.DOs.stats.IdentityMDS;
import com.madarasz.netrunnerstats.DOs.stats.entries.DeckInfo;
import com.madarasz.netrunnerstats.DRs.DeckRepository;
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

    // JSON output
    @RequestMapping(value="/JSON/Deck", params = {"url"}, method = RequestMethod.GET)
    public @ResponseBody
    DeckInfo getDeckInfo (@RequestParam String url) {
        Deck deck = deckRepository.findByUrl(url);
        if (deck != null) {
            return new DeckInfo(deck);
        } else {
            return new DeckInfo();
        }
    }

    // JSON output
    @RequestMapping(value="/JSON/Deck/{DPName}/{identity}", method = RequestMethod.GET)
    public @ResponseBody
    DeckInfos getAllDeckInfos(@PathVariable(value="identity") String identity, @PathVariable(value="DPName") String DPName) {
        List<Deck> decks = deckRepository.filterByIdentityAndCardPool(identity, DPName);
        return new DeckInfos(decks);
    }
}
