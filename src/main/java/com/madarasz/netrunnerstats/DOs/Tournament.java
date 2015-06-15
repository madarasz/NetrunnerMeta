package com.madarasz.netrunnerstats.DOs;

import com.madarasz.netrunnerstats.DOs.relationships.TournamentHasDeck;
import org.springframework.data.neo4j.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
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
    private int playerNumber;
    @Indexed(unique=true) private String url;
    @RelatedTo(type = "POOL") private @Fetch CardPack cardpool;
    @RelatedToVia(type = "RANKING") @Fetch private Set<TournamentHasDeck> decks;

    public Tournament() {
    }

    public Tournament(int id, String name, Date date, CardPack cardpool, String url, int playerNumber) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.cardpool = cardpool;
        this.url = url;
        this.playerNumber = playerNumber;
        decks = new HashSet<TournamentHasDeck>();
    }

    public TournamentHasDeck hasDeck(Deck deck, int rank, String side_code) {
        TournamentHasDeck tournamentHasDeck = new TournamentHasDeck(this, deck, rank, side_code);
        this.decks.add(tournamentHasDeck);
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

    public Set<TournamentHasDeck> getDecks() {
        return decks;
    }

    @Override
    public String toString() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return String.format("%s (%s) - %d players - cardpool: %s - %s", name, format.format(date), playerNumber, cardpool.getName(), url);
    }
}
