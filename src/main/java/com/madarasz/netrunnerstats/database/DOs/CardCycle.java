package com.madarasz.netrunnerstats.database.DOs;

import com.madarasz.netrunnerstats.helper.comparator.CardPackComparator;
import org.springframework.data.neo4j.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Node for Netrunner data cycles
 * Created by madarasz on 2016-08-25.
 */
@NodeEntity
public class CardCycle {
    @GraphId Long id;
    @Indexed(unique=true) private String name;
    @RelatedTo(type = "HAS_PACK") private @Fetch Set<CardPack> cardPacks;
    private String code;
    private int cyclenumber;

    public CardCycle() {
        this.cardPacks = new HashSet<>();
    }

    public CardCycle(String name, String code, int cyclenumber) {
        this.name = name;
        this.code = code;
        this.cyclenumber = cyclenumber;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getCyclenumber() {
        return cyclenumber;
    }

    public Set<CardPack> getCardPacks() {
        return cardPacks;
    }

    public List<CardPack> getSortedCardpacks() {
        CardPackComparator comparator = new CardPackComparator();
        List<CardPack> result = new ArrayList<>(cardPacks);
        result.sort(comparator);
        return result;
    }

    public void addCardPack (CardPack newCardPack) {
        cardPacks.add(newCardPack);
    }

    @Override
    public String toString() {
        return name;
    }
}
