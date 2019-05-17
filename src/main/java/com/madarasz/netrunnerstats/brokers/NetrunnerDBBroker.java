package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.CardCycle;
import com.madarasz.netrunnerstats.database.DOs.CardPack;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DOs.relationships.DeckHasCard;
import com.madarasz.netrunnerstats.database.DRs.CardCycleRepository;
import com.madarasz.netrunnerstats.database.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import com.madarasz.netrunnerstats.database.DRs.DeckRepository;
import com.madarasz.netrunnerstats.helper.SafeJSONObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Handling netrunnerdb.com stuff
 * Created by madarasz on 2015-06-09.
 */
@Component
public final class NetrunnerDBBroker {

    private final static String NETRUNNERDB_API_URL = "https://netrunnerdb.com/api/2.0/public/";
    private final static String NETRUNNERDB_PRIVATEDECK_URL = "https://netrunnerdb.com/en/deck/view/";
    private final static String NETRUNNERDB_DECKLIST_URL = "https://netrunnerdb.com/en/decklist/";

    private static final Logger logger = LoggerFactory.getLogger(NetrunnerDBBroker.class);

    @Autowired
    private CardPackRepository cardPackRepository;

    @Autowired
    private CardCycleRepository cardCycleRepository;

    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private  HttpBroker httpBroker;

    @Autowired
    private DeckRepository deckRepository;

    public NetrunnerDBBroker() {

    }

    public Set<CardPack> readSets() {
        JSONArray setData = new JSONObject(httpBroker.readFromUrl(NETRUNNERDB_API_URL + "packs", false))
                .getJSONArray("data");
        Set<CardPack> resultSet = new HashSet<>();

        for (int i = 0; i < setData.length(); i++) {
            JSONObject packData = setData.getJSONObject(i);
            CardCycle cardCycle = cardCycleRepository.findByCode(packData.getString("cycle_code"));
            CardPack cardPack = new CardPack(packData.getString("name"), packData.getString("code"),
                    packData.getInt("position"), cardCycle.getCyclenumber());
            resultSet.add(cardPack);
        }

        // add R&R MWL 2.2 and Downfall MWL 3.2
        resultSet.add(new CardPack("D&D, post-MWL", "dadmwl", 2, 9));
        resultSet.add(new CardPack("R&R MWL 2.2", "rar2", 2, 22));
        resultSet.add(new CardPack("Downfall MWL 3.2", "df2", 2, 26));

        return resultSet;
    }

    public Set<CardCycle> readCycles() {
        JSONArray cycleData = new JSONObject(httpBroker.readFromUrl(NETRUNNERDB_API_URL + "cycles", false))
                .getJSONArray("data");
        Set<CardCycle> resultSet = new HashSet<>();

        for (int i = 0; i < cycleData.length(); i++) {
            JSONObject cycle = cycleData.getJSONObject(i);
            CardCycle cardCycle = new CardCycle(cycle.getString("name"), cycle.getString("code"),
                    cycle.getInt("position"));
            resultSet.add(cardCycle);
        }
        return resultSet;
    }

    public Set<Card> readCards() {
        JSONArray cardsData = new JSONObject(httpBroker.readFromUrl(NETRUNNERDB_API_URL + "cards", false))
                .getJSONArray("data");
        Set<Card> resultSet = new HashSet<>();

        for (int i = 0; i < cardsData.length(); i++) {

            SafeJSONObject cardData = new SafeJSONObject(cardsData.getJSONObject(i));

            CardPack cardPack = cardPackRepository.findByCode(cardData.getString("pack_code"));
            if (cardPack == null) {
                logger.error("Cannot find cardpack for: " + cardData.getString("pack_code") + " - " + cardData.getString("title"));
            }

            Card card = new Card(cardData.getString("code"), cardData.getString("title"), cardData.getString("type_code"), cardData.getString("keywords"),
                    cardData.getString("text"), cardData.getString("faction_code"), cardData.getString("side_code"), cardData.getBoolean("uniqueness"),
                    cardData.getInt("deck_limit"), cardPack, cardData.getInt("base_link"), cardData.getInt("influence_limit"), cardData.getInt("minimum_deck_size"),
                    cardData.getInt("cost"), cardData.getInt("faction_cost"), cardData.getInt("memory_cost"), cardData.getInt("strength"),
                    cardData.getInt("advancement_cost"), cardData.getInt("agenda_points"), cardData.getInt("trash_cost"));
            resultSet.add(card);
        }

        return resultSet;
    }

    public String deckApiUrlFromId(int deckid, boolean published) {
        if (published) {
            return NETRUNNERDB_API_URL + "decklist/" + deckid;
        } else {
            return NETRUNNERDB_API_URL + "deck/" + deckid;
        }
    }

    public String deckViewUrlFromId(int deckid, boolean published) {
        if (published) {
            return NETRUNNERDB_DECKLIST_URL + deckid;
        } else {
            return NETRUNNERDB_PRIVATEDECK_URL + deckid;
        }
    }

    public Deck readDeck(int deckid) {
        return readDeck(deckid, true);
    }

    public Deck readDeck(int deckid, boolean published) {
        JSONObject deckData;
        Deck resultDeck = deckRepository.findByUrl(deckApiUrlFromId(deckid, published));

        if (resultDeck != null) {
            logger.trace("Deck already in DB: " + resultDeck.toString());
            return resultDeck;
        }

        String deckurl = deckViewUrlFromId(deckid, published),
            apiurl = deckApiUrlFromId(deckid, published);

        try {
            deckData = new JSONObject(httpBroker.readFromUrl(apiurl, false)).getJSONArray("data").getJSONObject(0);
            resultDeck = new Deck(deckData.getString("name"), deckData.getString("user_name"), deckurl);
        } catch (Exception ex) {
            logger.error("Could not read deck: " + deckurl);
            return null;
        }

        JSONObject cards = deckData.getJSONObject("cards");
        Iterator<?> cardData = cards.keys();
        while (cardData.hasNext()) {
            String key = (String) cardData.next();
            Card card = cardRepository.findByCode(key);
            if (card != null) {
                if (card.isIdentity()) {
                    resultDeck.setIdentity(card);
                } else {
                    int quantity = cards.getInt(key);
                    resultDeck.hasCard(card, quantity);
                }
            } else {
                logger.error("Cound not find card for code: " + key + " - " + deckurl);
            }
        }

        return updateDeckWithCore2(resultDeck);
    }

    public Deck updateDeckWithCore2(Deck deck) {
        deck.setIdentity(updateCardWithCore2(deck.getIdentity()));

        Set<DeckHasCard> oldcards = new HashSet<>(deck.getCards());
        deck.removeAllCards();

        for (DeckHasCard deckHasCard : oldcards) {
            deck.hasCard(updateCardWithCore2(deckHasCard.getCard()), deckHasCard.getQuantity());
        }

        deck.calculateUpto();
        return deck;
    }

    /**
     * Returns Core2 substitute for a card if possible.
     * @param card
     * @return
     */
    public Card updateCardWithCore2(Card card) {
        String title = card.getTitle();
        if (title.contains(" (old)")) {
            Card substitute = cardRepository.findByTitle(title.split(" \\(old\\)")[0]);
            if (substitute != null) {
                // logger.info("Substituting " + title + " with " + substitute.getTitle());
                return substitute;
            } else {
                logger.error("Could not find substitute for: " + title);
            }
        }
        return card;
    }
}