package com.example.bryan.ipcsharedatatestone.backgroundjobs;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bryan.ipcsharedatatestone.DataStorage.ArtCacheContract;
import com.example.bryan.ipcsharedatatestone.DataStorage.FileUtils;
import com.example.bryan.ipcsharedatatestone.UIControls.MainActivity;

import java.io.IOException;
import java.net.URI;

import javax.xml.transform.URIResolver;

/**
 * It's important to remember that a Service, though independent of the foreground component, still runs code
 * in the MainThread; Therefore, i must create a new custom Thread when using a service.
 */

public class PaintFileService extends Service {

    private FileUtils fileUtils;
    private Thread fileSaveThread;
    private Uri cacheRes;
    private String fileName;

    @Override
    public void onCreate() {
        if(fileUtils==null)
      fileUtils = new FileUtils();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        fileName = intent.getExtras().getString(MainActivity.IMAGE_NAME);
        cacheRes = Uri.parse(intent.getExtras().getString(MainActivity.IMAGE_KEY));

        fileUtils.setupFileUtils(getFilesDir());

        fileSaveThread = new Thread(saveImageRunnable);
        fileSaveThread.start();

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Returns NULL if i don't want to bind the service
        return null;
    }



    private Runnable saveImageRunnable = new Runnable() {
        @Override
        public void run() {
          final Cursor artData = getContentResolver().query(cacheRes,
                    new String[]{ArtCacheContract.COL_BITMAP, ArtCacheContract.COL_NAME},
                    ArtCacheContract.COL_NAME+"=?",
                    new String[]{fileName},
                    null);

            byte[] image = null;
            String artName = null;

            if(artData!=null)
            if(artData.getCount()!=0){
                artData.moveToFirst();

                final int artIndex = artData.getColumnIndex(ArtCacheContract.COL_BITMAP);
                final int artNameIndex = artData.getColumnIndex(ArtCacheContract.COL_NAME);

                image = artData.getBlob(artIndex);
                artName = artData.getString(artNameIndex);
            }

            try {
                fileUtils.saveImageToFile(image, artName);
            }catch (IOException e){
                Log.i("test", "Saving the image to the file went wrong ;/");
            }


           long num = getContentResolver().delete(cacheRes, ArtCacheContract.COL_NAME+"=?", new String[]{fileName});
            Log.i("test", "NUMBER OF RECORDS DELETED FROM CACHE: " + String.valueOf(num));

        }


    };



}
