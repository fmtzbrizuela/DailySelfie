package com.example.fmb.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    File photoFile = null;
    static final int REQUEST_TAKE_PHOTO = 1;

    ListView lvPhotos;                  // ListView to display pictures
    BaseAdapter lvPicAdapter;  // adapter for handle data of lvPhotos
    ArrayList<String> listPic;          // List associated with lvPhotos

    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;

    private static final long INITIAL_ALARM_DELAY = 2 * 60 * 1000L;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final static String TAG = "fmbDailySelfie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // setup the alarm
        // Get the AlarmManager Service
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(MainActivity.this,
                AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, mNotificationReceiverIntent, 0);
        // Set up inexact repeating alarm
        setAlarm();

        // create and attache the adapter to the Listview
        // Get the list of picture files
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
         listPic = getListFiles(storageDir); // get list of pictures order descending

        lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        lvPicAdapter = new ItemAdapter(this, listPic);
        lvPhotos.setAdapter(lvPicAdapter);

        // attach a listener to the ListView to react to item click events
        lvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callDisplayPhoto(position);
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

    // When the pic is taken, the file name is inserted in the list, sorted and refresh
      @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
              if(photoFile.exists()){
                    listPic.add((photoFile.getName())); // add the name to the list
                    Collections.sort(listPic, Collections.reverseOrder());
                    lvPicAdapter.notifyDataSetChanged();
            }
          }
      }

    // get list of files with jpg extension, order descending
    private ArrayList<String> getListFiles(File parentDir) {
        ArrayList<String> inFiles = new ArrayList<String>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
          //      inFiles.addAll(getListFiles(file));
            } else {
                if(file.getName().endsWith(".jpg")){
                    inFiles.add(file.getName());
                }
            }
        }
         Collections.sort(inFiles, Collections.reverseOrder());
        return inFiles;
    }

    private void callDisplayPhoto(int index){
        Intent myIntent = new Intent(this, PhotoActivity.class);
        myIntent.putExtra("Photo", listPic.get(index));
        startActivity(myIntent);
    }

    // Set up inexact repeating alarm
    private void setAlarm() {
        Log.e(TAG, "Enter setAlarm");
        // Set inexact repeating alarm
        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                INITIAL_ALARM_DELAY,
                mNotificationReceiverPendingIntent);

        Toast.makeText(getApplicationContext(),
                "Inexact Repeating Alarm Set", Toast.LENGTH_LONG)
                .show();
    }
}
