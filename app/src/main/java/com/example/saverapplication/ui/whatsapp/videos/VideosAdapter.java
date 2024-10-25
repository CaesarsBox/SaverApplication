package com.example.saverapplication.ui.whatsapp.videos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saverapplication.R;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {
    private final Context context;
    private final List<VideoData> videoList;
    private final OnItemClickListener itemClickListener;

    public VideosAdapter(Context context, List<VideoData> videoList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.videoList = videoList;
        this.itemClickListener = itemClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(List<VideoData> newData) {
        videoList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_layout, parent, false);
        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder holder, int position) {
        final VideoData videoData = videoList.get(position);

        Glide.with(context)
                .load(Uri.parse(videoData.getThumbnailUri()))
                .into(holder.videoImageView);

        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(videoData.getVideoUri()));

        holder.playButton.setOnClickListener(v -> itemClickListener.onItemClick(videoData.getVideoUri()));

        holder.downloadButton.setOnClickListener(v -> itemClickListener.onDownloadClick(videoData.getVideoUri()));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideosViewHolder extends RecyclerView.ViewHolder {
        ImageView videoImageView;
        ImageButton playButton;
        ImageView downloadButton;

        public VideosViewHolder(View itemView) {
            super(itemView);
            videoImageView = itemView.findViewById(R.id.videoThumbnailImageView);
            playButton = itemView.findViewById(R.id.playButton);
            downloadButton = itemView.findViewById(R.id.downloadButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String videoPath);

        void onDownloadClick(String videoPath);
    }
}
