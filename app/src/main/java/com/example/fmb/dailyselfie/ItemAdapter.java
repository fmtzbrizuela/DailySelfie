package com.example.fmb.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by fmb on 25/04/2016.
 */
public class ItemAdapter extends BaseAdapter {
    private Context context;
    private List<String> items;

    private final static String TAG = "fmb";
    int THUMBSIZE = 60;
    public ItemAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item, parent, false);
        }

        // Set data into the view.
        ImageView ivPhoto = (ImageView) rowView.findViewById(R.id.ivPhoto);
        TextView tvFileName = (TextView) rowView.findViewById(R.id.tvFilename);
        String fileName = items.get(position);
        tvFileName.setText(fileName);
        Log.d(TAG, "fileName: " + fileName);
        String storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File newfile = new File(storageDir+"/"+fileName);
        if(newfile.exists()){
            // becouse decodefile use too much memory, we will use this utility for thumbnail
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(newfile.getAbsolutePath()), THUMBSIZE, THUMBSIZE);
            ivPhoto.setImageBitmap(ThumbImage);

//            Bitmap myBitmap = BitmapFactory.decodeFile(newfile.getAbsolutePath());
//            ivPhoto.setImageBitmap(myBitmap);
//            Log.d(TAG,"file Existe");
        }
        if (rowView != null && position >= 0)
            rowView.setBackgroundColor(position % 2 == 0 ? Color.LTGRAY : Color.CYAN);
        return rowView;
    }

}
