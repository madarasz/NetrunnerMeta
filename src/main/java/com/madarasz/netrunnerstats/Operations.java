package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DOs.Tournament;
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
 * Handling all the possible operations of the application.
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

    public void loadNetrunnerDB() {
        Set<CardPack> allCardPacks = netrunnerDBBroker.readSets();
        int found = 0;
        for (CardPack cardPack : allCardPacks) {
            if (cardPackRepository.findByCode(cardPack.getCode()) == null) {
                cardPackRepository.save(cardPack);
//                System.out.println("Found pack: " + cardPack.toString());
                found++;
            }
        }
        System.out.println("Found new card packs: " + found);

        Set<Card> allCards = netrunnerDBBroker.readCards();
        found = 0;
        for (Card card : allCards) {
            if (cardRepository.findByTitle(card.getTitle()) == null) {
                cardRepository.save(card);
//                System.out.println("Found card: " + card.toString());
                found++;
            }
        }
        System.out.println("Found new cards: " + found);
    }

    public void loadNetrunnerDbDeck(int deckId) {
        String url = netrunnerDBBroker.deckUrlFromId(deckId);
        Deck exists = deckRepository.findByUrl(url);
        if (exists != null) {
            System.out.println("Deck is already in DB. Not saving!");
        } else {
            Deck deck = netrunnerDBBroker.readDeck(deckId);
            System.out.println("Saving new deck!");
            System.out.println(deck.toString());
            deckRepository.save(deck);
        }
    }

    public void loadAcooDeck(int deckId) {
        String url = acooBroker.deckUrlFromId(deckId);
        Deck exists = deckRepository.findByUrl(url);
        if (exists != null) {
            System.out.println("Deck is already in DB. Not saving!");
        } else {
            Deck deck = acooBroker.readDeck(deckId);
            System.out.println("Saving new deck!");
            System.out.println(deck.toString());
            deckRepository.save(deck);
        }
    }

    public void loadAcooTournament(int tournamentId) {
        Tournament tournament = acooBroker.readTournament(tournamentId);
        System.out.println(tournament.toString());
    }
}
