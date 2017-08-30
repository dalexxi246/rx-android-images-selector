package com.wh2.foss.imageselector.model.downloads;

public class DownloadPaths {

    private String url, dirPath, fileName;

    public DownloadPaths(String url, String dirPath, String fileName) {
        this.url = url;
        this.dirPath = dirPath;
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public String getDirPath() {
        return dirPath;
    }

    public String getFileName() {
        return fileName;
    }
}
