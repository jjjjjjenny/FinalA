package com.example.finala.data;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues contentValues= new ContentValues();
        contentValues.put(Contract.Entry.COLUMN_LONGITUDE, 175);
        contentValues.put(Contract.Entry.COLUMN_LATITUDE, 45);
        contentValues.put(Contract.Entry.COLUMN_NAME, "Test1");
        list.add(contentValues);

        contentValues= new ContentValues();
        contentValues.put(Contract.Entry.COLUMN_LONGITUDE, 169);
        contentValues.put(Contract.Entry.COLUMN_LATITUDE, 44);
        contentValues.put(Contract.Entry.COLUMN_NAME, "Test2");
        list.add(contentValues);

        contentValues= new ContentValues();
        contentValues.put(Contract.Entry.COLUMN_LONGITUDE, 119);
        contentValues.put(Contract.Entry.COLUMN_LATITUDE, 25);
        contentValues.put(Contract.Entry.COLUMN_NAME, "Test3");
        list.add(contentValues);

        contentValues= new ContentValues();
        contentValues.put(Contract.Entry.COLUMN_LONGITUDE, 118);
        contentValues.put(Contract.Entry.COLUMN_LATITUDE, 27);
        contentValues.put(Contract.Entry.COLUMN_NAME, "Test4");
        list.add(contentValues);

        contentValues= new ContentValues();
        contentValues.put(Contract.Entry.COLUMN_LONGITUDE, 121.540226);
        contentValues.put(Contract.Entry.COLUMN_LATITUDE, 25.0169544);
        contentValues.put(Contract.Entry.COLUMN_NAME, "Test5");
        list.add(contentValues);

        try
        {
            db.beginTransaction();
            db.delete (Contract.Entry.TABLE_NAME,null,null);
            for(ContentValues c:list){
                db.insert(Contract.Entry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}
