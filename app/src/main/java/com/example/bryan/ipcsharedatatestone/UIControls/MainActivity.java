package com.example.bryan.ipcsharedatatestone.UIControls;

import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.example.bryan.ipcsharedatatestone.Adapter.ColorAdapter;
import com.example.bryan.ipcsharedatatestone.CustomViews.ExtendActionViews;
import com.example.bryan.ipcsharedatatestone.CustomViews.PaintView;
import com.example.bryan.ipcsharedatatestone.DataStorage.ArtCacheContract;
import com.example.bryan.ipcsharedatatestone.Interfaces.OnActionItemClicked;
import com.example.bryan.ipcsharedatatestone.R;
import com.example.bryan.ipcsharedatatestone.backgroundjobs.PaintFileService;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ColorAdapter.OnColorChosen, OnActionItemClicked{


    private PaintView paintView;
    private ColorChooser chooser;
    private ExtendActionViews extendActionViews;

    public static final String IMAGEE_KEY = "artKey";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = (PaintView) findViewById(R.id.paintViewId);
        paintView.setOnTouchListener(this.onDrawListener);

        extendActionViews = (ExtendActionViews) findViewById(R.id.extendedViewId);

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
            saveImage(image, "temp string");
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

        art.compress(Bitmap.CompressFormat.PNG, 0, byteArrayStream); //should i compress it?

        final ContentValues artByteValues = new ContentValues();
        artByteValues.put(ArtCacheContract.COL_BITMAP, byteArrayStream.toByteArray()); //temp bytes
        //final Uri bitmapCacheUri = getContentResolver().insert(ArtCacheContract.ART_CACHE_URI, artByteValues);

        tempSaveToFileTest(byteArrayStream.toByteArray());
    }


    private void tempSaveToFileTest(byte[] imageBytes){

    }

    private void saveToFile(Uri uri){
        Intent saveFileIntent = new Intent(this, PaintFileService.class);
        saveFileIntent.putExtra(IMAGEE_KEY, uri);
        startService(saveFileIntent);
    }


    private boolean shouldHide = true;

      // show/hide all views IF the user is drawing.
      // boolean cond shouldHide is to avoid redundant alpha animation for each move event
     // If more views are added to the content views i can simply place them in the animate... methods

    private View.OnTouchListener onDrawListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                if(shouldHide) {
                    TopViewsAnimator.animateViewsAlpha(0f, 100, extendActionViews);
                    shouldHide = false;
                }
            }
            else if(event.getActionMasked() == MotionEvent.ACTION_CANCEL||event.getActionMasked() == MotionEvent.ACTION_UP) {
                TopViewsAnimator.animateViewsAlpha(1f, 100, extendActionViews);
                shouldHide = true;
            }

            return false;
        }
    };






}
