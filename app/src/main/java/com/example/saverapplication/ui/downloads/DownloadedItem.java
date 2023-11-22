package com.example.saverapplication.ui.downloads;

public class DownloadedItem {
    private String filePath;
    private String fileName;
    private String thumbnailUri;
    private String mediaType; // Updated field name
    private boolean isInternal;

    public DownloadedItem(String filePath, String fileName, String thumbnailUri, boolean isInternal) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.thumbnailUri = thumbnailUri;
        this.mediaType = determineMediaType();
        this.isInternal = isInternal;
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

    public String getMediaType() {
        return mediaType;
    }

    public boolean isInternal() {
        return isInternal;
    }

    private String determineMediaType() {
        // Your logic to determine media type goes here
        // You can use this.filePath or other attributes as needed
        // For now, let's return an empty string as a placeholder
        return "";
    }
}
