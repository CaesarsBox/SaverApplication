package com.example.saverapplication.ui.downloads;

import static android.telecom.VideoProfile.isVideo;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saverapplication.R;

import java.io.File;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private final List<File> savedFiles;
    private final FileClickListener listener;

    public interface FileClickListener {
        void onFileClick(File file);
    }

    public StatusAdapter(List<File> savedFiles, FileClickListener listener) {
        this.savedFiles = savedFiles;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File file = savedFiles.get(position);

        // Skip directories
        if (file.isDirectory()) return;

        // Set thumbnail using Glide
        Glide.with(holder.itemView.getContext())
                .load(file)
                .into(holder.thumbnail);

        if (isVideo(file)) {
            holder.playIcon.setVisibility(View.VISIBLE);
        } else {
            holder.playIcon.setVisibility(View.GONE);
        }

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            listener.onFileClick(file);
        });
    }

    private boolean isVideo(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp4") || fileName.endsWith(".mkv") ||
                fileName.endsWith(".avi") || fileName.endsWith(".3gp");
    }

    @Override
    public int getItemCount() {
        return savedFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        ImageView playIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            playIcon = itemView.findViewById(R.id.playIcon);
        }
    }
}
