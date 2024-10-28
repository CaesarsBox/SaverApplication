package com.example.saverapplication.ui.whatsapp.images;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saverapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    private List<ImageData> imageList = new ArrayList<>();
    private final OnItemClickListener itemClickListener;
    private final Context context;

    public ImagesAdapter(Context context, List<ImageData> imageList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.imageList = imageList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_layout, parent, false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        final ImageData imageData = imageList.get(position);

        Glide.with(context)
                .load(Uri.parse(imageData.getThumbnailUri()))
                .into(holder.imageImageView);
        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(imageData.getImageUri()));

        holder.downloadButton.setOnClickListener(v -> itemClickListener.onDownloadClick(imageData.getImageUri()));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void updateImageData(List<ImageData> newImageData) {
        this.imageList.clear();
        if (newImageData != null) {
            this.imageList.addAll(newImageData);
        }
        notifyDataSetChanged();
    }

    public static class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageImageView;
        ImageView downloadButton;

        public ImagesViewHolder(View itemView) {
            super(itemView);
            imageImageView = itemView.findViewById(R.id.imageImageView);
            downloadButton = itemView.findViewById(R.id.imageDownloadButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String imagePath);
        void onDownloadClick(String imagePath);
    }
}
