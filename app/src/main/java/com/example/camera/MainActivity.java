package com.example.camera;

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

public class MainActivity extends AppCompatActivity {

    private static int REQUEST_CODE = 100;
    ImageView img;
    Button btnTake, btnSave;
    OutputStream outputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTake = findViewById(R.id.btnTake);
        btnSave = findViewById(R.id.btnSave);
        img = findViewById(R.id.img);

        if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                askPermissions();

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE) ;
    }


    private void saveImage() {
        Uri images;

        ContentResolver contentResolver = getContentResolver();
        images = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*");

        Uri uri = contentResolver.insert(images, contentValues);

        try {
            BitmapDrawable drawable = (BitmapDrawable)img.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);
            Toast.makeText(this, "Image has been saved", Toast.LENGTH_SHORT).show();
            img.setImageDrawable(getResources().getDrawable(R.drawable._584e386aee75abcd96a2be6e78f0db3a460348965f98dc317f8b3aad181fb90));
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE)
            return;

        if (grantResults.length <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE)
            return;

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        img.setImageBitmap(bitmap);
    }
}