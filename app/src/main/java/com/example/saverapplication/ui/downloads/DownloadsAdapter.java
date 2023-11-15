package com.example.saverapplication.ui.downloads;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saverapplication.R;

import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadsViewHolder> {

    private List<DownloadedItem> downloadedItemList;
    private OnItemClickListener itemClickListener;
    private Context context;

    public DownloadsAdapter(Context context, List<DownloadedItem> downloadedItemList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.downloadedItemList = downloadedItemList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public DownloadsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.downloads_item_layout, parent, false);
        return new DownloadsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadsViewHolder holder, int position) {
        DownloadedItem downloadedItem = downloadedItemList.get(position);

        // Populate the views in your item layout with information from DownloadedItem
        // For example:
        // holder.fileNameTextView.setText(downloadedItem.getFileName());

        // Set click listeners
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(downloadedItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return downloadedItemList.size();
    }

    public class DownloadsViewHolder extends RecyclerView.ViewHolder {
        // Define views in your downloaded_item_layout.xml
        // For example:
        // TextView fileNameTextView;
        // Button shareButton;

        public DownloadsViewHolder(View itemView) {
            super(itemView);
            // Initialize views
            // For example:
            // fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
            // shareButton = itemView.findViewById(R.id.shareButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DownloadedItem downloadedItem);

        void onShareClick(DownloadedItem downloadedItem);
    }
}
