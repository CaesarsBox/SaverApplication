package com.example.saverapplication.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saverapplication.R;

import java.io.File;
import java.util.List;

public class FullscreenVideoAdapter extends RecyclerView.Adapter<FullscreenVideoAdapter.ViewHolder> {

    private final List<File> videoFiles;
    private final Context context;

    public FullscreenVideoAdapter(List<File> videoFiles, Context context) {
        this.videoFiles = videoFiles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fullscreen_video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File videoFile = videoFiles.get(position);
        holder.videoView.setVideoPath(videoFile.getAbsolutePath());
        holder.videoView.setOnPreparedListener(mp -> mp.setLooping(true));
        holder.videoView.start();
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.fullVideoView);
        }
    }
}
