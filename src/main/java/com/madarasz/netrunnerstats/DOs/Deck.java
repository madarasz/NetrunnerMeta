package com.madarasz.netrunnerstats.DOs;

import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import org.springframework.data.neo4j.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by madarasz on 2015-06-09.
 */
@NodeEntity
public class Deck {
    @GraphId Long id;
    @RelatedToVia(type = "HAS_CARD") private @Fetch Set<DeckHasCard> cards;
    private String name;
    private String player;
    private String url;
    @RelatedTo(type = "IDENTITY") private @Fetch Card identity;
    @RelatedTo(type = "UP_TO") private @Fetch CardPack upto;

    public Deck() {
        cards = new HashSet<DeckHasCard>();
    }

    public Deck(String name, String player, String url) {
        this.name = name;
        this.player = player;
        this.url = url;
        cards = new HashSet<DeckHasCard>();
    }

    public DeckHasCard hasCard(Card card, int quantity) {
        DeckHasCard deckHasCard = new DeckHasCard(this, card, quantity);
        this.cards.add(deckHasCard);
        return deckHasCard;
    }

    public Set<DeckHasCard> getCards() {
        return cards;
    }

    public String getName() {
        return name;
    }

    public String getPlayer() {
        return player;
    }

    public String getUrl() {
        return url;
    }

    public Card getIdentity() {
        return identity;
    }

    public void setIdentity(Card identity) {
        this.identity = identity;
    }

    public CardPack getUpto() {
        if (upto == null) {
            calculateUpto();
        }
        return upto;
    }

    public void calculateUpto() {
        CardPack result = new CardPack("dummy", "dummy", 0, 0);
        for (DeckHasCard deckHasCard : cards) {
            CardPack cardPack = deckHasCard.getCard().getCardPack();
            if (cardPack.later(result)) {
                result = cardPack;
            }
        }
        upto = result;
    }

    // TODO
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("%s - cards up to: %s - %s", name, upto.toString(), url);
    }
}
