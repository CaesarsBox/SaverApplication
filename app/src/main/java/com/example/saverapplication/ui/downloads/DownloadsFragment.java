package com.example.saverapplication.ui.downloads;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saverapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadsFragment extends Fragment {

    private RecyclerView recyclerViewMedia;
    private MediaAdapter mediaAdapter;  // Make this a class-level variable
    private List<VideoItem> mediaItems; // Store the media items here

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);

        recyclerViewMedia = view.findViewById(R.id.recyclerViewMedia);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewMedia.setLayoutManager(layoutManager);

        // Initialize the media items list
        mediaItems = new ArrayList<>();
        mediaAdapter = new MediaAdapter(mediaItems, getContext());
        recyclerViewMedia.setAdapter(mediaAdapter);

        loadMediaFiles();  // Load files initially

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMediaFiles();  // Refresh the media files when the fragment is resumed
    }

    private void loadMediaFiles() {
        mediaItems.clear();  // Clear the existing items to prevent duplicates
        File dcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File statusSaverDirectory = new File(dcimDirectory, "StatusSaver");

        if (statusSaverDirectory.exists()) {
            File[] files = statusSaverDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (file.getName().endsWith(".mp4")) {
                            mediaItems.add(new VideoItem(file, true)); // Add video item
                        } else if (file.getName().endsWith(".jpg")) {
                            mediaItems.add(new VideoItem(file, false)); // Add image item
                        }
                    }
                }
            }
        }

        mediaAdapter.notifyDataSetChanged();  // Notify the adapter of data changes
    }
}

