package com.madarasz.netrunner.DOs;

import java.util.Set;

/**
 * Created by jenkins on 05/06/15.
 */
public class Deck {
    private int id;
    private Set<Card> cards;
    private Set<DeckURL> found_at;
    private DeckArchetype archetype;
    private Tournament tournament;
}
