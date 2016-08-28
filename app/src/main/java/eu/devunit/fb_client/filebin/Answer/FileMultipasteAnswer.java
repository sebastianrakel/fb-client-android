package eu.devunit.fb_client.filebin.Answer;

import org.json.JSONException;

import eu.devunit.fb_client.filebin.Answer.Base.SuccessAnswer;

/**
 * Created by sebastian on 8/28/16.
 */
public class FileMultipasteAnswer extends SuccessAnswer {
    public String getUrl() {
        try {
            return getData().getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getUrlId() {
        try {
            return getData().getString("url_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
