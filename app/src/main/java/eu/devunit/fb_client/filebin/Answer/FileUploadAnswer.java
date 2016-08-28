package eu.devunit.fb_client.filebin.Answer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eu.devunit.fb_client.filebin.Answer.Base.SuccessAnswer;

/**
 * Created by sebastian on 8/28/16.
 */
public class FileUploadAnswer extends SuccessAnswer {
    public String[] getIds() {
        try {
            ArrayList<String> ids = new ArrayList<String>();

            JSONArray items = getData().getJSONArray("ids");
            for(int i = 0; i < items.length(); i++) {
                ids.add(items.getString(i));
            }

            return ids.toArray(new String[ids.size()]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getUrls() {
        try {
            ArrayList<String> urls = new ArrayList<String>();

            JSONArray items = getData().getJSONArray("urls");
            for(int i = 0; i < items.length(); i++) {
                urls.add(items.getString(i));
            }

            return urls.toArray(new String[urls.size()]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
