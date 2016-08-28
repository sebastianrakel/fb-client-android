package eu.devunit.fb_client.filebin.Answer.Base;

import org.json.JSONObject;

/**
 * Created by sebastian on 8/28/16.
 */
public class BaseAnswer {
    private JSONObject data;

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
