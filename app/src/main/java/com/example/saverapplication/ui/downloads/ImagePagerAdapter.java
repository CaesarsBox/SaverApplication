package com.example.saverapplication.ui.downloads;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);

        // Load the image using Glide
        Glide.with(context)
                .load(imagePath)
                .into(holder.imageView);

        // Set up the delete button
        holder.deleteButton.setOnClickListener(v -> deleteImage(imagePath, position));

        // Set up the share button
        holder.shareButton.setOnClickListener(v -> shareImage(imagePath));
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

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
        // Remove the "file://" prefix if present
        if (imagePath.startsWith("file://")) {
            imagePath = imagePath.substring(7); // Remove "file://"
        }

        Log.d("ImagePagerAdapter", "Attempting to delete image at path: " + imagePath);
        File file = new File(imagePath);

        if (file.exists()) {
            Log.d("ImagePagerAdapter", "File exists: " + file.getAbsolutePath());
            if (file.delete()) {
                imagePaths.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, imagePaths.size());
                Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete image from storage", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("ImagePagerAdapter", "Image not found: " + file.getAbsolutePath());
            Toast.makeText(context, "Image not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage(String imagePath) {
        // Create a File object from the provided image path
        File file = new File(imagePath);

        // Check if the file exists before proceeding
        if (!file.exists()) {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Uri using FileProvider
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        // Create the share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Start the sharing activity
        context.startActivity(Intent.createChooser(shareIntent, "Share image via"));
    }

}
