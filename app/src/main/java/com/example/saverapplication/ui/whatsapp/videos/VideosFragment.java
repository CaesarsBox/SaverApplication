package com.example.saverapplication.ui.whatsapp.videos;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saverapplication.R;

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

public class VideosFragment extends Fragment {
    private VideosAdapter videosAdapter;

    public VideosFragment() {
        // Required empty public constructor
    }

    public static VideosFragment newInstance() {
        return new VideosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        // Initialize RecyclerView with GridLayoutManager
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_videos);
        int spanCount = 2; // Number of items per row
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));

        VideosAdapter.OnItemClickListener itemClickListener = new VideosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String videoUri) {
                playVideo(videoUri);
            }
            public void onDownloadClick(String videoUri) {
                // Handle the download click, e.g., initiate download
                downloadVideo(videoUri);
            }
        };

        videosAdapter = new VideosAdapter(requireContext(), getVideoData(), itemClickListener);
        recyclerView.setAdapter(videosAdapter);
        new  VideosFragment.LoadVideosTask().execute();

        return view;
    }

    private class LoadVideosTask extends AsyncTask<Void, List<VideoData>, Void> {
        private static final int BATCH_SIZE = 2; // Adjust the batch size as needed

        @Override
        protected Void doInBackground(Void... voids) {
            List<VideoData> allVideoData = getVideoData();

            for (int start = 0; start < allVideoData.size(); start += BATCH_SIZE) {
                int end = Math.min(start + BATCH_SIZE, allVideoData.size());
                List<VideoData> batch = allVideoData.subList(start, end);
                publishProgress(batch);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(List<VideoData>... batches) {
            super.onProgressUpdate(batches);

            List<VideoData> batch = batches[0];
            videosAdapter.addData(batch);
            videosAdapter.notifyDataSetChanged();
            Toast.makeText(requireContext(), "Status loaded successfully", Toast.LENGTH_SHORT).show();
        }

    }
    private void updateThumbnailInBackground(final VideoData videoData) {
        new AsyncTask<Void, Void, Uri>() {
            @Override
            protected Uri doInBackground(Void... voids) {
                return getThumbnailUriForVideo(new File(videoData.getVideoUri()));
            }

            @Override
            protected void onPostExecute(Uri thumbnailUri) {
                super.onPostExecute(thumbnailUri);

                if (thumbnailUri != null) {
                    // Update the VideoData with the thumbnail URI
                    videoData.setThumbnailUri(thumbnailUri.toString());

                    // Notify the adapter that the data set has changed
                    videosAdapter.notifyDataSetChanged();
                } else {
                    // Handle the case where thumbnailUri is null (e.g., log an error or show a message)
                    Log.e("VideoFragment", "Thumbnail URI is null.");
                }
            }
        }.execute();
    }
    private void downloadVideoInBackground(final String videoUri) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadVideo(videoUri);
                return null;
            }
        }.execute();
    }

    private List<VideoData> getVideoData() {
        List<VideoData> videoDataList = new ArrayList<>();

        // Get the directory where video files are stored
        File whatsappDirectory = new File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/.Statuses");
        if (whatsappDirectory.exists() && whatsappDirectory.isDirectory()) {
            File[] files = whatsappDirectory.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Check if it's a file and has a video extension (e.g., .mp4)
                    if (file.isFile() && isVideoFile(file)) {
                        // Convert the file path to a URI and add it to the list
                        Uri videoUri = Uri.fromFile(file);

                        // Assuming you have a method to get the thumbnail URI for the video
                        Uri thumbnailUri = getThumbnailUriForVideo(file);

                        VideoData videoData = new VideoData(videoUri.toString(), thumbnailUri.toString());
                        videoDataList.add(videoData);
                        updateThumbnailInBackground(videoData);
                    }
                }
            }
        } else {
            Log.e("VideosFragment", "WhatsApp Status directory not found.");
        }

        return videoDataList;
    }


    // Add a method to check if a file is a video file based on its extension
    private boolean isVideoFile(File file) {
        // Add more video extensions if needed
        String[] videoExtensions = new String[]{".mp4", ".avi", ".mkv"};
        String fileName = file.getName().toLowerCase();

        for (String extension : videoExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }

        return false;
    }

    // Add a method to get the thumbnail URI for the video (you can use a suitable approach here)/ ...
    private Uri getThumbnailUriForVideo(File videoFile) {
        MediaMetadataRetriever retriever = null;

        try {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoFile.getAbsolutePath());

            // Extract the frame at a smaller time interval
            Bitmap thumbnail = retriever.getFrameAtTime(100000);

            if (thumbnail != null) {
                // Resize the thumbnail to a smaller size
                int targetWidth = 100; // Adjust the width as needed
                int targetHeight = (int) (targetWidth * (float) thumbnail.getHeight() / thumbnail.getWidth());
                Bitmap resizedThumbnail = Bitmap.createScaledBitmap(thumbnail, targetWidth, targetHeight, false);

                // Save the resized thumbnail image to a file (optional)
                String videoFileName = videoFile.getName();
                File thumbnailFile = saveThumbnailToDisk(resizedThumbnail, videoFileName);

                // Log the thumbnail file path
                Log.d("VideoFragment", "Thumbnail file path: " + thumbnailFile.getAbsolutePath());

                // Return the URI of the saved thumbnail
                return Uri.fromFile(thumbnailFile);
            } else {
                Log.d("VideoFragment", "Thumbnail is null.");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.d("VideoFragment", "RuntimeException: " + e.getMessage());
        } finally {
            // Handle the IOException when releasing the retriever
            if (retriever != null) {
                try {
                    retriever.release();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("VideoFragment", "Exception on release: " + e.getMessage());
                }
            }
        }

        // Return null or a default thumbnail URI in case of failure
        return null;
    }

    // Helper method to save the thumbnail as an image file (optional)
    private File saveThumbnailToDisk(Bitmap thumbnail, String videoFileName) {
        String thumbnailFileName = "thumbnail_" + videoFileName + ".jpg";
        File thumbnailFile = new File(requireContext().getCacheDir(), thumbnailFileName);
        try {
            FileOutputStream out = new FileOutputStream(thumbnailFile);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbnailFile;
    }

    private void playVideo(String videoUri) {
        // Assuming the videoUri is a valid URI of the video file
        Uri uri = Uri.parse(videoUri);

        // Create an Intent to open the video file
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Check if the URI is a content URI or a file URI
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            // Convert file URI to content URI using FileProvider
            uri = FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", new File(uri.getPath()));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.setDataAndType(uri, "video/*");

        // Check if there's an app to handle this Intent
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle the case where no app can handle the video
            Toast.makeText(requireContext(), "No video player found", Toast.LENGTH_SHORT).show();
        }
    }
    private void downloadVideo(String videoUri) {
        File sourceFile = new File(Uri.parse(videoUri).getPath());

        // Set destination directory for the copied file in DCIM/StatusSaver
        File dcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File statusSaverDirectory = new File(dcimDirectory, "StatusSaver");

        // Create the directory if it doesn't exist
        if (!statusSaverDirectory.exists()) {
            statusSaverDirectory.mkdirs();
        }

        // Generate a unique file name using timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "video_" + timeStamp + ".mp4";

        // Set the destination path including the directory and unique file name
        File destinationFile = new File(statusSaverDirectory, fileName);

        try {
            // Copy the file to the destination directory
            FileUtils.copyFile(sourceFile, destinationFile);

            // Notify the system that a new file has been created
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(destinationFile));
            requireContext().sendBroadcast(mediaScanIntent);

            // Show a Toast indicating success on the main thread
            showSuccessToast();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, e.g., show an error message on the main thread
            showErrorToast();
        }
    }

    private void showSuccessToast() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requireContext(), "Video downloaded successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showErrorToast() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requireContext(), "Failed to download video", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
