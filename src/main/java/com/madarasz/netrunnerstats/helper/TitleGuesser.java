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
            case "Liberated Accounts" :
                newtitle = "Liberated Account";
                break;
            case "Legwork1" :
                newtitle = "Legwork";
                break;
            case "Lucky Find1" :
                newtitle = "Lucky Find";
                break;
            case "Quality Time1" :
                newtitle = "Quality Time";
                break;
            case "Stimhack1" :
                newtitle = "Stimhack";
                break;
            case "Astrolabe1" :
                newtitle = "Astrolabe";
                break;
            case "Plascrete Carapace1" :
                newtitle = "Plascrete Carapace";
                break;
            case "Prepaid VoicePAD1" :
                newtitle = "Prepaid VoicePAD";
                break;
            case "Same Old Thing1" :
                newtitle = "Same Old Thing";
                break;
            case "Architect2" :
                newtitle = "Architect";
                break;
            case "Viper1" :
                newtitle = "Viper";
                break;
            case "ZU.13 Key Master1" :
                newtitle = "ZU.13 Key Master";
                break;
            case "Director Haas' Pet Project1" :
                newtitle = "Director Haas' Pet Project";
                break;
            case "Palana Agroplex" :
                newtitle = "Pālanā Agroplex";
                break;
            case "Doppelganger" :
                newtitle = "Doppelgänger";
                break;
            case "Salems Hospitality" :
                newtitle = "Salem's Hospitality";
                break;
            case "Rielle Kit Peddler: Transhuman" :
                newtitle = "Rielle \"Kit\" Peddler: Transhuman";
                break;
            case "Controlling the Message" :
                newtitle = "NBN: Controlling the Message";
                break;
        }
        return newtitle;
    }
}
