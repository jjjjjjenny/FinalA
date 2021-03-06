package com.example.finala.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.finala.data.Contract.Entry;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "locationDb.db";
    private static final int VERSION = 1;

    DbHelper(Context context){
        super(context, DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + Contract.Entry.TABLE_NAME + " (" +
                Entry._ID                + " INTEGER PRIMARY KEY, " +
                Entry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                Entry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                Entry.COLUMN_NAME + " TEXT NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Entry.TABLE_NAME);
        onCreate(db);
    }
}
