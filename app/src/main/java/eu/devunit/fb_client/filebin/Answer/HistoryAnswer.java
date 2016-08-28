package eu.devunit.fb_client.filebin.Answer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import eu.devunit.fb_client.filebin.Answer.Base.SuccessAnswer;
import eu.devunit.fb_client.filebin.History.FlatHistoryItem;
import eu.devunit.fb_client.filebin.History.HistoryItem;
import eu.devunit.fb_client.filebin.History.HistoryMultipaste;

/**
 * Created by sebastian on 2/23/15.
 */
public class HistoryAnswer extends SuccessAnswer {
    public ArrayList<HistoryItem> getItems() {
        ArrayList<HistoryItem> items = new ArrayList<>();

        try {
            JSONObject itemPairs = getData().getJSONObject("items");

            Iterator<String> keysItr = itemPairs.keys();
            while(keysItr.hasNext()) {
                String key = keysItr.next();
                JSONObject value = itemPairs.getJSONObject(key);

                items.add(new HistoryItem(value));
            }

            return items;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<HistoryMultipaste> getMultipasteItems() {
        ArrayList<HistoryMultipaste> items = new ArrayList<>();

        try {
            JSONObject itemPairs = getData().getJSONObject("multipaste_items");

            Iterator<String> keysItr = itemPairs.keys();
            while(keysItr.hasNext()) {
                String key = keysItr.next();
                JSONObject value = itemPairs.getJSONObject(key);

                items.add(new HistoryMultipaste(value));
            }

            return items;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<FlatHistoryItem> getFlatHistory() {
        ArrayList<FlatHistoryItem> flatItems = new ArrayList<>();

        for(HistoryItem historyItem : getItems()) {
            flatItems.add(new FlatHistoryItem(historyItem));
        }

        for(HistoryMultipaste multipaste : getMultipasteItems()) {
            flatItems.add(new FlatHistoryItem(multipaste));
        }

        Collections.sort(flatItems);
        return flatItems;
    }

    public int GetTotalSize() {
        try {
            return getData().getInt("total_size");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
