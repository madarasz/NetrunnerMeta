package com.madarasz.netrunnerstats.helper;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by madarasz on 11/17/15.
 * Checks the validity of a deck
 */
@Component
public class DeckValidator {

    private static final Logger logger = LoggerFactory.getLogger(DeckValidator.class);
    private static final List<String> MWLTitles =
            Arrays.asList("Cerberus \"Lady\" H1", "Clone Chip", "Desperado", "Parasite",
                    "Prepaid VoicePAD", "Yog.0",
                    "Architect", "AstroScript Pilot Program", "Eli 1.0", "NAPD Contract", "SanSan City Grid");
    private static final String ALLIANCE = "alliance";

    public DeckValidator() {
    }

    public boolean isInfluenceOK(Deck deck, boolean MWL) {
        int infLimit = deck.getIdentity().getInfluencelimit();
        if (MWL) {
            for (DeckHasCard deckHasCard : deck.getCards()) {
                if (MWLTitles.contains(deckHasCard.getCard().getTitle())) {
                    infLimit -= deckHasCard.getQuantity();
                }
            }
            if (infLimit < 1) {
                infLimit = 1;
            }
        }
//        logger.info(String.format("Deck - %s - %d / %d", deck.getName(), deck.getInfluenceCount(), infLimit));
        return (getInfluenceCount(deck) <= infLimit);
    }

    public String isValidDeck(Deck deck) {
        return isValidDeck(deck, false);
    }

    public int countFactionNonAlliance(Deck deck, String factionCode) {
        int result = 0;
        for (DeckHasCard deckHasCard : deck.getCards()) {
            Card card = deckHasCard.getCard();
            if ((card.getFaction_code().equals(factionCode)) && (!card.getSubtype_code().contains(ALLIANCE))) {
                result += deckHasCard.getQuantity();
            }
        }
        return result;
    }

    public int countType(Deck deck, String typeCode) {
        int result = 0;
        for (DeckHasCard deckHasCard : deck.getCards()) {
            if (deckHasCard.getCard().getType_code().contains(typeCode)) {
                result += deckHasCard.getQuantity();
            }
        }
        return result;
    }

    /**
     * Count deck influence
     * @return influence count
     */
    public int getInfluenceCount(Deck deck) {
        final String[] ALLIANCE_6_NAMES = { "Executive Search Firm", "Heritage Committee", "Ibrahim Salem",
            "Jeeves Model Bioroids", "Raman Rai", "Salem's Hospitality" };
        final String ALLIANCE_ICE = "Mumba Temple";
        final String ALLIANCE_SIZE = "Museum of History";
        final String PROFESSORCODE = "03029";

        Card identity = deck.getIdentity();
        if (identity == null) return 0;
        int result = 0;
        String faction_code = identity.getFaction_code();

        if (identity.getCode().equals(PROFESSORCODE)) {  // The professor
            for (DeckHasCard deckHasCard : deck.getCards()) {
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
            for (DeckHasCard deckHasCard : deck.getCards()) {
                Card card = deckHasCard.getCard();
                if (!faction_code.equals(card.getFaction_code())) {
                    if (card.getSubtype_code().contains(ALLIANCE)) { // allience cards
                        if (((Arrays.asList(ALLIANCE_6_NAMES).contains(card.getTitle())) &&
                                (countFactionNonAlliance(deck, card.getFaction_code()) < 6)) ||
                            ((card.getTitle().equals(ALLIANCE_ICE))) && (countType(deck, "ice") > 15) ||
                            ((card.getTitle().equals(ALLIANCE_SIZE)) && (deck.countCards() < 50 ))) {
                                    result += card.getFactioncost() * deckHasCard.getQuantity();
                        }
                    } else {    // normal cards
                        result += card.getFactioncost() * deckHasCard.getQuantity();
                    }
                }
            }
        }

        return result;
    }


    /**
     * Check deck validity
     * @return validity
     */
    public String isValidDeck(Deck deck, boolean MWL) {
        String validity = "";
        Card identity = deck.getIdentity();

        // deck size
        int decksize = deck.countCards();
        if (decksize < identity.getMinimumdecksize()) {
            validity += String.format("ERROR - card count: %d\n", decksize);
        }

        // influence check
        int influence = getInfluenceCount(deck);
        if (!isInfluenceOK(deck, MWL)) {
            validity += String.format("ERROR - influence count: %d\n", influence);
        }

        String side = identity.getSide_code();
        int agendaCount = 0;
        boolean isApex = identity.getTitle().equals("Apex: Invasive Predator");
        for (DeckHasCard deckHasCard : deck.getCards()) {
            int quantity = deckHasCard.getQuantity();
            Card card = deckHasCard.getCard();

            // card quantity
            if ((quantity <1) ||
                    ((!card.isLimited()) && (!card.getSubtype_code().contains("consumer-grade")) && (quantity > 3)) ||
                    ((card.isLimited()) && (quantity > 1)) ||
                    ((card.getSubtype_code().contains("consumer-grade")) && (quantity > 6))){
                validity += String.format("ERROR - illegal card quantity - %dx %s\n", quantity, card.getTitle());
            }

            // same side
            if (!card.getSide_code().equals(side)) {
                validity += String.format("ERROR - illegal side - %s\n", card.getTitle());
            }

            // agenda count
            if (card.getType_code().equals("agenda")) {
                agendaCount += card.getAgendapoints() * quantity;
            }

            // Apex limitation
            if ((isApex) && (card.getType_code().equals("resource"))
                    && (!card.getSubtype_code().contains("virtual"))) {
                validity += String.format("ERROR - Apex can only have virtual resources - %s\n", card.getTitle());
            }
        }

        // agenda count
        if (side.equals("corp")) {
            int size = (int) Math.floor((decksize - 40) / 5);
            int top = size * 2 + 19;
            int bottom = size * 2 + 18;
            if ((agendaCount < bottom) || (agendaCount > top)) {
                validity += String.format("ERROR - deck size: %d - agenda count: %d (%d-%d)\n",
                        decksize, agendaCount, bottom, top);
            }
        }

        if (!validity.equals("")) {
            logger.warn(validity + "\nValidity error occurred with deck: " + deck.getUrl());
        }
        return validity;
    }

}
