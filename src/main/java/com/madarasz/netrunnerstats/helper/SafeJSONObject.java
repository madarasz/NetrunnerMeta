package com.madarasz.netrunnerstats.helper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Creating safe JSONObject class which does not throw exception on empty values
 * Created by madarasz on 2015-06-09.
 */
public class SafeJSONObject {
    private JSONObject jsonObject;

    public SafeJSONObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getString(String key) throws JSONException {
        try {
            return jsonObject.getString(key);
        } catch (JSONException ex) {
            return "";
        }
    }

    public int getInt(String key) throws JSONException {
        try {
            return jsonObject.getInt(key);
        } catch (JSONException ex) {
            return 0;
        }
    }

    public boolean getBoolean(String key) throws JSONException {
        return jsonObject.getBoolean(key);
    }
}
