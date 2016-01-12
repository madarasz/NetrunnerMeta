package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardCombo;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.DPDecks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 1/8/16.
 */
public class CardStat {
    private String title;
    private String code;
    private List<CardUsage> overTime;
    private List<CardUsage> top; // top identities for cards, top cards for identities
    private List<CardCombo> combos;
    private List<DPDecks> decks;

    public CardStat() {
        this.overTime = new ArrayList<>();
        this.top = new ArrayList<>();
        this.combos = new ArrayList<>();
        this.decks = new ArrayList<>();
    }

    public CardStat(Card card) {
        this.title = card.getTitle();
        this.code = card.getCode();
        this.overTime = new ArrayList<>();
        this.top = new ArrayList<>();
        this.combos = new ArrayList<>();
        this.decks = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public List<CardUsage> getOverTime() {
        return overTime;
    }

    public List<CardUsage> getTop() {
        return top;
    }

    public List<CardCombo> getCombos() {
        return combos;
    }

    public List<DPDecks> getDecks() {
        return decks;
    }

    public void addOverTime(CardUsage pack) {
        this.overTime.add(pack);
    }

    public void addTop(CardUsage id) {
        this.top.add(id);
    }

    public void addCombos(CardCombo combo) {
        this.combos.add(combo);
    }

    public void addDecks(DPDecks dpDecks) {
        this.decks.add(dpDecks);
    }
}
