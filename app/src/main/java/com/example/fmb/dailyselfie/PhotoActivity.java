package com.example.fmb.dailyselfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;
// Display the picture selected in the MainActivity ListView

public class PhotoActivity extends AppCompatActivity {

    ImageView photo;

    private final static String TAG = "fmbDailySelfie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        // Get the parameters passed by the intent
        Intent myIntent = getIntent(); // gets the previously created intent
        String photoName = myIntent.getStringExtra("Photo"); // will return the filename selected
        getPhoto(photoName);
    }

    public  void getPhoto(String fileName) {
        String storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File newfile = new File(storageDir+"/"+fileName);
        if(newfile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(newfile.getAbsolutePath());

            photo = (ImageView) findViewById(R.id.photo);;

            photo.setImageBitmap(myBitmap);

        }

    }
}
