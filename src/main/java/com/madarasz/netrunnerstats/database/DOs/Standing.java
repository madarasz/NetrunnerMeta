package com.madarasz.netrunnerstats.database.DOs;

import org.springframework.data.neo4j.annotation.*;

/**
 * Node for tournament standings
 * Created by madarasz on 2015.08.10..
 */
@NodeEntity
public class Standing {
    @GraphId
    Long id;
    @RelatedTo(type = "IN_TOURNAMENT") @Fetch private Tournament tournament;
    private int rank;
    @RelatedTo(type = "IS_IDENTITY") @Fetch private Card identity;
    @Indexed(unique=false) private boolean topdeck;
    @RelatedTo(type = "IS_DECK") @Fetch private Deck deck;
    @Indexed(unique=false) private boolean is_runner;

    public Standing() {
    }

    // without linked deck
    public Standing(Tournament tournament, int rank, Card identity, boolean topdeck, boolean is_runner) {
        this.tournament = tournament;
        this.rank = rank;
        this.identity = identity;
        this.topdeck = topdeck;
        this.is_runner = is_runner;
    }

    // with linked deck
    public Standing(Tournament tournament, int rank, Card identity, boolean topdeck, boolean is_runner, Deck deck) {
        this.tournament = tournament;
        this.rank = rank;
        this.identity = identity;
        this.topdeck = topdeck;
        this.is_runner = is_runner;
        this.deck = deck;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public int getRank() {
        return rank;
    }

    public Card getIdentity() {
        return identity;
    }

    public boolean isTopdeck() {
        return topdeck;
    }

    public Deck getDeck() {
        return deck;
    }

    public boolean is_runner() {
        return is_runner;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void setIdentity(Card identity) {
        this.identity = identity;
    }

    @Override
    public String toString() {
        if (deck == null) {
            return String.format("%s - #%d %s", tournament.getUrl(), rank, identity.getTitle());
        } else {
            return String.format("%s - #%d %s (%s)", tournament.getUrl(), rank, identity.getTitle(), deck.getName());
        }
    }
}
