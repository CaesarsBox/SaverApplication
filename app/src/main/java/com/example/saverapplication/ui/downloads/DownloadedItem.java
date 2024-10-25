package com.example.saverapplication.ui.downloads;

public class DownloadedItem {
    private String filePath;
    private String fileName;
    private String thumbnailUri;
    private boolean isInternal;
    private boolean isVideo;

    public DownloadedItem(String filePath, String fileName, String thumbnailUri, boolean isInternal, boolean isVideo) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.thumbnailUri = thumbnailUri;
        this.isInternal = isInternal;
        this.isVideo = isVideo;
    }


    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public boolean isVideo() {
        return isVideo;
    }
}
