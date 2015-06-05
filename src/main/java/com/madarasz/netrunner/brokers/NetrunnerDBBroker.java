package com.madarasz.netrunner.brokers;

import com.madarasz.netrunner.DOs.node.CardSet;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by madarasz on 05/06/15.
 */
public final class NetrunnerDBBroker {
    private final static String NETRUNNERDB_API_URL = "http://netrunnerdb.com/api/";

    private NetrunnerDBBroker() {

    }

    public static int readSets() {
        JSONArray setsData = new JSONObject(JSONBroker.readJSONFromUrl(NETRUNNERDB_API_URL + "sets"))
                .getJSONArray("input");
        for (int i = 0; i < setsData.length(); i++) {
            JSONObject setData = setsData.getJSONObject(i);
            CardSet cardSet = new CardSet(setData.getString("name"), setData.getString("code"),
                    setData.getInt("number"), setData.getInt("cyclenumber"));
            System.out.println(cardSet.toString());
        }
        return setsData.length();
    }


}
