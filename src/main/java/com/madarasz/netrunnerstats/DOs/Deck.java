package com.madarasz.netrunnerstats.DOs;

import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
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

    /**
     * Count deck influence
     * @return influence count
     */
    public int getInfluenceCount() {
        if (identity == null) return 0;
        int result = 0;
        String faction_code = identity.getFaction_code();

        if (identity.getCode().equals("03029")) {  // The professor
            for (DeckHasCard deckHasCard : cards) {
                Card card = deckHasCard.getCard();
                if (!faction_code.equals(card.getFaction_code())) {
                    if (card.getType_code().equals("program")) {
                        result += card.getFactioncost() * (deckHasCard.getQuantity()-1);
                    } else {
                        result += card.getFactioncost() * deckHasCard.getQuantity();
                    }
                }
            }
        } else {    // not The professor
            for (DeckHasCard deckHasCard : cards) {
                Card card = deckHasCard.getCard();
                if (!faction_code.equals(card.getFaction_code())) {
                    result += card.getFactioncost() * deckHasCard.getQuantity();
                }
            }
        }

        return result;
    }

    /**
     * Check deck validity
     * @return validity
     */
    public boolean isValidDeck() {
        boolean validity = true;

        // deck size
        int decksize = 0;
        for (DeckHasCard card : cards)
        {
            decksize += card.getQuantity();
        }
        if (decksize < identity.getMinimumdecksize()) {
            validity = false;
            System.out.println(String.format("ERROR - card count: %d", decksize));
        }

        // influence check
        int influence = getInfluenceCount();
        if (getInfluenceCount() > identity.getInfluencelimit()) {
            validity = false;
            System.out.println(String.format("ERROR - influence count: %d", influence));
        }

        String side = identity.getSide_code();
        int agendaCount = 0;
        boolean isApex = identity.getTitle().equals("Apex: Invasive Predator");
        for (DeckHasCard deckHasCard : cards) {
            int quantity = deckHasCard.getQuantity();
            Card card = deckHasCard.getCard();

            // card quantity
            if ((quantity <1) ||
                    ((!card.isLimited()) && (quantity > 3)) ||
                    ((card.isLimited()) && (quantity > 1))) {
                validity = false;
                System.out.println(String.format("ERROR - illegal card quantity - %dx %s", quantity, card.getTitle()));
            }

            // same side
            if (!card.getSide_code().equals(side)) {
                validity = false;
                System.out.println(String.format("ERROR - illegal side - %s", card.getTitle()));
            }

            // agenda count
            if (card.getType_code().equals("agenda")) {
                agendaCount += card.getAgendapoints() * quantity;
            }

            // Apex limitation
            if ((isApex) && (card.getType_code().equals("resource"))
                    && (!card.getSubtype_code().contains("virtual"))) {
                validity = false;
                System.out.println(String.format("ERROR - Apex can only have virtual resources - %s", card.getTitle()));
            }
        }

        // agenda count
        if (side.equals("corp")) {
            int size = (int) Math.floor((decksize - 40) / 5);
            int top = size * 2 + 19;
            int bottom = size * 2 + 18;
            if ((agendaCount < bottom) || (agendaCount > top)) {
                validity = false;
                System.out.println(String.format("ERROR - deck size: %d - agenda count: %d (%d-%d)",
                        decksize, agendaCount, bottom, top));
            }
        }

        if (!validity) {
            System.out.println("Validity error occurred with deck: " + url);
        }
        return validity;
    }

    public boolean equals(Deck deck) {
        if (!identity.getCode().equals(deck.getIdentity().getCode())) {
            return false;
        }
        Set<DeckHasCard> deck1 = new HashSet<DeckHasCard>(cards);
        Set<DeckHasCard> deck2 = new HashSet<DeckHasCard>(deck.getCards());
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
        return String.format("%s (%s) - %d cards (%d inf) up to: %s - %s", name, identity.toString(), getNumberofCards(), getInfluenceCount(), getUpto().toString(), url);
//        return name;
    }
}
