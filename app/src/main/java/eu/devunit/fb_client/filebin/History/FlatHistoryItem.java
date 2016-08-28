package eu.devunit.fb_client.filebin.History;

import java.util.Date;

/**
 * Created by sebastian on 8/28/16.
 */
public class FlatHistoryItem implements Comparable<FlatHistoryItem> {
    private String id;
    private String filename;
    private String mimetype;
    private long date;
    private String hash;
    private int filesize;


    public FlatHistoryItem(HistoryMultipaste historyMultipaste) {
        this.id = historyMultipaste.getUrlId();
        this.date = historyMultipaste.getDate();
    }

    public FlatHistoryItem(HistoryItem historyItem) {
        this.id = historyItem.getId();
        this.filename = historyItem.getFilename();
        this.mimetype = historyItem.getMimetype();
        this.date = historyItem.getDate();
        this.filesize = historyItem.getFilesize();
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
        boolean si = false;
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

    @Override
    public int compareTo(FlatHistoryItem flatHistoryItem) {
        return flatHistoryItem.getHumanReadableDate().compareTo(getHumanReadableDate());
    }
}
