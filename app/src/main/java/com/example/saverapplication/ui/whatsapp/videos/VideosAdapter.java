package com.example.saverapplication.ui.whatsapp.videos;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saverapplication.R;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {
    private List<VideoData> videoList;
    private Context context;
    private OnItemClickListener itemClickListener;

    public VideosAdapter(Context context, List<VideoData> videoList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.videoList = videoList;
        this.itemClickListener = itemClickListener;
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

        // Set the video thumbnail or cover image to the ImageView
        holder.videoImageView.setImageURI(Uri.parse(videoData.getThumbnailUri()));

        // Set an OnClickListener for the video item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(videoData.getVideoUri());
            }
        });

        // Set an OnClickListener for the playButton
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call onItemClick when playButton is clicked
                itemClickListener.onItemClick(videoData.getVideoUri());
            }
        });
        // Set an OnClickListener for the download button
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onDownloadClick(videoData.getVideoUri());
            }
        });
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
        void onItemClick(String videoUri);

        void onDownloadClick(String videoUri);
    }
}
