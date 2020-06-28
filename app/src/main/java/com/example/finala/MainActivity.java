package com.example.finala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finala.data.Contract;
import com.example.finala.data.TestUtil;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_LOADER_ID = 0;

    private CursorAdapter mAdapter;
    RecyclerView mRecyclerView;

    private EditText longitude;
    private EditText latitude;
    private EditText name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longitude=(EditText)findViewById(R.id.longitude);
        latitude=(EditText)findViewById(R.id.latitude);
        name=(EditText)findViewById(R.id.name);


        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AlertDialog.Builder a=new AlertDialog.Builder(MainActivity.this);
                a.setTitle("確定？");
                a.setMessage("是否確定刪除呢！");
                a.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int id= (int)viewHolder.itemView.getTag();
                        String stringId=Integer.toString(id);
                        Uri uri= Contract.Entry.CONTENT_URI;
                        uri=uri.buildUpon().appendPath(stringId).build();
                        getContentResolver().delete(uri,null,null);
                        getSupportLoaderManager().restartLoader(LOCATION_LOADER_ID,null,MainActivity.this);
                        AlertDialog.Builder a=new AlertDialog.Builder(MainActivity.this);
                    }

                });

                a.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        getSupportLoaderManager().restartLoader(LOCATION_LOADER_ID,null,MainActivity.this);
                        arg0.dismiss();
                    }

                });

                a.show();//記得R
            }
        }).attachToRecyclerView(mRecyclerView);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOCATION_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(Contract.Entry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void ClickConfirm(View view) {
        EditText longi=(EditText)findViewById(R.id.longitude);
        EditText lati=(EditText)findViewById(R.id.latitude);
        EditText na=(EditText)findViewById(R.id.name);


        String eLongitude = longi.getText().toString();
        String eLatitude = lati.getText().toString();
        String eName = na.getText().toString();

        if (eLongitude.length() == 0||eLatitude.length() == 0||eName.length() == 0) {
            Toast.makeText(this,"確認一下是否都有輸入喔！😯",Toast.LENGTH_LONG).show();
            return;
        }
        if(Integer.parseInt(eLatitude)>90&&Integer.parseInt(eLongitude)>180){
            Toast.makeText(this,"緯度為0~90度！\n經度為0~180度！\n請再次輸入喔！！",Toast.LENGTH_LONG).show();
            latitude.getText().clear();
            longitude.getText().clear();
            name.clearFocus();
            return;
        }
        if(Integer.parseInt(eLatitude)>90){
            Toast.makeText(this,"緯度為0~90度！\n經度為0~180度！\n請再次輸入喔！！",Toast.LENGTH_LONG).show();
            latitude.getText().clear();
            name.clearFocus();
            return;
        }
        if(Integer.parseInt(eLongitude)>180){
            Toast.makeText(this,"緯度為0~90度！\n經度為0~180度！\n請再次輸入喔！！",Toast.LENGTH_LONG).show();
            longitude.getText().clear();
            name.clearFocus();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Entry.COLUMN_LONGITUDE, eLongitude);
        contentValues.put(Contract.Entry.COLUMN_LATITUDE, eLatitude);
        contentValues.put(Contract.Entry.COLUMN_NAME, eName);

        Uri uri = getContentResolver().insert(Contract.Entry.CONTENT_URI, contentValues);
        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }

        mAdapter.notifyDataSetChanged();
        getSupportLoaderManager().restartLoader(LOCATION_LOADER_ID,null,MainActivity.this);

        longitude.getText().clear();
        latitude.getText().clear();
        name.getText().clear();



    }


}
