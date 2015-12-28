package eu.devunit.fb_client.filebin;

import java.io.File;

public class FileSizeInfo {
    private String mSizeUnit;
    private Integer mSize;

    public String getSizeUnit() {
        return mSizeUnit;
    }

    public void setSizeUnit(String mSizeUnit) {
        this.mSizeUnit = mSizeUnit;
    }

    public Integer getSize() {
        return mSize;
    }

    public void setSize(Integer mSize) {
        this.mSize = mSize;
    }

    public static FileSizeInfo getHumanReadableSizeInfo(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            FileSizeInfo fileSizeInfo = new FileSizeInfo();
            fileSizeInfo.setSize((int)(bytes));
            fileSizeInfo.setSizeUnit("B");

            return fileSizeInfo;
        }

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");

        FileSizeInfo fileSizeInfo = new FileSizeInfo();
        fileSizeInfo.setSize((int) (bytes / Math.pow(unit, exp)));
        fileSizeInfo.setSizeUnit(String.format("%sB", pre));

        return fileSizeInfo;
    }
}
