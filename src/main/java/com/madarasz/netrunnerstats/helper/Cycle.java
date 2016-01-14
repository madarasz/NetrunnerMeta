package com.madarasz.netrunnerstats.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 1/14/16.
 * Representation of card cycle
 */
public class Cycle {

    private int cyclenumber;
    private String title;
    private List<String> datapacks;

    public Cycle(int cyclenumber) {
        this.cyclenumber = cyclenumber;
        this.datapacks = new ArrayList<>();
    }

    public int getCyclenumber() {
        return cyclenumber;
    }

    public List<String> getDatapacks() {
        return datapacks;
    }

    public String getTitle() {
        return Enums.CardCycles.values()[cyclenumber].toString().replaceAll("_", " ");
    }

    public void addDatapacks(List<String> datapacks) {
        this.datapacks.addAll(datapacks);
    }

}
