package com.madarasz.netrunnerstats.springMVC.gchartConverter;

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
            if (stattype.equals("identity")) {
                data = stats.getSortedRunnerIdentities();
            } else if (stattype.equals("faction")){
                data = stats.getSortedRunnerFactions();
            } else {
                return new ArrayList<CountDeckStands>();
            }
        } else if (sidecode.equals("corp")) {
            if (stattype.equals("identity")) {
                data = stats.getSortedCorpIdentities();
            } else if (stattype.equals("faction")){
                data = stats.getSortedCorpFactions();
            } else {
                return new ArrayList<CountDeckStands>();
            }
        } else {
            return new ArrayList<CountDeckStands>();
        }
        return data;
    }
}
