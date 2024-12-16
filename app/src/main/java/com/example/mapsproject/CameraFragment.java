package com.example.mapsproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    private static final int REQUEST_CODE = 99; // Request code for camera intent
    private Button btnSnap; // Button to trigger camera
    private ImageView imageView; // ImageView to display captured image

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        // Initialize views
        btnSnap = view.findViewById(R.id.btncamera);
        imageView = view.findViewById(R.id.imageview1);

        // Set click listener for the snap button
        btnSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK && data != null) {
                Bitmap picTaken = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(picTaken);
                saveImageToGallery(picTaken);
            } else {
                Toast.makeText(getActivity(), "Camera Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToGallery(Bitmap bitmap) {
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getActivity().getContentResolver(),
                bitmap,
                "Captured Image",
                "Image of something"
        );

        if (savedImageURL != null) {
            Toast.makeText(getActivity(), "Image saved to gallery!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }

        if (savedImageURL != null) {
            Toast.makeText(getActivity(), "Image saved to gallery!", Toast.LENGTH_SHORT).show();

            // Notify media scanner about the new image
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.parse(savedImageURL));
            getActivity().sendBroadcast(mediaScanIntent);

        } else {
            Toast.makeText(getActivity(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
}