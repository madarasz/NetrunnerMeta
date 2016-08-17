package com.madarasz.netrunnerstats.helper.comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by madarasz on 01/12/16.
 * Abstract Comparator for stuff based on data pack names
 */
public class DPComparator {

    // TODO: this should be coming from DB
    protected List<String> dpnames = new ArrayList<>(Arrays.asList("Order and Chaos",
            "The Valley", "Breaker Bay", "Chrome City","The Underway", "Old Hollywood", "The Universe of Tomorrow",
            "Data and Destiny", "D&D, post-MWL",
            "Kala Ghoda", "Business First", "Democracy and Dogma", "Salsette Island", "The Liberated Mind", "Fear the Masses",
            "23 Seconds", "Blood Money", "Escalation", "Intervention"));

    protected int getNumber(String title) {
        return dpnames.indexOf(title);
    }

}
