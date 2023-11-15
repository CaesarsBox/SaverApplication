package com.example.saverapplication.ui.whatsapp.videos;

public class VideoData {
    private String videoUri;
    private String thumbnailUri;

    public VideoData(String videoUri, String thumbnailUri) {
        this.videoUri = videoUri;
        this.thumbnailUri = thumbnailUri;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }
}
