package com.example.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int CAMERA_REQ_CODE = 100;
    static Uri uri;
    ImageView imgCamera;
    // Define constant variable for folder name
    private static final String FOLDER_NAME = "MyAppImages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermission();
        imgCamera = findViewById(R.id.imgCamera);
        Button btnCamera = findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(iCamera, CAMERA_REQ_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQ_CODE) {
                //camera
                if (data != null) {
                    Bitmap img = (Bitmap) data.getExtras().get("data");
                    imgCamera.setImageBitmap(img);

                    // Save the image to the device's MediaStore
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    String fileName = timeStamp + ".jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + FOLDER_NAME);
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    try {
                        OutputStream outputStream = getContentResolver().openOutputStream(uri);
                        if (outputStream != null) {
                            // Compress and save the image to the OutputStream
                            img.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                            outputStream.close();
                            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
                            MainActivity.uri = uri;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
    private void askPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        },0);
    }


}