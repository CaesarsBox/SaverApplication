package com.example.saverapplication.ui.downloads;

import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.whatsapp.ImageViewerActivity;
import com.example.saverapplication.ui.whatsapp.VideoViewerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private final List<File> savedFiles = new ArrayList<>();

    public SavedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saved, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.savedSwipeRefreshLayout);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSaved);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        loadSavedFiles();

        StatusAdapter adapter = new StatusAdapter(savedFiles, file -> {
            Intent intent;
            if (isVideoFile(file)) {
                intent = new Intent(getContext(), SavedVideoViewerActivity.class);
                intent.putExtra("videos", new ArrayList<>(getVideoFiles()));
            } else {
                intent = new Intent(getContext(), SavedImageViewerActivity.class);
                intent.putExtra("images", new ArrayList<>(getImageFiles()));
            }
            intent.putExtra("position", getPosition(file));
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            savedFiles.clear();
            loadSavedFiles();
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });

        return rootView;
    }

    private void loadSavedFiles() {
        File directory = new File(Environment.getExternalStorageDirectory(), "DCIM/Status Saver");
        File[] files = directory.listFiles();
        if (files != null) {
            savedFiles.clear();
            savedFiles.addAll(Arrays.asList(files));
        }
    }

    private List<File> getVideoFiles() {
        List<File> videos = new ArrayList<>();
        for (File file : savedFiles) {
            if (isVideoFile(file)) {
                videos.add(file);
            }
        }
        return videos;
    }

    private List<File> getImageFiles() {
        List<File> images = new ArrayList<>();
        for (File file : savedFiles) {
            if (isImageFile(file)) {
                images.add(file);
            }
        }
        return images;
    }

    private boolean isVideoFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".3gp");
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }

    private int getPosition(File file) {
        List<File> list = isVideoFile(file) ? getVideoFiles() : getImageFiles();
        return list.indexOf(file);
    }
}
