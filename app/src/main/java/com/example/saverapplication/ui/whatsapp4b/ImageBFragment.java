package com.example.saverapplication.ui.whatsapp4b;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.saverapplication.R;
import com.example.saverapplication.ui.downloads.ImageViewerActivity;
import com.example.saverapplication.ui.whatsapp.images.ImageData;
import com.example.saverapplication.ui.whatsapp.images.ImagesAdapter;
import com.example.saverapplication.ui.whatsapp.images.ImagesFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageBFragment extends Fragment {
    public ImageBFragment() {
        // Required empty public constructor
    }

    public static ImageBFragment newInstance() {
        return new ImageBFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_b, container, false);

        // Initialize RecyclerView with GridLayoutManager
        RecyclerView recyclerView = view.findViewById(R.id.wb_recycler_view_images);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 items per row

        // Set up an item click listener for handling image clicks and downloads
        ImagesAdapter.OnItemClickListener itemClickListener = new ImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String imageUri) {
                openImage(imageUri);
            }

            @Override
            public void onDownloadClick(String imageUri) {
                downloadImage(imageUri);
            }
        };

        // Create and set the adapter for the RecyclerView
        ImagesAdapter imagesAdapter = new ImagesAdapter(requireContext(), getImageData(), itemClickListener);
        recyclerView.setAdapter(imagesAdapter);

        return view;
    }

    // Helper method to get a list of ImageData from the WhatsApp Status directory
    private List<ImageData> getImageData() {
        List<ImageData> imageDataList = new ArrayList<>();

        // Get the directory where image files are stored
        File whatsappBusinessDirectory = new File(Environment.getExternalStorageDirectory(), "WhatsApp Business/Media/.Statuses");
        if (whatsappBusinessDirectory.exists() && whatsappBusinessDirectory.isDirectory()) {
            File[] files = whatsappBusinessDirectory.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Check if it's a file and has an image extension (e.g., .jpg, .png)
                    if (file.isFile() && isImageFile(file)) {
                        // Convert the file path to a URI and add it to the list
                        Uri imageUri = Uri.fromFile(file);

                        // Assuming you have a method to get the thumbnail URI for the image
                        Uri thumbnailUri = getThumbnailUriForImage(file);

                        ImageData imageData = new ImageData(imageUri.toString(), thumbnailUri.toString());
                        imageDataList.add(imageData);
                    }
                }
            }
        } else {
            Log.e("ImagesFragment", "WhatsApp Business Status directory not found.");
        }

        return imageDataList;
    }

    // Add a method to check if a file is an image file based on its extension
    private boolean isImageFile(File file) {
        // Add more image extensions if needed
        String[] imageExtensions = new String[]{".jpg", ".png", ".jpeg"};
        String fileName = file.getName().toLowerCase();

        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }

        return false;
    }

    // Add a method to get the thumbnail URI for the image (you can use a suitable approach here)
    private Uri getThumbnailUriForImage(File imageFile) {
        try {
            // Decode the original image file to get a Bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Use higher quality configuration
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            // Calculate the inSampleSize to get a smaller thumbnail
            options.inSampleSize = calculateInSampleSize(options, 100, 100); // Adjust the dimensions as needed

            // Decode the image with the calculated inSampleSize and higher quality configuration
            options.inJustDecodeBounds = false;
            Bitmap thumbnailBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            // Save the thumbnail to a temporary file
            File thumbnailFile = saveThumbnailToFile(thumbnailBitmap, "thumbnail_" + imageFile.getName());

            // Return the URI of the thumbnail file
            return Uri.fromFile(thumbnailFile);
        } catch (Exception e) {
            e.printStackTrace();
            // Return the original image URI as a fallback
            return Uri.fromFile(imageFile);
        }
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

    private File saveThumbnailToFile(Bitmap thumbnailBitmap, String fileName) {
        File thumbnailFile = new File(requireContext().getCacheDir(), fileName);
        try {
            FileOutputStream fos = new FileOutputStream(thumbnailFile);
            thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbnailFile;
    }


    // Handle opening the image using an Intent
    private void openImage(String imageUri) {
        Log.d("ImageUri", imageUri);

        // Create an Intent to open the ImageViewerActivity for preview
        Intent intent = new Intent(requireContext(), ImageViewerActivity.class);
        intent.putExtra("imageUri", imageUri); // Pass the URI of the image to preview
        startActivity(intent);
    }


    private void downloadImage(String imageUri) {
        Uri uri = Uri.parse(imageUri);

        // Set destination directory for the downloaded file in DCIM/StatusSaver
        File dcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File statusSaverDirectory = new File(dcimDirectory, "StatusSaver");

        // Create the directory if it doesn't exist
        if (!statusSaverDirectory.exists()) {
            statusSaverDirectory.mkdirs();
        }

        // Generate a unique file name using timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "image_" + timeStamp + "." + getFileExtension(uri);

        // Set the destination path including the directory and unique file name
        File destinationFile = new File(statusSaverDirectory, fileName);

        // Copy the file using file I/O
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            // Notify the system that a new file has been created
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(destinationFile));
            requireContext().sendBroadcast(mediaScanIntent);

            Toast.makeText(requireContext(), "Image downloaded successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

        // Ensure extension is not null or empty
        return (extension != null && !extension.isEmpty()) ? extension : "jpg";
    }

}
