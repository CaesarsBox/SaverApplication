package com.example.saverapplication.ui.whatsapp.videos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.downloads.VideoPlayerActivity;

import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class VideosFragment extends Fragment {
    private VideosAdapter videosAdapter;
    private ExecutorService executorService;

    public VideosFragment() {
    }

    public static VideosFragment newInstance() {
        return new VideosFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_videos);
        int spanCount = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));

        VideosAdapter.OnItemClickListener itemClickListener = new VideosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String videoUri) {
                playVideo(videoUri);
            }

            public void onDownloadClick(String videoUri) {
                downloadVideo(videoUri);
            }
        };

        videosAdapter = new VideosAdapter(requireContext(), getVideoData(), itemClickListener);
        recyclerView.setAdapter(videosAdapter);

        loadVideosInBackground();

        return view;
    }

    private void loadVideosInBackground() {
        executorService.submit(() -> {
            List<VideoData> allVideoData = getVideoData();
            int batchSize = 2;

            for (int start = 0; start < allVideoData.size(); start += batchSize) {
                int end = Math.min(start + batchSize, allVideoData.size());
                List<VideoData> batch = allVideoData.subList(start, end);

                requireActivity().runOnUiThread(() -> {
                    videosAdapter.addData(batch);
                    Toast.makeText(requireContext(), "Status loaded successfully", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private List<VideoData> getVideoData() {
        List<VideoData> videoDataList = new ArrayList<>();
        File whatsappDirectory = new File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/.Statuses");

        if (whatsappDirectory.exists() && whatsappDirectory.isDirectory()) {
            File[] files = whatsappDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isVideoFile(file)) {
                        Uri videoUri = Uri.fromFile(file);
                        Uri thumbnailUri = getThumbnailUriForVideo(file);
                        videoDataList.add(new VideoData(videoUri.toString(), thumbnailUri != null ? thumbnailUri.toString() : ""));
                    }
                }
            }
        } else {
            Log.e("VideosFragment", "WhatsApp Status directory not found.");
        }

        return videoDataList;
    }

    private boolean isVideoFile(File file) {
        String[] videoExtensions = {".mp4", ".avi", ".mkv"};
        String fileName = file.getName().toLowerCase();
        for (String extension : videoExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private Uri getThumbnailUriForVideo(File videoFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Uri thumbnailUri = null;

        if (!videoFile.exists()) {
            Log.e("VideoFragment", "Invalid video file or file does not exist.");
            return null;
        }

        try {
            retriever.setDataSource(videoFile.getAbsolutePath());
            Bitmap thumbnail = retriever.getFrameAtTime(1000000);

            if (thumbnail != null) {
                int targetWidth = 300;
                int targetHeight = (int) (targetWidth * (float) thumbnail.getHeight() / thumbnail.getWidth());
                Bitmap resizedThumbnail = Bitmap.createScaledBitmap(thumbnail, targetWidth, targetHeight, true);
                thumbnailUri = saveThumbnailToDisk(resizedThumbnail, videoFile.getName());
            } else {
                Log.e("VideoFragment", "Thumbnail is null.");
            }
        } catch (IllegalArgumentException e) {
            Log.e("VideoFragment", "IllegalArgumentException: Invalid file path: " + e.getMessage());
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                Log.e("VideoFragment", "IOException during release: " + e.getMessage());
            }
        }
        return thumbnailUri;
    }

    private Uri saveThumbnailToDisk(Bitmap thumbnail, String videoFileName) {
        String thumbnailFileName = "thumbnail_" + videoFileName + ".jpg";
        File thumbnailFile = new File(requireContext().getCacheDir(), thumbnailFileName);

        try {
            try (FileOutputStream out = new FileOutputStream(thumbnailFile)) {
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }
        } catch (Exception e) {
            Log.e("VideoFragment", "Error saving thumbnail: " + e.getMessage());
            return null;
        }

        return Uri.fromFile(thumbnailFile);
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
            // Notify the media scanner about the new file
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(destinationFile));
            requireContext().sendBroadcast(mediaScanIntent);
            showSuccessToast();  // Show success toast if copy is successful
        } catch (Exception e) {
            Log.e("VideoFragment", "Error copying file: " + e.getMessage());
            showErrorToast();  // Show error toast if an exception occurs
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
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
