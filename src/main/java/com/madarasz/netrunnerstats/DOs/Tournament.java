package com.madarasz.netrunnerstats.DOs;

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
    private int id;  // -1 for Stimhack tournaments
    private String name;
    private Date date;
    private int playerNumber;
    @Indexed(unique=true) private String url;
    @RelatedTo(type = "POOL") private @Fetch CardPack cardpool;
    @RelatedTo(type = "IS_STANDING") @Fetch private Set<Standing> standings;  // TODO: is this needed? Standing has IN_TOURNAMENT

    public Tournament() {
    }

    public Tournament(int id, String name, Date date, CardPack cardpool, String url, int playerNumber) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.cardpool = cardpool;
        this.url = url;
        this.playerNumber = playerNumber;
//        standings = new HashSet<Standing>();
    }

//    public Standing hasStanding(int rank, Card identity, boolean top_deck, boolean is_runner) {
//        Standing standing = new Standing(this, rank, identity, top_deck, is_runner);
//        this.standings.add(standing);
//        return standing;
//    }
//
//    public Standing hasStanding(int rank, Card identity, boolean top_deck, boolean is_runner, Deck deck) {
//        Standing standing = new Standing(this, rank, identity, top_deck, is_runner, deck);
//        this.standings.add(standing);
//        return standing;
//    }
//
//    public void hasStandings(Set<Standing> standings) {
//        for (Standing standing : standings) {
//            this.standings.add(standing);
//        }
//    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public CardPack getCardpool() {
        if (cardpool != null) {
            return cardpool;
        } else {
            cardpool = guessCardPool();
            return cardpool;
        }
    }

    public void setCardpool(CardPack cardpool) {
        this.cardpool = cardpool;
    }

//    public Set<Standing> getStandings() {
//        return standings;
//    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Tries to guess tournament cardpool based on cards used.
     * @return latest card pack used in tournament
     */
    // TODO: Refactor on IS_STANDING vs IN_TOURNAMENT
    public CardPack guessCardPool() {
        CardPack result = new CardPack("dummy", "dummy", 0, 0);
        for (Standing standing : standings) {
            if (standing.getDeck() != null) {
                if (standing.getDeck().getUpto().later(result)) {
                    result = standing.getDeck().getUpto();
                }
            } else {
                if (standing.getIdentity().getCardPack().later(result)) {
                    result = standing.getIdentity().getCardPack();
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return String.format("%s (%s) - %d players - cardpool: %s - %s", name, format.format(date), playerNumber, getCardpool().getName(), url);
    }
}
