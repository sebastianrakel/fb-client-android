package eu.devunit.fb_client.filebin.History;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by sebastian on 8/28/16.
 */
public class HistoryMultipaste {
    public class HistoryMultipasteItem {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    private String urlId;
    private long date;
    private HashMap<String, HistoryMultipasteItem> items;

    public HistoryMultipaste(JSONObject jsonObject) {
        try {
            this.urlId = jsonObject.getString("url_id");
            this.date = jsonObject.getLong("date");

            JSONObject itemPairs = jsonObject.getJSONObject("items");

            items = new HashMap<>();

            Iterator<String> keysItr = itemPairs.keys();
            while(keysItr.hasNext()) {
                String key = keysItr.next();
                JSONObject value = itemPairs.getJSONObject(key);

                HistoryMultipasteItem item = new HistoryMultipasteItem();
                item.setId(value.getString("id"));

                items.put(item.getId(), item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public HashMap<String, HistoryMultipasteItem> getItems() {
        return items;
    }

    public void setItems(HashMap<String, HistoryMultipasteItem> items) {
        this.items = items;
    }
}
