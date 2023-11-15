package com.example.saverapplication.ui.whatsapp.images;

import android.net.Uri;

import java.io.File;

public class ImageData {
    private String imageUri;
    private String thumbnailUri;

    public ImageData(String imageUri, String thumbnailUri) {
        this.imageUri = imageUri;
        this.thumbnailUri = thumbnailUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }
}

