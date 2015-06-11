package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import com.madarasz.netrunnerstats.DRs.DeckRepository;
import com.madarasz.netrunnerstats.brokers.AcooBroker;
import com.madarasz.netrunnerstats.brokers.NetrunnerDBBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by istvan on 2015-06-11.
 */
@Component
public class Operations {

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardPackRepository cardPackRepository;

    @Autowired
    DeckRepository deckRepository;

    @Autowired
    NetrunnerDBBroker netrunnerDBBroker;

    @Autowired
    AcooBroker acooBroker;

    @Autowired
    Neo4jOperations template;

    public void cleanDB() {
        System.out.println("Cleaning DB.");
        Map<String, Object> emptyparams = new HashMap<String, Object>();
        template.query("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r", emptyparams);
        logDBCount();
    }

    public void logDBCount() {
        System.out.println(String.format("Cards: %d, CardPacks: %d, Decks: %d, Deck-card relations: %d",
                template.count(Card.class), template.count(CardPack.class), template.count(Deck.class), template.count(DeckHasCard.class)));
    }

    public void loadNetrunnerDB(boolean merge) {
        Set<CardPack> allCardPacks = netrunnerDBBroker.readSets();
        int found = 0;
        for (CardPack cardPack : allCardPacks) {
            if ((!merge) || (cardPackRepository.findByCode(cardPack.getCode()) == null)) {
                cardPackRepository.save(cardPack);
                System.out.println("Found pack: " + cardPack.toString());
                found++;
            }
        }
        System.out.println("Found new card packs: " + found);

        Set<Card> allCards = netrunnerDBBroker.readCards();
        found = 0;
        for (Card card : allCards) {
            if ((!merge) || (cardRepository.findByTitle(card.getTitle()) == null)) {
                cardRepository.save(card);
                System.out.println("Found card: " + card.toString());
                found++;
            }
        }
        System.out.println("Found new cards: " + found);
    }

    public void testDb() {
        logDBCount();

        CardPack whatset = cardPackRepository.findByName("Core Set");
        if (whatset != null) {
            System.out.println(whatset.toString());
        } else {
            System.out.println("Card pack not found!");
        }

        Card whatcard = cardRepository.findByTitle("Account Siphon");
        if (whatcard != null) {
            System.out.println(whatcard.toString());
        } else {
            System.out.println("Card not found!");
        }

        Deck whatdeck = deckRepository.findByUrl("http://netrunnerdb.com/api//en/decklist/20162");
        if (whatdeck != null) {
            System.out.println(whatdeck.toString());
        } else {
            System.out.println("NetrunnerDB Deck not found!");
        }

        Deck whatdeck2 = deckRepository.findByUrl("http://www.acoo.net/deck/10890");
        if (whatdeck2 != null) {
            System.out.println(whatdeck2.toString());
        } else {
            System.out.println("Acoo deck not found!");
        }
    }

    public void loadNetrunnerDbDeck(int deckId) {
        Deck deck = netrunnerDBBroker.readDeck(deckId);
        System.out.println(deck.toString());
        deckRepository.save(deck);
    }

    public void loadAcooDeck(int deckId) {
        Deck deck = acooBroker.readDeck(deckId);
        System.out.println(deck.toString());
        deckRepository.save(deck);
    }
}
