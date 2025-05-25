package com.example.saverapplication.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saverapplication.R;
import com.example.saverapplication.ui.whatsapp.ImageViewerActivity;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageStatusAdapter extends RecyclerView.Adapter<ImageStatusAdapter.MyViewHolder> {

    private final List<File> imageFiles;
    private final Context context;

    public ImageStatusAdapter(List<File> imageFiles, Context context) {
        this.imageFiles = imageFiles;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        File file = imageFiles.get(position);

        Glide.with(context)
                .load(file)
                .centerCrop()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImageViewerActivity.class);
            intent.putExtra("images", new ArrayList<>(imageFiles));
            intent.putExtra("position", position);
            context.startActivity(intent);
        });


        holder.downloadButton.setOnClickListener(v -> {
            try {
                File sourceFile = file;
                File destDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM), "Status Saver");

                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                File destFile = new File(destDir, sourceFile.getName());

                try (FileInputStream in = new FileInputStream(sourceFile);
                     FileOutputStream out = new FileOutputStream(destFile)) {

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }

                    Toast.makeText(context, "Downloaded to DCIM/Status Saver", Toast.LENGTH_SHORT).show();

                    // Refresh gallery
                    Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    scanIntent.setData(Uri.fromFile(destFile));
                    context.sendBroadcast(scanIntent);

                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageFiles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageView;
        ImageButton downloadButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            downloadButton = itemView.findViewById(R.id.imageDownloadButton);
        }
    }
}

