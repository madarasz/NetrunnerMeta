package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by madarasz on 11/9/15.
 * Sets color codes based on faction or identity.
 */
@Component
public class ColorPicker {

    @Autowired
    CardRepository cardRepository;

    public ColorPicker() {
    }

    public String colorFaction(String title) {
        switch (title) {
            case "shaper":
                return("#7EAC39");
            case "criminal":
                return("#3962AC");
            case "anarch":
                return("#AC5439");
            case "jinteki":
                return("#8f1E0A");
            case "haas-bioroid":
                return("#702871");
            case "weyland-consortium":
                return("#1B654F");
            case "nbn":
                return("#CB953A");
            case "adam":
                return("darkgoldenrod");
            case "apex":
                return("darkred");
            case "sunny-lebeau":
                return("black");
            default:
                return("#CCCCCC");
        }
    }

    public String colorIdentity(String title) {
        Card card = cardRepository.findByTitle(title);
        if (card != null) {
            return colorFaction(card.getFaction_code());
        } else {
            return("#CCCCCC");
        }
    }
}
