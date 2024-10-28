package com.example.saverapplication.ui.downloads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.saverapplication.R;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private final List<VideoItem> videoItems;
    private final Context context;

    public MediaAdapter(List<VideoItem> videoItems, Context context) {
        this.videoItems = videoItems;
        this.context = context;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        VideoItem videoItem = videoItems.get(position);
        holder.bind(videoItem);
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    class MediaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnailImageView;
        private final ImageView playButton;
        private final ImageButton downloadsShareButton;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            playButton = itemView.findViewById(R.id.playButton);
            downloadsShareButton = itemView.findViewById(R.id.downloadsShareButton);
        }

        public void bind(VideoItem videoItem) {
            if (videoItem.isVideo()) {
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoItem.getFile().getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                thumbnailImageView.setImageBitmap(thumbnail);

                playButton.setVisibility(View.VISIBLE);
                playButton.setOnClickListener(v -> {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("videoPath", videoItem.getFile().getAbsolutePath());
                    context.startActivity(intent);
                });
            } else {
                Uri imageUri = Uri.fromFile(videoItem.getFile());
                Glide.with(context).load(imageUri).into(thumbnailImageView);

                thumbnailImageView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra("imageUri", imageUri.toString());
                    context.startActivity(intent);
                });
                playButton.setVisibility(View.GONE);
            }

            downloadsShareButton.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType(videoItem.isVideo() ? "video/*" : "image/*");

                Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", videoItem.getFile());
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                context.startActivity(Intent.createChooser(shareIntent, "Share via"));
            });
        }
    }
}