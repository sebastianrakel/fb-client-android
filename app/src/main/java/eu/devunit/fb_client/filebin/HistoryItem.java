package eu.devunit.fb_client.filebin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sebastian on 2/23/15.
 */
public class HistoryItem {
    private String id;
    private String filename;
    private String mimetype;
    private String date;
    private String hash;
    private int filesize;

    public HistoryItem(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getString("id");
            this.filename = jsonObject.getString("filename");
            this.mimetype = jsonObject.getString("mimetype");
            this.date = jsonObject.getString("date");
            this.hash = jsonObject.getString("hash");
            this.filesize = jsonObject.getInt("filesize");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getMimetype() {
        return mimetype;
    }

    public String getDate() {
        return date;
    }

    public String getHash() {
        return hash;
    }

    public int getFilesize() {
        return filesize;
    }

    @Override
    public String toString() {
        return id;
    }
}
