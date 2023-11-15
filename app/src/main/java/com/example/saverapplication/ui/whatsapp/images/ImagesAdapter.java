package com.example.saverapplication.ui.whatsapp.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saverapplication.R;

import java.io.File;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    private List<ImageData> imageList;
    private OnItemClickListener itemClickListener;
    private Context context;

    public ImagesAdapter(Context context, List<ImageData> imageList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.imageList = imageList;
        this.itemClickListener = itemClickListener;
    }
    public void setImageList(List<ImageData> updatedImageList) {
        this.imageList = updatedImageList;
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

        // Use the provided method to get the thumbnail URI for the image
        Uri thumbnailUri = Uri.parse(imageData.getThumbnailUri());

        // Load the thumbnail into the ImageView using Glide
        Glide.with(context)
                .load(thumbnailUri)
                .into(holder.imageImageView);

        // Set an OnClickListener for the image item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(imageData.getImageUri());
            }
        });

        // Set an OnClickListener for the download button
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onDownloadClick(imageData.getImageUri());
            }
        });
    }



    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageImageView;
        ImageView downloadButton;

        public ImagesViewHolder(View itemView) {
            super(itemView);
            imageImageView = itemView.findViewById(R.id.imageImageView);
            downloadButton = itemView.findViewById(R.id.imageDownloadButton);

            // Set click listeners
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemClick(imageList.get(position).getImageUri());
                        }
                    }
                }
            });

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onDownloadClick(imageList.get(position).getImageUri());
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String imagePath);

        void onDownloadClick(String imagePath);
    }
}
