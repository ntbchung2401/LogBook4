package com.example.logbook4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements RequestPermission {
    private static final int CAMERA_PERM_CODE = 101 ;
    private static final int CAMERA_REQUEST_CODE = 102;
    ImageView selectedImage;
    ImageView imageView;
    Button camera_btn,backward_image_btn,forward_image_btn, save_to_gallery_button;
    private static final int REQUEST_CODE = 1;
    int i = 0;
    private int [] textureArrayWin = {};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        selectedImage = findViewById(R.id.image_view);
        backward_image_btn = findViewById(R.id.backward_image_btn);
        forward_image_btn = findViewById(R.id.forward_image_btn);
        save_to_gallery_button = findViewById(R.id.save_to_gallery_button);
        if(i == 0){
            backward_image_btn.setVisibility(View.GONE);
        }
        if(i == 1){
            forward_image_btn.setVisibility(View.GONE);
        }
        backward_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageResource(textureArrayWin[i]);
                i--;
                if(i == 0){
                    backward_image_btn.setVisibility(View.GONE);
                }else {
                    backward_image_btn.setVisibility(View.VISIBLE);
                }
                if (i == 2){
                    forward_image_btn.setVisibility(View.GONE);
                }else {
                    forward_image_btn.setVisibility(View.VISIBLE);
                }
            }
        });
        forward_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageResource(textureArrayWin[i]);
                i++;
                if(i == 0){
                    backward_image_btn.setVisibility(View.GONE);
                }else {
                    backward_image_btn.setVisibility(View.VISIBLE);
                }
                if (i == 1){
                    forward_image_btn.setVisibility(View.GONE);
                }else {
                    forward_image_btn.setVisibility(View.VISIBLE);
                }
            }
        });
        camera_btn = findViewById(R.id.camera_btn);
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Toast.makeText(MainActivity.this,"Camera Btn is Clicked!", Toast.LENGTH_SHORT).show();*/
                askCameraPermission();
            }

        });
        save_to_gallery_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    saveImage();
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },REQUEST_CODE);
                }
            }
        });
    }
    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            openCamera();
        }
    }
    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permission, @NonNull int[] grandResults){
        if(requestCode == REQUEST_CODE){
            if(grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED){
                saveImage();
            }else {
                Toast.makeText(MainActivity.this, "Please provide required permission",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permission, grandResults);
    }





    private void saveImage(){
        Uri images;
        ContentResolver contentResolver = getContentResolver();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            images = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }else {
            images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*");
        Uri uri = contentResolver.insert(images,contentValues);
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);
            Toast.makeText(MainActivity.this, "Image Save To Gallery Successfully!!!",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Image Not Saved!!!",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use Camera!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openCamera(){
        /* Toast.makeText(this, "Camera Open Request",Toast.LENGTH_SHORT).show();*/
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE){
            Bitmap image =(Bitmap) data.getExtras().get("data");
            selectedImage.setImageBitmap(image);
        }
    }
}