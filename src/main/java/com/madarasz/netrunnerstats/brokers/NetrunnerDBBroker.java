package com.madarasz.netrunnerstats.brokers;

import com.madarasz.netrunnerstats.DOs.Card;
import com.madarasz.netrunnerstats.DOs.CardSet;
import com.madarasz.netrunnerstats.DRs.CardSetRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by madarasz on 2015-06-09.
 */
@Component
public final class NetrunnerDBBroker {
    private final static String NETRUNNERDB_API_URL = "http://netrunnerdb.com/api/";

    @Autowired
    private CardSetRepository cardSetRepository;

    public NetrunnerDBBroker() {

    }

    public Set<CardSet> readSets() {
        JSONArray setsData = new JSONObject(JSONBroker.readJSONFromUrl(NETRUNNERDB_API_URL + "sets"))
                .getJSONArray("input");
        Set<CardSet> resultSet = new HashSet<CardSet>();

        for (int i = 0; i < setsData.length(); i++) {
            JSONObject setData = setsData.getJSONObject(i);
            CardSet cardSet = new CardSet(setData.getString("name"), setData.getString("code"),
                    setData.getInt("number"), setData.getInt("cyclenumber"));
            resultSet.add(cardSet);
        }
        return resultSet;
    }

    public Set<Card> readCards() {
        JSONArray cardsData = new JSONObject(JSONBroker.readJSONFromUrl(NETRUNNERDB_API_URL + "cards"))
                .getJSONArray("input");
        Set<Card> resultSet = new HashSet<Card>();

        for (int i = 0; i < cardsData.length(); i++) {

            SafeJSONObject cardData = new SafeJSONObject(cardsData.getJSONObject(i));

            CardSet cardSet = cardSetRepository.findByCode(cardData.getString("set_code"));

            Card card = new Card(cardData.getInt("code"), cardData.getString("title"), cardData.getString("type_code"), cardData.getString("subtype_code"),
                    cardData.getString("text"), cardData.getString("faction_code"), cardData.getString("side_code"), cardData.getBoolean("uniqueness"),
                    cardData.getBoolean("limited"), cardSet, cardData.getInt("baselink"), cardData.getInt("influencelimit"), cardData.getInt("minnimumdecksize"),
                    cardData.getInt("cost"), cardData.getInt("factioncost"), cardData.getInt("memoryunits"), cardData.getInt("strenght"),
                    cardData.getInt("advancementcost"), cardData.getInt("agengapoints"), cardData.getInt("trash"));
            resultSet.add(card);
        }

        return resultSet;
    }


}