package com.example.saverapplication.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
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
import com.example.saverapplication.ui.whatsapp.VideoViewerActivity;
import com.google.android.material.imageview.ShapeableImageView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class VideoStatusAdapter extends RecyclerView.Adapter<VideoStatusAdapter.ViewHolder> {

    private final List<File> videoFiles;
    private final Context context;

    public VideoStatusAdapter(List<File> videoFiles, Context context) {
        this.videoFiles = videoFiles;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoStatusAdapter.ViewHolder holder, int position) {
        File file = videoFiles.get(position);
        Uri videoUri = Uri.fromFile(file);

        Glide.with(context)
                .load(videoUri)
                .thumbnail(0.1f)
                .into(holder.thumbnailView);

        holder.playButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file), "video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        });

        holder.thumbnailView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoViewerActivity.class);
            intent.putExtra("videos", (Serializable) videoFiles);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });

        holder.downloadButton.setOnClickListener(v -> {
            File dest = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + "/Status Saver", file.getName());
            try {
                FileUtils.copyFile(file, dest);
                Toast.makeText(context, "Saved to Status Saver", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView thumbnailView;
        ImageView playButton;
        ImageButton downloadButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailView = itemView.findViewById(R.id.videoThumbnail);
            playButton = itemView.findViewById(R.id.videoPlayButton);
            downloadButton = itemView.findViewById(R.id.videoDownloadButton);

        }
    }
}
