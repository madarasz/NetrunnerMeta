package com.madarasz.netrunnerstats.DOs.relationships;

import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.Tournament;
import org.springframework.data.neo4j.annotation.*;

/** Relataionship between tournaments and decks
 * Created by madarasz on 2015-06-12.
 */
@RelationshipEntity(type = "RANKING")
public class TournamentHasDeck {
    @GraphId Long id;
    @StartNode @Fetch private Tournament tournament;
    @EndNode @Fetch private Deck deck;
    private int rank;
    private String side_code;
    private boolean impact = true;
//
    public TournamentHasDeck() {
    }

    public TournamentHasDeck(Tournament tournament, Deck deck, int rank, String side_code) {
        this.tournament = tournament;
        this.deck = deck;
        this.rank = rank;
        this.side_code = side_code;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Deck getDeck() {
        return deck;
    }

    public int getRank() {
        return rank;
    }

    public String getSide_code() {
        return side_code;
    }

    public void calculateImpact() {
        impact = true; // TODO
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TournamentHasDeck tournamentHasDeck = (TournamentHasDeck) o;
        if (id == null) return super.equals(o);
        return id.equals(tournamentHasDeck.id);
    }

    @Override
    public String toString() {
        return String.format("Tournament %s has deck %s (rank: %d)", tournament.getName(), deck.getName(), rank);
    }
}
