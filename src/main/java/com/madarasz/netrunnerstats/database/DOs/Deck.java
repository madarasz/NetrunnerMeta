package com.madarasz.netrunnerstats.database.DOs;

import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;
import org.springframework.data.neo4j.annotation.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Node for Netrunner decks
 * Created by madarasz on 2015-06-09.
 */
@NodeEntity
public class Deck {
    @GraphId Long id;
    @RelatedToVia(type = "HAS_CARD") @Fetch private Set<DeckHasCard> cards;
    private String name;
    private String player;
    @Indexed(unique=true) private String url;
    @RelatedTo(type = "IDENTITY") @Fetch private Card identity;
    @RelatedTo(type = "UP_TO") @Fetch private CardPack upto;

    public Deck() {
        this.cards = new HashSet<>();
    }

    public Deck(String name, String player, String url) {
        this.name = name;
        this.player = player;
        this.url = url;
        this.cards = new HashSet<>();
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

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getNumberofCards() {
        int result = 0;
        for (DeckHasCard deckHasCard : cards) {
            result += deckHasCard.getQuantity();
        }
        return result;
    }

    public int countCards() {
        int decksize = 0;
        for (DeckHasCard card : cards)
        {
            decksize += card.getQuantity();
        }
        return decksize;
    }

    public boolean equals(Deck deck) {
        if (!identity.getCode().equals(deck.getIdentity().getCode())) {
            return false;
        }
        if ((id.equals(deck.id)) || (url.equals(deck.getUrl()))) {
            return true;
        }

        Set<DeckHasCard> deck1 = new HashSet<>(cards);
        Set<DeckHasCard> deck2 = new HashSet<>(deck.getCards());
        for (DeckHasCard hasCard : cards) {
            for (DeckHasCard hasCard2 : deck.getCards()) {
                if ((hasCard.getCard().getCode().equals(hasCard2.getCard().getCode())) &&
                        (hasCard.getQuantity() == hasCard2.getQuantity())) {
                    deck1.remove(hasCard);
                    deck2.remove(hasCard2);
                }
            }
        }
        return ((deck1.isEmpty())) &&
                (deck2.isEmpty());
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %d cards up to: %s - %s", name, identity.toString(), getNumberofCards(), getUpto().toString(), url);
//        return name;
    }
}
