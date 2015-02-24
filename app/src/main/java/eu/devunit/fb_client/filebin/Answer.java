package eu.devunit.fb_client.filebin;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sebastian on 2/23/15.
 */
public class Answer {
    private String raw;
    private String status = "UNKNOWN";
    private JSONObject data;

    public Answer(String raw) {
        this.raw = raw;

        try {
            JSONObject jsonObject = new JSONObject(raw);
            status = jsonObject.getString("status");
            data = jsonObject.getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return status == "success";
    }

    protected JSONObject getData() {
        return data;
    }
}
