package eu.devunit.fb_client.filebin;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by sebastian on 2/23/15.
 */
public class HistoryAnswer extends Answer {
    private ArrayList<HistoryItem> items;
    private int total_size;

    public HistoryAnswer(String raw) {
        super(raw);

        items = new ArrayList<>();

        try {
            total_size = getData().getInt("total_size");
            JSONObject itemPairs = getData().getJSONObject("items");

            Iterator<String> keysItr = itemPairs.keys();
            while(keysItr.hasNext()) {
                String key = keysItr.next();
                JSONObject value = itemPairs.getJSONObject(key);

                items.add(new HistoryItem(value));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<HistoryItem> getItems() {
        return items;
    }
}
