package com.madarasz.netrunnerstats.helper;

import org.springframework.stereotype.Component;

/**
 * Created by madarasz on 11/6/15.
 * Fixes type-os in card titles.
 * Every type-o has to be added mannually :(
 */
@Component
public class TitleGuesser {

    public String alternateTitle(String title) {
        String newtitle = "";
        switch (title) {
            case "Haarpsichord Studios: Entertainement Unleached" :
                newtitle = "Haarpsichord Studios: Entertainment Unleashed";
                break;
            case "Deja Vu" :
                newtitle = "Déjà Vu";
                break;
//            default:
//                System.out.println(String.format("ERROR - Unknown card title: %s", title));
        }
        return newtitle;
    }
}
