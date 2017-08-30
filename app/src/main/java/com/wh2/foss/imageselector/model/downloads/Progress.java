package com.wh2.foss.imageselector.model.downloads;

public class Progress {

    private long downloadeBytes, totalBytes;

    public Progress(long downloadeBytes, long totalBytes) {
        this.downloadeBytes = downloadeBytes;
        this.totalBytes = totalBytes;
    }

    public long getDownloadeBytes() {
        return downloadeBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public int getPercentDownloaded() {
        return Math.round((downloadeBytes * 100.0f) / totalBytes + 0.5f);
    }
}
