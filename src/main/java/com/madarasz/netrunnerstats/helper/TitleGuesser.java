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
        String newtitle = title;
        switch (title) {
            case "Kate Mac McCaffrey: Digital Tinker" :
                newtitle = "Kate \"Mac\" McCaffrey: Digital Tinker";
                break;
            case "Haarpsichord Studios" :
                newtitle = "Haarpsichord Studios: Entertainment Unleashed";
                break;
            case "Haarpsichord Studios: Entertainement Unleached" :
                newtitle = "Haarpsichord Studios: Entertainment Unleashed";
                break;
            case "Deja Vu" :
                newtitle = "Déjà Vu";
                break;
            case "Unregistered S&W 35" :
                newtitle = "Unregistered S&W '35";
                break;
        }
        return newtitle;
    }
}
