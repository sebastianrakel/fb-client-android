package eu.devunit.fb_client.filebin.Answer;

import org.json.JSONException;

import eu.devunit.fb_client.filebin.Answer.Base.SuccessAnswer;

/**
 * Created by sebastian on 8/28/16.
 */
public class CreateApikeyAnswer extends SuccessAnswer {
    public String getNewApiKey() {
        try {
            return getData().getString("new_key");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
