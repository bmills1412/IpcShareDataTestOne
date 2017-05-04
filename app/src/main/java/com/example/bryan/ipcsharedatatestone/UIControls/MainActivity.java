package com.example.bryan.ipcsharedatatestone.UIControls;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;


import com.example.bryan.ipcsharedatatestone.Adapter.ColorAdapter;
import com.example.bryan.ipcsharedatatestone.CustomViews.ExtendActionViews;
import com.example.bryan.ipcsharedatatestone.CustomViews.PaintView;
import com.example.bryan.ipcsharedatatestone.DataStorage.ArtCacheContract;
import com.example.bryan.ipcsharedatatestone.DataStorage.FileUtils;
import com.example.bryan.ipcsharedatatestone.Interfaces.OnActionItemClicked;
import com.example.bryan.ipcsharedatatestone.R;
import com.example.bryan.ipcsharedatatestone.backgroundjobs.PaintFileService;


import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ColorAdapter.OnColorChosen, OnActionItemClicked{


    private PaintView paintView;
    private ColorChooser chooser;
    private ExtendActionViews extendActionViews;

    private FileUtils fileUtility;

    public static final String IMAGE_KEY = "artKey";
    public static final String IMAGE_NAME = "imageName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = (PaintView) findViewById(R.id.paintViewId);
        extendActionViews = (ExtendActionViews) findViewById(R.id.extendedViewId);


        paintView.setOnTouchListener(new OnDrawListener(extendActionViews));

        constructActionItems();
    }

    @Override
    public void onColorChosen(String color) {
        paintView.setCurrPathColor(Color.parseColor(color));
    }

    @Override
    public void onActionItemClicked(String actionState){
        switch(actionState){
            case ExtendActionViews.STATE_CLEAR: onClear();
                break;
            case ExtendActionViews.STATE_UNDO: onUndo();
                break;
            case ExtendActionViews.STATE_SAVE: onSave();
                break;
            case ExtendActionViews.STATE_PALLETE: onPallete();
                break;
            case ExtendActionViews.STATE_SIZE: onSize();
                break;
            default:
                break;
        }
    }

    private void constructActionItems() {
        Map<String, Integer> actionItems = new LinkedHashMap<>();
            actionItems.put(ExtendActionViews.STATE_CLEAR, R.drawable.clear);
            actionItems.put(ExtendActionViews.STATE_UNDO, R.drawable.undo);
            actionItems.put(ExtendActionViews.STATE_SAVE, R.drawable.download);
            actionItems.put(ExtendActionViews.STATE_PALLETE, R.drawable.pallete);
            actionItems.put(ExtendActionViews.STATE_SIZE, R.drawable.brush);

        extendActionViews.setActionStates(actionItems);
        extendActionViews.setOnActionItemClickedListener(this);
    }

    private void onClear() {
        paintView.clearPaths();
    }

    private void onUndo() {
        paintView.undo();
    }

    private void onSave() {
        final Bitmap image = paintView.getDrawingBitmap();
        if (image != null) {
            //Launch a Dialog asking the user for an image name as well.
            saveImage(image, "tempstring");
        }
    }

    private void onPallete() {
        this.chooser = new ColorChooser(this);
        this.chooser.setOnColorChosenClient(this);
        chooser.showAtLocation(paintView, Gravity.NO_GRAVITY, 0, 0);
    }

    private void onSize() {
        //launch size dialog...
    }

    private void saveImage(Bitmap art, String artName) {

        //byteArrayOutputStream contains an implicit Buffer, so no need to wrap :)
        final ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        art.compress(Bitmap.CompressFormat.PNG, 0, byteArrayStream);

        final ContentValues artByteValues = new ContentValues();
        artByteValues.put(ArtCacheContract.COL_BITMAP, byteArrayStream.toByteArray());
        artByteValues.put(ArtCacheContract.COL_NAME, artName);

        final Uri bitmapCacheUri = getContentResolver().insert(ArtCacheContract.ART_CACHE_URI, artByteValues);

        launchSaveFileService(bitmapCacheUri, "temp");
    }

    private void launchSaveFileService(Uri uri, String fileName){
        Intent saveFileIntent = new Intent(this, PaintFileService.class);
        saveFileIntent.putExtra(IMAGE_KEY, uri.toString());
        saveFileIntent.putExtra(IMAGE_NAME, fileName);
        startService(saveFileIntent);
    }


}
