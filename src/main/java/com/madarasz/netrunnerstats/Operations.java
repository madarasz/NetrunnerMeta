package com.madarasz.netrunnerstats;

import com.madarasz.netrunnerstats.DOs.*;
import com.madarasz.netrunnerstats.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.DOs.relationships.TournamentHasDeck;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import com.madarasz.netrunnerstats.DRs.DeckRepository;
import com.madarasz.netrunnerstats.DRs.TournamentRepository;
import com.madarasz.netrunnerstats.brokers.AcooBroker;
import com.madarasz.netrunnerstats.brokers.NetrunnerDBBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Component;
import java.util.*;

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
    TournamentRepository tournamentRepository;

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
        System.out.println(String.format("Cards: %d, CardPacks: %d, Decks: %d, Tournaments: %d, Deck-card relations: %d, Tournament-deck relations: %d",
                template.count(Card.class), template.count(CardPack.class), template.count(Deck.class), template.count(Tournament.class),
                template.count(DeckHasCard.class), template.count(TournamentHasDeck.class)));
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

    public Deck loadNetrunnerDbDeck(int deckId) {
        String url = netrunnerDBBroker.deckUrlFromId(deckId);
        Deck exists = deckRepository.findByUrl(url);
        if (exists != null) {
            System.out.println("Deck is already in DB. Not saving!");
            return exists;
        } else {
            Deck deck = netrunnerDBBroker.readDeck(deckId);
            System.out.println("Saving new deck! - " + deck.toString());
            deckRepository.save(deck);
            return deck;
        }
    }

    public Deck loadAcooDeck(int deckId) {
        String url = acooBroker.deckUrlFromId(deckId);
        Deck exists = deckRepository.findByUrl(url);
        if (exists != null) {
            System.out.println("Deck is already in DB. Not saving!");
            return exists;
        } else {
            Deck deck = acooBroker.readDeck(deckId);
            System.out.println("Saving new deck! - " + deck.toString());
            deckRepository.save(deck);
            return deck;
        }
    }

    public Tournament loadAcooTournament(int tournamentId) {
        String url = acooBroker.tournamentUrlFromId(tournamentId);
        Tournament exists = tournamentRepository.findByUrl(url);
        if (exists != null) {
            System.out.println("Tournament is already in DB. Not saving!");
            return exists;
        } else {
            Tournament tournament = acooBroker.readTournament(tournamentId);
            System.out.println("Saving new tournament! - " + tournament.toString());
            tournamentRepository.save(tournament);
            return tournament;
        }
    }

    public void loadAcooTournamentDecks(int tournamentId) {
        Tournament tournament = loadAcooTournament(tournamentId);
        Map<Integer, Integer> decks = acooBroker.loadTournamentDeckIds(tournamentId);
        Iterator iterator = decks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            int rank = (Integer)pair.getValue();
            System.out.print("Rank: " + rank + " - ");
            boolean topdeck = rank <= tournament.getPlayerNumber()*0.3;    // top 30% of decks
            if (topdeck) {
                System.out.print("TOP - ");
            }
            Deck deck = loadAcooDeck((Integer)pair.getKey());
            tournament.hasDeck(deck, rank, deck.getIdentity().getSide_code(), topdeck);
        }
        tournamentRepository.save(tournament);
    }

    public void loadAcooTournamentsFromUrl(String url, boolean paging, boolean filterempty) {
        System.out.println("*** Reading tournaments on page: " + url);
        Set<Integer> tournamentIds = acooBroker.loadTournamentIdsFromUrl(url, filterempty);
        for (int tournamentId : tournamentIds) {
            loadAcooTournamentDecks(tournamentId);
        }
        String pagination = acooBroker.getTournamentPageNextLink(url);
        if ((paging) && (!pagination.equals(""))) {
            loadAcooTournamentsFromUrl(acooBroker.ACOO_URL + pagination, true, filterempty);
        }
    }

    public void generateArchetype(String name, String cardpool, String identity, boolean filtertop) {
        List<Deck> deckList;
        if (filtertop) {
            deckList = deckRepository.filterTopByIdentityAndCardPool(identity, cardpool);
        } else {
            deckList = deckRepository.filterByIdentityAndCardPool(identity, cardpool);
        }
        Archetype archetype = new Archetype(name, deckList, cardRepository.findByTitle(identity));
        System.out.println(archetype.toString());
    }
}
