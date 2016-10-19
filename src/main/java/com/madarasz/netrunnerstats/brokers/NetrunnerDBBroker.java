package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DOs.CardCycle;
import com.madarasz.netrunnerstats.database.DOs.CardPack;
import com.madarasz.netrunnerstats.database.DOs.Deck;
import com.madarasz.netrunnerstats.database.DRs.CardCycleRepository;
import com.madarasz.netrunnerstats.database.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
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

    private static final Logger logger = LoggerFactory.getLogger(NetrunnerDBBroker.class);

    @Autowired
    private CardPackRepository cardPackRepository;

    @Autowired
    private CardCycleRepository cardCycleRepository;

    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private  HttpBroker httpBroker;

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

            Card card = new Card(cardData.getString("code"), cardData.getString("title"), cardData.getString("type_code"), cardData.getString("subtype_code"),
                    cardData.getString("text"), cardData.getString("faction_code"), cardData.getString("side_code"), cardData.getBoolean("uniqueness"),
                    cardData.getInt("limited"), cardPack, cardData.getInt("baselink"), cardData.getInt("influencelimit"), cardData.getInt("minimumdecksize"),
                    cardData.getInt("cost"), cardData.getInt("factioncost"), cardData.getInt("memoryunits"), cardData.getInt("strength"),
                    cardData.getInt("advancementcost"), cardData.getInt("agendapoints"), cardData.getInt("trash"));
            resultSet.add(card);
        }

        return resultSet;
    }

    public String deckUrlFromId(int deckid) {
        return NETRUNNERDB_API_URL + "deck/" + deckid;
    }

    public Deck readDeck(int deckid) {
        JSONObject deckData = new JSONObject(httpBroker.readFromUrl(deckUrlFromId(deckid), false)).getJSONObject("data");
        Deck resultDeck = new Deck(deckData.getString("name"), deckData.getString("username"), deckUrlFromId(deckid));

        JSONObject cards = deckData.getJSONObject("cards");
        Iterator<?> cardData = cards.keys();
        while (cardData.hasNext()) {
            String key = (String) cardData.next();
            Card card = cardRepository.findByCode(key);
            if (card.isIdentity()) {
                resultDeck.setIdentity(card);
            } else {
                int quantity = cards.getInt(key);
                resultDeck.hasCard(card, quantity);
            }
        }

        return resultDeck;
    }
}