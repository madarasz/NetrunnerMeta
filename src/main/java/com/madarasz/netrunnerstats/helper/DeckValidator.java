package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;
import org.springframework.stereotype.Component;

/**
 * Created by madarasz on 11/17/15.
 * Checks the validity of a deck
 */
@Component
public class DeckValidator {

    public DeckValidator() {
    }

    /**
     * Check deck validity
     * @return validity
     */
    public boolean isValidDeck(Deck deck) {
        boolean validity = true;
        Card identity = deck.getIdentity();

        // deck size
        int decksize = deck.countCards();
        if (decksize < identity.getMinimumdecksize()) {
            validity = false;
            System.out.println(String.format("ERROR - card count: %d", decksize));
        }

        // influence check
        int influence = deck.getInfluenceCount();
        if (influence > identity.getInfluencelimit()) {
            validity = false;
            System.out.println(String.format("ERROR - influence count: %d", influence));
        }

        String side = identity.getSide_code();
        int agendaCount = 0;
        boolean isApex = identity.getTitle().equals("Apex: Invasive Predator");
        for (DeckHasCard deckHasCard : deck.getCards()) {
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
            System.out.println("Validity error occurred with deck: " + deck.getUrl());
        }
        return validity;
    }

}
