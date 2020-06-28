package com.example.finala.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    public static final String AUTHORITY="com.example.finala";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+AUTHORITY);
    public static final String PATH_LOCATION = "location";
    public static final String PATH_NEARBY = "nearby";


    public static final class Entry implements BaseColumns {

        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final Uri CONTENT_URI2=BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEARBY).build();

        public static final String TABLE_NAME = "location";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_NAME = "name";


    }
}
