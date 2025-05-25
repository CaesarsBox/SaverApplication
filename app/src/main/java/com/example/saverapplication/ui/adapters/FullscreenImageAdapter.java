package com.example.saverapplication.ui.adapters;

import android.content.Context;
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

public class FullscreenImageAdapter extends RecyclerView.Adapter<FullscreenImageAdapter.ViewHolder> {

    private final List<File> imageFiles;
    private final Context context;

    public FullscreenImageAdapter(List<File> imageFiles, Context context) {
        this.imageFiles = imageFiles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fullscreen_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File imageFile = imageFiles.get(position);
        Glide.with(context).load(imageFile.getAbsolutePath()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fullImageView);
        }
    }
}
