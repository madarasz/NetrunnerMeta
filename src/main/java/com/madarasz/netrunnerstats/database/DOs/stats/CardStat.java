package com.madarasz.netrunnerstats.database.DOs.stats;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardCombo;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.CardUsage;
import com.madarasz.netrunnerstats.database.DOs.stats.entries.DPDecks;
import com.madarasz.netrunnerstats.helper.comparator.CardComboComparator;
import com.madarasz.netrunnerstats.helper.comparator.CardUsageComparator;
import com.madarasz.netrunnerstats.helper.comparator.CardUsageDPComparator;
import com.madarasz.netrunnerstats.helper.comparator.DPDecksComparator;
import org.springframework.data.neo4j.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by madarasz on 1/8/16.
 * Statistics for a card.
 */
@NodeEntity
public class CardStat {
    @GraphId
    private Long id;
    @Indexed(unique=true) private String title;
    private String code;
    @RelatedTo(type = "OVERTIME") private @Fetch Set<CardUsage> overTime;
    @RelatedTo(type = "TOP") private @Fetch Set<CardUsage> top; // top identities for cards, top cards for identities
    @RelatedTo(type = "COMBO") private @Fetch Set<CardCombo> combos;
    @RelatedTo(type = "DECKS") private @Fetch Set<DPDecks> decks;

    public CardStat() {
        this.overTime = new HashSet<>();
        this.top = new HashSet<>();
        this.combos = new HashSet<>();
        this.decks = new HashSet<>();
    }

    public CardStat(Card card) {
        this.title = card.getTitle();
        this.code = card.getCode();
        this.overTime = new HashSet<>();
        this.top = new HashSet<>();
        this.combos = new HashSet<>();
        this.decks = new HashSet<>();
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public List<CardUsage> getOverTime() {
        List<CardUsage> result = new ArrayList<>(overTime);
        CardUsageDPComparator comparator = new CardUsageDPComparator();
        result.sort(comparator);
        return result;
    }

    public List<CardUsage> getTop() {
        List<CardUsage> result = new ArrayList<>(top);
        CardUsageComparator comparator = new CardUsageComparator();
        result.sort(comparator);
        return result;
    }

    public List<CardCombo> getCombos() {
        List<CardCombo> result = new ArrayList<>(combos);
        CardComboComparator comparator = new CardComboComparator();
        result.sort(comparator);
        return result;
    }

    public List<DPDecks> getDecks() {
        List<DPDecks> result = new ArrayList<>(decks);
        DPDecksComparator comparator = new DPDecksComparator();
        result.sort(comparator);
        return result;
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
