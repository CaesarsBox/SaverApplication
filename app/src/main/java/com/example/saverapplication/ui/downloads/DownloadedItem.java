package com.example.saverapplication.ui.downloads;

public class DownloadedItem {
    private int imageResource; // You can use this to store the resource ID of the downloaded image
    // or any other relevant information about the downloaded item

    public DownloadedItem(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getImageResource() {
        return imageResource;
    }
}
