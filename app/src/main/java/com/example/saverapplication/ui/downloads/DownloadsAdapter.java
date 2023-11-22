package com.example.saverapplication.ui.downloads;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
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
    public void setData(List<DownloadedItem> newData) {
        downloadedItemList.clear();
        downloadedItemList.addAll(newData);
        notifyDataSetChanged();
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

        if (downloadedItem.getThumbnailUri() != null) {
            holder.thumbnailImageView.setImageURI(Uri.parse(downloadedItem.getThumbnailUri()));
        } else {
            // Handle the case where the thumbnail URI is null
            // You might want to set a default image or hide the thumbnail view
            holder.thumbnailImageView.setImageResource(R.drawable.icon);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(downloadedItem);
            }
        });

        // Set share click listener
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onShareClick(downloadedItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return downloadedItemList.size();
    }

    public class DownloadsViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnailImageView;
        Button shareButton;

        public DownloadsViewHolder(View itemView) {
            super(itemView);
            // Initialize views
            // For example:
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);

            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DownloadedItem downloadedItem);

        void onShareClick(DownloadedItem downloadedItem);
    }
}
