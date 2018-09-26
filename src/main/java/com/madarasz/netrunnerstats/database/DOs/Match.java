package com.madarasz.netrunnerstats.database.DOs;

import org.springframework.data.neo4j.annotation.*;

/**
 * Node for Netrunner matches from NRTM json
 * Created by madarasz on 2017-10-25.
 */
@NodeEntity
public class Match {
    @GraphId private Long id;
    @RelatedTo(type = "IN_TOURNAMENT") @Fetch private Tournament tournament;
    @RelatedTo(type = "WINNER") @Fetch private Card winner;
    @RelatedTo(type = "LOSER") @Fetch private Card loser;
    private boolean timed;
    private boolean tie;
    private boolean elimination;
    private int table;
    private int round;

    public Match() {
    }

    public Match(Tournament tournament, Card winner, Card loser, boolean timed, boolean tie,
                 boolean elimination, int table, int round) {
        this.tournament = tournament;
        this.winner = winner;
        this.loser = loser;
        this.timed = timed;
        this.tie = tie;
        this.elimination = elimination;
        this.table = table;
        this.round = round;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Card getWinner() {
        if (this.tie) {
            return null;
        }
        return winner;
    }

    public Card getLoser() {
        if (this.tie) {
            return null;
        }
        return loser;
    }

    public Card getPlayer(int index) {
        if (index == 1) {
            return this.winner;
        } else {
            return this.loser;
        }
    }

    public boolean isTimed() {
        return timed;
    }

    public boolean isTie() {
        return tie;
    }

    public boolean isElimination() {
        return elimination;
    }

    public int getRound() {
        return round;
    }

    public int getTable() {
        return table;
    }
}
