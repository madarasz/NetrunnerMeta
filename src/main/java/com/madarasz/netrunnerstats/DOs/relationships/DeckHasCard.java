package com.madarasz.netrunnerstats.DOs.relationships;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.Deck;
import org.springframework.data.neo4j.annotation.*;

/**
 * Created by madarasz on 2015-06-09.
 */
@RelationshipEntity(type = "HAS_CARD")
public class DeckHasCard {
    @GraphId Long id;
    @StartNode @Fetch Deck deck;
    @EndNode Card card;
    private int quantity;

    public DeckHasCard() {
    }

    public DeckHasCard(Deck deck, Card card, int quantity) {
        this.deck = deck;
        this.card = card;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Deck getDeck() {
        return deck;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Deck %s has %s %s", deck.getName(), quantity, card.getTitle());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeckHasCard deckHasCard = (DeckHasCard) o;
        if (id == null) return super.equals(o);
        return id.equals(deckHasCard.id);
    }
}
