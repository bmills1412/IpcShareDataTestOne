package com.example.bryan.ipcsharedatatestone.DataStorage;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * For bitmap scaling
 * https://www.youtube.com/watch?v=HY9aaXHx8yA
 */

public class ArtCacheProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.bryan.ipcscahreddatatestone.DataStorage.ArtCacheProvider";
    public static final Uri ART_CACHE_PROVIDER_URI = Uri.parse("content://" + AUTHORITY);

    private ArtCacheHelper artHelper;

    private static UriMatcher matcher;

    public static final int REQUEST_ALL = 1;
    public static final int REQUEST_ONE = 2;

    static{
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, ArtCacheContract.TABLE_NAME, REQUEST_ALL);
        matcher.addURI(AUTHORITY, ArtCacheContract.TABLE_NAME + "/#", REQUEST_ONE );
    }


    @Override
    public boolean onCreate(){
        artHelper = ArtCacheHelper.getArtCache(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return artHelper.getReadableDatabase().query(ArtCacheContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if(matcher.match(uri) == REQUEST_ALL)
            return ArtCacheContract.ART_CACHE_MIME_DIR;

     return ArtCacheContract.ART_CACHE_MIME_ITEM;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        //NOTE: when you delete a record, sqlite still maintains its internal counter for primary key
        //soo if i have 100 items, delete 5, and insert 1, the _id will be 101.

        long rowId = artHelper.getWritableDatabase().insert(ArtCacheContract.TABLE_NAME, null, values);

        if(rowId == -1)
            throw new SQLException("Could not insert into table: " + uri.getPath() + " of database: " + artHelper.getDatabaseName());


     return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
       return artHelper.getWritableDatabase().delete(ArtCacheContract.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return artHelper.getWritableDatabase().update(ArtCacheContract.TABLE_NAME, values, selection, selectionArgs);
    }
}
