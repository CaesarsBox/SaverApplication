package com.example.saverapplication.ui.downloads;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.saverapplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DownloadsAdapter downloadsAdapter;

    public DownloadsFragment() {
        // Required empty public constructor
    }

    public static DownloadsFragment newInstance() {
        return new DownloadsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("DownloadsFragment", "onCreateView called");

        View view = inflater.inflate(R.layout.fragment_downloads, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_downloads);
        downloadsAdapter = new DownloadsAdapter(requireContext(), getDownloadedItems(), new DownloadsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DownloadedItem downloadedItem) {
                // Handle item click
            }

            @Override
            public void onShareClick(DownloadedItem downloadedItem) {
                shareImage(downloadedItem.getFilePath());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(downloadsAdapter);

        List<DownloadedItem> downloadedItems = getDownloadedItems();
        downloadsAdapter.setData(downloadedItems);

        return view;
    }
    private void shareImage(String imageUri) {
        Log.d("ImageUri", imageUri);
        File imageFile = new File(Uri.parse(imageUri).getPath());

        // Use FileProvider to get content URI
        Uri contentUri = FileProvider.getUriForFile(requireContext(),
                requireContext().getApplicationContext().getPackageName() + ".provider",
                imageFile);

        Log.d("ContentUri", contentUri.toString());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");

        // Put the content URI in the intent
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Check if there's an app to handle this Intent
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Open image with"));
        } else {
            // Handle the case where no app can handle the image
            Toast.makeText(requireContext(), "No app found to open the image", Toast.LENGTH_SHORT).show();
        }
    }


    private List<DownloadedItem> getDownloadedItems() {
        List<DownloadedItem> downloadedItems = new ArrayList<>();

        // Get files from internal storage DCIM/StatusSaver directory
        File internalStatusSaverDirectory = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "StatusSaver");
        Log.d("DownloadsFragment", "Internal directory path: " + internalStatusSaverDirectory.getAbsolutePath());
        downloadedItems.addAll(getDownloadedItemsFromDirectory(internalStatusSaverDirectory, true));

        // Get files from external storage DCIM/StatusSaver directory
        File externalDcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File externalStatusSaverDirectory = new File(externalDcimDirectory, "StatusSaver");
        Log.d("DownloadsFragment", "External directory path: " + externalStatusSaverDirectory.getAbsolutePath());
        downloadedItems.addAll(getDownloadedItemsFromDirectory(externalStatusSaverDirectory, false));

        return downloadedItems;
    }

    private List<DownloadedItem> getDownloadedItemsFromDirectory(File directory, boolean isInternal) {
        List<DownloadedItem> downloadedItems = new ArrayList<>();

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Assuming you have a method to get the thumbnail URI for the file
                    String thumbnailUri = getThumbnailUriForFile(file);

                    // Create a DownloadedItem object with file details and thumbnail URI
                    DownloadedItem downloadedItem = new DownloadedItem(file.getAbsolutePath(), file.getName(), thumbnailUri, isInternal);
                    downloadedItems.add(downloadedItem);
                }
            }
        } else {
            Log.e("DownloadsFragment", "StatusSaver directory not found.");
        }

        return downloadedItems;
    }



    private String getThumbnailUriForFile(File file) {
        // Implementation to get the thumbnail URI based on the file type (video or image)
        // You may need to distinguish between video and image files
        String mediaType = determineMediaType(file);

        if ("image".equals(mediaType)) {
            return getImageThumbnailUriForFile(file.getPath());
        } else if ("video".equals(mediaType)) {
            return generateVideoThumbnailUri(file);
        }

        // Return null or a default thumbnail URI in case of an unknown media type
        return null;
    }

    private String generateVideoThumbnailUri(File videoFile) {
        MediaMetadataRetriever retriever = null;
        Log.e("DownloadsFragment", "Video Thumbnail generation Started.");

        try {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoFile.getAbsolutePath());

            // Extract the frame at a specific time interval (adjust as needed)
            long timeInMicros = 500000; // 500,000 microseconds (0.5 seconds)
            Bitmap thumbnail = retriever.getFrameAtTime(timeInMicros);

            if (thumbnail != null) {
                // Save the thumbnail image to a file (optional)
                String videoFileName = videoFile.getName();
                File thumbnailFile = saveThumbnailToDisk(thumbnail, videoFileName);

                // Return the URI of the saved thumbnail
                return Uri.fromFile(thumbnailFile).toString();
            } else {
                Log.d("DownloadsFragment", "Thumbnail is null.");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.d("DownloadsFragment", "RuntimeException: " + e.getMessage());
        } finally {
            // Handle the IOException when releasing the retriever
            if (retriever != null) {
                try {
                    retriever.release();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("DownloadsFragment", "Exception on release: " + e.getMessage());
                }
            }
        }

        // Return null or a default thumbnail URI in case of failure
        return null;
    }

    private String getImageThumbnailUriForFile(String filePath) {
        Log.e("DownloadsFragment", "Picture Thumbnail generation Started.");

        try {
            // Decode the original image file to get a Bitmap with inSampleSize applied
            Log.d("DownloadsFragment", "Decoding image: " + filePath);
            Bitmap thumbnailBitmap = decodeSampledBitmapFromFile(filePath, 100, 100); // Adjust dimensions as needed

            if (thumbnailBitmap != null) {
                // Save the thumbnail to a temporary file
                File thumbnailFile = saveThumbnailToDisk(thumbnailBitmap, "thumbnail_" + new File(filePath).getName());
                Log.d("DownloadsFragment", "Image thumbnail saved: " + thumbnailFile.getAbsolutePath());

                // Return the URI of the thumbnail file
                return Uri.fromFile(thumbnailFile).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the original image file URI as a fallback
        Log.e("DownloadsFragment", "Failed to generate thumbnail for image: " + filePath);
        return Uri.fromFile(new File(filePath)).toString();
    }

    private Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Higher quality configuration

        return BitmapFactory.decodeFile(filePath, options);
    }



    private String determineMediaType(File file) {
        if (isVideoFile(file)) {
            return "video";
        } else if (isImageFile(file)) {
            return "image";
        } else {
            return "unknown";
        }
    }
    private boolean isImageFile(File file) {
        // Add more image extensions if needed
        String[] imageExtensions = new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        String fileName = file.getName().toLowerCase();
        Log.d("DownloadsFragment", "Checking image file: " + fileName);

        // Check if the filename contains a timestamp and ends with a known image extension
        if (fileName.matches("image_\\d{8}_\\d{6}.*")) {
            for (String extension : imageExtensions) {
                if (fileName.endsWith(extension)) {
                    Log.d("DownloadsFragment", "Image file recognized: " + fileName);
                    return true;
                }
            }
        }

        Log.d("DownloadsFragment", "Not an image file: " + fileName);
        return false;
    }


    private boolean isVideoFile(File file) {
        // Add more video extensions if needed
        String[] videoExtensions = new String[]{".mp4", ".avi", ".mkv"};
        String fileName = file.getName().toLowerCase();
        Log.d("DownloadsFragment", "Checking video file: " + fileName);

        for (String extension : videoExtensions) {
            if (fileName.endsWith(extension)) {
                Log.d("DownloadsFragment", "Video file recognized: " + fileName);
                return true;
            }
        }

        Log.d("DownloadsFragment", "Not a video file: " + fileName);
        return false;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }



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

}
