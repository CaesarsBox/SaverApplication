package com.example.saverapplication.ui.downloads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saverapplication.R;

import java.util.ArrayList;
import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadsViewHolder> {
    private final Context context;
    private final List<DownloadedItem> downloadedItems;
    private final OnItemClickListener listener;

    public DownloadsAdapter(Context context, List<DownloadedItem> downloadedItems, OnItemClickListener listener) {
        this.context = context;
        this.downloadedItems = downloadedItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DownloadsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.downloads_item_layout, parent, false);
        return new DownloadsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadsViewHolder holder, int position) {
        DownloadedItem item = downloadedItems.get(position);

        Glide.with(context).load(item.getThumbnailUri()).into(holder.thumbnailImageView);

        holder.thumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                Intent intent = new Intent(context, ImageViewerActivity.class);

                ArrayList<String> imagePaths = new ArrayList<>();
                for (DownloadedItem downloadedItem : downloadedItems) {
                    imagePaths.add(downloadedItem.getFilePath());
                }

                intent.putStringArrayListExtra("imagePaths", imagePaths);
                intent.putExtra("startPosition", currentPosition);

                context.startActivity(intent);
            }
        });

        // Set click listener for the share button
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current adapter position
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                // Handle the share functionality
                listener.onShareClick(downloadedItems.get(currentPosition));
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearData() {
        downloadedItems.clear();
        notifyDataSetChanged();  // Notify the adapter to refresh the view
    }


    @Override
    public int getItemCount() {
        return downloadedItems.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<DownloadedItem> newItems) {
        downloadedItems.clear();
        downloadedItems.addAll(newItems);
        notifyDataSetChanged();
    }

    public static class DownloadsViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        ImageButton shareButton;

        public DownloadsViewHolder(View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DownloadedItem downloadedItem);

        void onShareClick(DownloadedItem downloadedItem);
    }
}
