package eu.devunit.fb_client.filebin;

public class UploadProgress {
    private long _totalSizeBytes;
    private long _uploadedBytes;

    public long get_totalSizeBytes() {
        return _totalSizeBytes;
    }

    public void set_totalSizeBytes(long totalSizeBytes) {
        this._totalSizeBytes = totalSizeBytes;
    }

    public long get_uploadedBytes() {
        return _uploadedBytes;
    }

    public void set_uploadedBytes(long uploadedBytes) {
        this._uploadedBytes = uploadedBytes;
    }

    public float get_progressPercentage() {
        return ((float) this._uploadedBytes / this._totalSizeBytes) * 100;
    }
}
