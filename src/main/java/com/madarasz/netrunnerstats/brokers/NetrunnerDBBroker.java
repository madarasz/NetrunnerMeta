package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardPack;
import com.madarasz.netrunnerstats.DOs.Deck;
import com.madarasz.netrunnerstats.DRs.CardPackRepository;
import com.madarasz.netrunnerstats.DRs.CardRepository;
import com.madarasz.netrunnerstats.helper.SafeJSONObject;
import org.json.JSONArray;
import org.json.JSONObject;
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
    private final static String NETRUNNERDB_API_URL = "http://netrunnerdb.com/api/";

    @Autowired
    private CardPackRepository cardPackRepository;

    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private  HttpBroker httpBroker;

    public NetrunnerDBBroker() {

    }

    public Set<CardPack> readSets() {
        JSONArray setData = new JSONObject(httpBroker.readFromUrl(NETRUNNERDB_API_URL + "sets", true))
                .getJSONArray("input");
        Set<CardPack> resultSet = new HashSet<CardPack>();

        for (int i = 0; i < setData.length(); i++) {
            JSONObject packData = setData.getJSONObject(i);
            CardPack cardPack = new CardPack(packData.getString("name"), packData.getString("code"),
                    packData.getInt("number"), packData.getInt("cyclenumber"));
            resultSet.add(cardPack);
        }
        return resultSet;
    }

    public Set<Card> readCards() {
        JSONArray cardsData = new JSONObject(httpBroker.readFromUrl(NETRUNNERDB_API_URL + "cards", true))
                .getJSONArray("input");
        Set<Card> resultSet = new HashSet<Card>();

        for (int i = 0; i < cardsData.length(); i++) {

            SafeJSONObject cardData = new SafeJSONObject(cardsData.getJSONObject(i));

            CardPack cardPack = cardPackRepository.findByCode(cardData.getString("set_code"));

            Card card = new Card(cardData.getString("code"), cardData.getString("title"), cardData.getString("type_code"), cardData.getString("subtype_code"),
                    cardData.getString("text"), cardData.getString("faction_code"), cardData.getString("side_code"), cardData.getBoolean("uniqueness"),
                    cardData.getBoolean("limited"), cardPack, cardData.getInt("baselink"), cardData.getInt("influencelimit"), cardData.getInt("minimumdecksize"),
                    cardData.getInt("cost"), cardData.getInt("factioncost"), cardData.getInt("memoryunits"), cardData.getInt("strength"),
                    cardData.getInt("advancementcost"), cardData.getInt("agendapoints"), cardData.getInt("trash"));
            resultSet.add(card);
        }

        return resultSet;
    }

    public String deckUrlFromId(int deckid) {
        return NETRUNNERDB_API_URL + "decklist/" + deckid;
    }

    public Deck readDeck(int deckid) {
        JSONObject deckData = new JSONObject(httpBroker.readFromUrl(deckUrlFromId(deckid), false));
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