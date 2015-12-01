package com.madarasz.netrunnerstats.helper.gchartConverter;

import com.madarasz.netrunnerstats.database.DOs.stats.DPStatistics;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CountDeckStands;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 11/19/15.
 * Abstract class for DP info to Chart functions
 */
public abstract class DPToChart {

    protected List<CountDeckStands> filter (DPStatistics stats, String sidecode, String stattype){
        List<CountDeckStands> data;
        if (sidecode.equals("runner")) {
            switch (stattype) {
                case "identity":
                    data = stats.getSortedRunnerIdentities();
                    break;
                case "faction":
                    data = stats.getSortedRunnerFactions();
                    break;
                default:
                    return new ArrayList<>();
            }
        } else if (sidecode.equals("corp")) {
            switch (stattype) {
                case "identity":
                    data = stats.getSortedCorpIdentities();
                    break;
                case "faction":
                    data = stats.getSortedCorpFactions();
                    break;
                default:
                    return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
        return data;
    }
}
