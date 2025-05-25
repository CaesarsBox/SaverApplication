package com.example.saverapplication.ui.whatsapp;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.adapters.VideoStatusAdapter;

import java.io.File;
import java.util.ArrayList;

public class WhatsAppVideoFragment extends Fragment {

    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private VideoStatusAdapter adapter;

    private final String STATUS_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses/";

    public WhatsAppVideoFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos_recycler, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        swipeRefreshLayout = view.findViewById(R.id.videoSwipeRefreshLayout);
        recyclerView = view.findViewById(R.id.videosRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        loadVideos();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadVideos();
            swipeRefreshLayout.setRefreshing(false);
        });

    }

    private void loadVideos() {
        File statusDir = new File(STATUS_PATH);
        File[] files = statusDir.listFiles();
        ArrayList<File> videoFiles = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".mp4")) {
                    videoFiles.add(file);
                }
            }
        }

        adapter = new VideoStatusAdapter(videoFiles, requireContext());
        recyclerView.setAdapter(adapter);
    }
}