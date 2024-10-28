package com.example.saverapplication.ui.whatsapp4b;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.saverapplication.R;
import com.example.saverapplication.ui.downloads.VideoPlayerActivity;
import com.example.saverapplication.ui.whatsapp.videos.VideoData;
import com.example.saverapplication.ui.whatsapp.videos.VideosAdapter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideosBFragment extends Fragment {
    private VideosAdapter videosAdapter;
    private ExecutorService executorService;

    public VideosBFragment() {
    }

    public static VideosBFragment newInstance() {
        return new VideosBFragment();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos_b, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.wb_recycler_view_videos);
        int spanCount = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));

        VideosAdapter.OnItemClickListener itemClickListener = new VideosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String videoUri) {
                playVideo(videoUri);
            }

            @Override
            public void onDownloadClick(String videoUri) {
                downloadVideoInBackground(videoUri);
            }
        };

        videosAdapter = new VideosAdapter(requireContext(), new ArrayList<>(), itemClickListener);
        recyclerView.setAdapter(videosAdapter);

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<VideoData> allVideoData = getVideoData();
            requireActivity().runOnUiThread(() -> {
                videosAdapter.addData(allVideoData);
                videosAdapter.notifyDataSetChanged();
            });
        });

        return view;
    }

    private void downloadVideoInBackground(final String videoUri) {
        executorService.execute(() -> downloadVideo(videoUri));
    }

    private List<VideoData> getVideoData() {
        List<VideoData> videoDataList = new ArrayList<>();

        File whatsappBusinessDirectory = new File(Environment.getExternalStorageDirectory(), "WhatsApp Business/Media/.Statuses");
        if (whatsappBusinessDirectory.exists() && whatsappBusinessDirectory.isDirectory()) {
            File[] files = whatsappBusinessDirectory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isVideoFile(file)) {
                        Uri videoUri = Uri.fromFile(file);
                        Uri thumbnailUri = getThumbnailUriForVideo(file);

                        VideoData videoData = new VideoData(videoUri.toString(), thumbnailUri != null ? thumbnailUri.toString() : "");
                        videoDataList.add(videoData);
                    }
                }
            }
        } else {
            Log.e("VideosFragment", "WhatsApp Business Status directory not found.");
        }

        return videoDataList;
    }

    private boolean isVideoFile(File file) {
        String[] videoExtensions = new String[]{".mp4", ".avi", ".mkv"};
        String fileName = file.getName().toLowerCase();

        for (String extension : videoExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }

        return false;
    }

    private Uri getThumbnailUriForVideo(File videoFile) {
        Bitmap thumbnail;
        try {
            thumbnail = Glide.with(this)
                    .asBitmap()
                    .load(videoFile)
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();

            return saveThumbnailToDisk(thumbnail, videoFile.getName());
        } catch (Exception e) {
            Log.e("VideosBFragment", "Error getting thumbnail URI", e);
            return null;
        }
    }

    private Uri saveThumbnailToDisk(Bitmap thumbnail, String videoFileName) {
        String thumbnailFileName = "thumbnail_" + videoFileName + ".jpg";
        File thumbnailFile = new File(requireContext().getCacheDir(), thumbnailFileName);
        try (FileOutputStream out = new FileOutputStream(thumbnailFile)) {
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
            return Uri.fromFile(thumbnailFile);
        } catch (IOException e) {
            Log.e("VideosBFragment", "Error saving thumbnail", e);
            return null;
        }
    }

    private void playVideo(String videoUri) {
        Intent intent = new Intent(requireContext(), VideoPlayerActivity.class);
        intent.putExtra("videoPath", Uri.parse(videoUri).getPath());
        startActivity(intent);
    }

    private void downloadVideo(String videoUri) {
        File sourceFile = new File(Uri.parse(videoUri).getPath());
        File dcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File statusSaverDirectory = new File(dcimDirectory, "StatusSaver");

        if (!statusSaverDirectory.exists()) {
            statusSaverDirectory.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "video_" + timeStamp + ".mp4";
        File destinationFile = new File(statusSaverDirectory, fileName);

        try {
            FileUtils.copyFile(sourceFile, destinationFile);

            if (destinationFile.exists() && destinationFile.length() > 0) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(destinationFile));
                requireContext().sendBroadcast(mediaScanIntent);

                showSuccessToast();
            } else {
                showErrorToast();
            }
        } catch (Exception e) {
            Log.e("VideoBFragment", "Error copying file: " + e.getMessage());
            showErrorToast();
        }
    }


    private void showSuccessToast() {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), "Video downloaded successfully", Toast.LENGTH_SHORT).show()
        );
    }

    private void showErrorToast() {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), "Failed to download video", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
