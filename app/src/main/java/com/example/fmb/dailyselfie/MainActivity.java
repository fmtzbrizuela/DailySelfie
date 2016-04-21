package com.example.fmb.dailyselfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String mCurrentPhotoPath;
    File photoFile = null;
    static final int REQUEST_TAKE_PHOTO = 1;
    ImageView photoView;

    ListView lvPhotos;                  // ListView to display pictures
    ArrayAdapter<String> lvPicAdapter;  // adapter for handle data of lvPhotos
    ArrayList<String> listPic;          // List associated with lvPhotos

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final static String TAG = "fmbDailySelfie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photoView = (ImageView) findViewById(R.id.photoView);

        // create and attache the adapter to the Listview
        // Get the list of picture files
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        List<File> picFiles = getListFiles(storageDir);

        lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        // inflate the line that will be in the LineView
        View v = LayoutInflater.from(this).inflate(R.layout.singlesmall_line, lvPhotos, false);
        lvPicAdapter = new SpecialAdapter(this,R.layout.singlesmall_line, listPic);
        lvPhotos.setAdapter(lvPicAdapter);

        // attach a listener to the ListView to react to item click events
        lvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callDisplayPhoto(position);
            }
        });


        Button btnGetLastPic = (Button) findViewById(R.id.btnGetLastPic);
        btnGetLastPic.setOnClickListener(new View.OnClickListener() {
            public  void onClick(View v) {
                if(photoFile.exists()){

                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                    ImageView myImage = (ImageView) findViewById(R.id.photoView);

                    myImage.setImageBitmap(myBitmap);

                }

            }
        });
    }
    //  **************** Menu Processing
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.takephoto:
                Log.e(TAG, "takephoto");
                dispatchTakePictureIntent();
                return true;
        }
        return false;
    }
    //  **************** End Menu Processing


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        Log.e(TAG, "imageFileName:"+imageFileName);
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        Log.e(TAG, "storageDir:"+storageDir.toString());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.e(TAG, "image:"+image.getName()+" "+image.getAbsolutePath()+" "+image.getCanonicalPath());
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }



    private void dispatchTakePictureIntent() {
        photoFile = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error occurred while creating the File");
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }


    }

      @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
              if(photoFile.exists()){
                Log.e(TAG, "PhotoFile existe, lo va a desplegar");
                Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ImageView myImage = (ImageView) findViewById(R.id.photoView);
                myImage.setImageBitmap(myBitmap);
            }
          }
      }

    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        listPic = new ArrayList<String>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
          //      inFiles.addAll(getListFiles(file));
            } else {
                if(file.getName().endsWith(".jpg")){
                    inFiles.add(file);
                    listPic.add(file.getName());
                }
            }
        }
        return inFiles;
    }

    private void callDisplayPhoto(int index){
        Intent myIntent = new Intent(this, PhotoActivity.class);
        myIntent.putExtra("Photo", listPic.get(index));
        startActivity(myIntent);
    }
}
