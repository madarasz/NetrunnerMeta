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
            case "Pre-paid VoicePAD" :
                newtitle = "Prepaid VoicePAD";
                break;
            case "Glenn Station2" :
                newtitle = "Glenn Station";
                break;
            case "Amazon Industrial Zone4" :
                newtitle = "Amazon Industrial Zone";
                break;
            case "Crisium Grid1" :
                newtitle = "Crisium Grid";
                break;
            case "Off the Grid1" :
                newtitle = "Off the Grid";
                break;
            case "Nisei MKII" :
                newtitle = "Nisei MK II";
                break;
            case "Future Perfect" :
                newtitle = "The Future Perfect";
                break;
            case "Future Is Now" :
                newtitle = "The Future is Now";
                break;
            case "Allelle Repression" :
                newtitle = "Allele Repression";
                break;
            case "Himisu Bako" :
                newtitle = "Himitsu-Bako";
                break;
        }
        return newtitle;
    }
}
