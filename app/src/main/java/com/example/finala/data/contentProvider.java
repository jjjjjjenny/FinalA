package com.example.finala.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.ContentProvider;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.finala.data.Contract.Entry.TABLE_NAME;

public class contentProvider extends ContentProvider {

    public static final int LOCATION = 100;
    public static final int LOCATION_WITH_ID = 101;
    public static final int LOCATION2 = 200;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DbHelper mDbHelper;

    public static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Contract.AUTHORITY,Contract.PATH_LOCATION,LOCATION);//1
        uriMatcher.addURI(Contract.AUTHORITY,Contract.PATH_LOCATION+"/#",LOCATION_WITH_ID);//2
        uriMatcher.addURI(Contract.AUTHORITY,Contract.PATH_NEARBY+"/#",LOCATION2);//3

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new DbHelper(context);

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        TestUtil testUtil = new TestUtil();
        testUtil.insertFakeData(db);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
//        Log.e("test","uri:"+uri);
        long mid=-1;
        Cursor retCursor;
        switch (match) {
            case LOCATION:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case LOCATION2:
                String id=uri.getPathSegments().get(1);
                Log.e("分隔線","-----------------------------");
                Log.e("test","getid:"+id);
                retCursor =  db.rawQuery("SELECT * FROM location WHERE _id='"+id+"'",null);
                retCursor.moveToFirst();

                double mlongitute=Double.valueOf(retCursor.getString(retCursor.getColumnIndex("longitude")));
                double mlatitute=Double.valueOf(retCursor.getString(retCursor.getColumnIndex("latitude")));

                retCursor.close();

                int minResult=0;
                double min=0.0;
                double min_dis=Double.MAX_VALUE;

                double find_longitude=-1.00;
                double find_latitude=-1.00;

                Cursor fcursor=db.rawQuery("SELECT * FROM location WHERE _id!='"+id+"'",null);
                Log.e("num","num:"+fcursor.getCount());
                fcursor.moveToFirst();

                if(fcursor.getCount()>0){
                    for(int i=0;i<fcursor.getCount();i++){
//                        Log.e("inner","num:"+fcursor.getCount());
                        float dis[]=new float[1];

                        int fid=fcursor.getInt(fcursor.getColumnIndex("_id"));
//                        Log.e("column","index:"+fcursor.getColumnIndex("_id"));
                        Log.e("fid","fid:"+fid);

                        find_longitude=Double.valueOf(fcursor.getString(fcursor.getColumnIndex("longitude")));
                        find_latitude=Double.valueOf(fcursor.getString(fcursor.getColumnIndex("latitude")));
                        Location.distanceBetween(mlatitute,mlongitute,find_latitude,find_longitude,dis);

                        Log.e("dis","dis:"+dis[0]);
//                        Log.e("min_dis","min_dis:"+min_dis);

                        if(dis[0]<min_dis){
                            min_dis=dis[0];
                            minResult=fid;
//                            Log.e("min_result","fid:"+fid);
//                            Log.e("min_result","id:"+minResult);
                        }

                        fcursor.moveToNext();

                    }
                    Log.e("分隔線","-----------------------------");
                    retCursor =  db.rawQuery("SELECT * FROM location WHERE _id='"+minResult+"'",null);
                }
                else{
                    return null;
                }

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case LOCATION:
                return "vnd.android.cursor.dir" + "/" + Contract.AUTHORITY + "/" + Contract.PATH_LOCATION;
            case LOCATION_WITH_ID:
                return "vnd.android.cursor.item" + "/" + Contract.AUTHORITY + "/" + Contract.PATH_LOCATION;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case LOCATION:
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(Contract.Entry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int Deleted;
        switch (match) {
            case LOCATION_WITH_ID:
                String id = uri.getPathSegments().get(1);
                Deleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (Deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return Deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        int Updated;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case LOCATION_WITH_ID:
                String id = uri.getPathSegments().get(1);
                Updated = db.update(TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (Updated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return Updated;
    }
}
