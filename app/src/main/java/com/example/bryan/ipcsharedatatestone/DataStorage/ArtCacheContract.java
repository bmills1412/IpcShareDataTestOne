package com.example.bryan.ipcsharedatatestone.DataStorage;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by bryan on 4/28/2017.
 */

public class ArtCacheContract implements BaseColumns {

    public static final String TABLE_NAME = "artCacheTable";

    public static final String COL_BITMAP = "artCacheCol";

    public static final String COL_NAME = "artName";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_BITMAP + " blob not null," +
            COL_NAME + " text not null);";


   public static final Uri ART_CACHE_URI = Uri.withAppendedPath(ArtCacheProvider.ART_CACHE_PROVIDER_URI, TABLE_NAME);


   public static String ART_CACHE_MIME_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "art.png";
   public static String ART_CACHE_MIME_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "art.png";

}
