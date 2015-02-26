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
    private long date;
    private String hash;
    private int filesize;

    public HistoryItem(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getString("id");
            this.filename = jsonObject.getString("filename");
            this.mimetype = jsonObject.getString("mimetype");
            this.date = jsonObject.getLong("date");
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

    public long getDate() {
        return date;
    }

    public Date getHumanReadableDate() {
        return new java.util.Date(date * 1000);
    }

    public String getHash() {
        return hash;
    }

    public int getFilesize() {
        return filesize;
    }

    public String getHumanReadableFilesize() {
        boolean si = true;
        int unit = si ? 1000 : 1024;
        if (filesize < unit) return filesize + " B";
        int exp = (int) (Math.log(filesize) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", filesize / Math.pow(unit, exp), pre);
    }

    @Override
    public String toString() {
        return id;
    }
}
