package com.example.saverapplication.ui.downloads;

import java.io.File;

public class VideoItem {
    private String path;

    private final File file;
    private final boolean isVideo;

    public VideoItem(File file, boolean isVideo) {
        this.file = file;
        this.isVideo = isVideo;
    }

    public File getFile() {
        return file;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public String getPath() {
        return path;
    }
}
