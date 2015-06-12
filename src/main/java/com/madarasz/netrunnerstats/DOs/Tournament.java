package com.madarasz.netrunnerstats.DOs;

import com.madarasz.netrunnerstats.DOs.relationships.TournamentHasDeck;
import org.springframework.data.neo4j.annotation.*;
import java.util.Date;
import java.util.Set;

/**
 * Node for tournaments
 * Created by madarasz on 2015-06-12.
 */
@NodeEntity
public class Tournament {
    @GraphId
    Long graphid;
    private int id;
    private String name;
    private Date date;
    @RelatedTo(type = "POOL") private @Fetch CardPack cardpool;
    @RelatedToVia(type = "HAS_DECK") @Fetch private Set<Deck> decks;

    public Tournament() {
    }

    public Tournament(int id, String name, Date date, CardPack cardpool) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.cardpool = cardpool;
    }

    public TournamentHasDeck hasDeck(Deck deck, int rank, String side_code) {
        TournamentHasDeck tournamentHasDeck = new TournamentHasDeck(this, deck, rank, side_code);
        this.decks.add(deck);
        return tournamentHasDeck;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public CardPack getCardpool() {
        return cardpool;
    }

    public Set<Deck> getDecks() {
        return decks;
    }
}
