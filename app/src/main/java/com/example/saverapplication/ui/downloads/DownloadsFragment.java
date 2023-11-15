package com.example.saverapplication.ui.downloads;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class DownloadsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DownloadsAdapter downloadsAdapter;

    public DownloadsFragment() {
        // Required empty public constructor
    }

    public static DownloadsFragment newInstance() {
        return new DownloadsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return null;
    }

    private List<DownloadedItem> getDownloadedItems() {
        // Retrieve the list of downloaded items from your storage
        // Populate a list of DownloadedItem objects with necessary information
        // For example, file path, type, etc.
        List<DownloadedItem> downloadedItems = new ArrayList<>();

        // Add downloaded items to the list

        return downloadedItems;
    }
}
