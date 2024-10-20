package com.example.saverapplication.ui.downloads;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saverapplication.R;

import java.io.File;
import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imagePaths;

    public ImagePagerAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each image page
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Load the image into the ImageView using Glide
        String imagePath = imagePaths.get(position);
        Glide.with(context)
                .load(imagePath)
                .into(holder.imageView);

        // Set click listeners for the delete and share buttons
        holder.deleteButton.setOnClickListener(v -> {
            // Handle delete action
            deleteImage(imagePath, position);
        });

        holder.shareButton.setOnClickListener(v -> {
            // Handle share action
            shareImage(imagePath);
        });
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    // ViewHolder class to hold references to the views
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton, shareButton;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            deleteButton = itemView.findViewById(R.id.imageDeleteButton);
            shareButton = itemView.findViewById(R.id.imageShareButton);
        }
    }

    private void deleteImage(String imagePath, int position) {
        File file = new File(imagePath);

        // Check if the file exists
        if (file.exists()) {
            // Attempt to delete the file
            if (file.delete()) {
                // If the file is deleted successfully, remove it from the list and notify the adapter
                imagePaths.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, imagePaths.size());  // Optional: To refresh the list

                // Notify the activity to refresh the DownloadsFragment
                if (context instanceof ImageViewerActivity) {
                    ((ImageViewerActivity) context).refreshDownloadsFragment();
                }

                Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                // If deletion fails
                Toast.makeText(context, "Failed to delete image from storage", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If the file doesn't exist
            Toast.makeText(context, "Image not found", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to share the image
    private void shareImage(String imagePath) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri uri = Uri.fromFile(new File(imagePath));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(shareIntent, "Share image via"));
    }
}
