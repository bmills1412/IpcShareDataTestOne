package com.example.bryan.ipcsharedatatestone.DataStorage;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by bryan on 4/28/2017.
 */

public class ArtCacheHelper extends SQLiteOpenHelper {

    public static final String ART_CACHE_DB_NAME = "artCache.db";

    public static final int VERSION = 1;

    public static ArtCacheHelper getArtCache(Context context){
        return new ArtCacheHelper(context, ART_CACHE_DB_NAME, null, VERSION);
    }

    public ArtCacheHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, ART_CACHE_DB_NAME, factory, VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ArtCacheContract.CREATE_TABLE);
        Log.i("test", ArtCacheContract.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ArtCacheContract.TABLE_NAME);
        onCreate(db);
    }



}
