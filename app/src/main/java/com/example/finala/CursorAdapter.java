package com.example.finala;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finala.data.Contract;

public class CursorAdapter extends RecyclerView.Adapter<CursorAdapter.LocationViewHolder>{
    public Cursor mCursor;
    private Context mContext;



    public CursorAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CursorAdapter.LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.location_list_item, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursorAdapter.LocationViewHolder holder, final int position) {
        int idIndex = mCursor.getColumnIndex(Contract.Entry._ID);
        final int longitudeIndex = mCursor.getColumnIndex(Contract.Entry.COLUMN_LONGITUDE);
        int latitudeIndex = mCursor.getColumnIndex(Contract.Entry.COLUMN_LATITUDE);
        int nameIndex = mCursor.getColumnIndex(Contract.Entry.COLUMN_NAME);

        mCursor.moveToPosition(position);


        final int id = mCursor.getInt(idIndex);
        final String longitude = mCursor.getString(longitudeIndex);
        final String latitude = mCursor.getString(latitudeIndex);
        String name = mCursor.getString(nameIndex);

        holder.itemView.setTag(id);
        holder.idView.getText();
        holder.idView.setText(Integer.toString(id));
        holder.longitudeView.setText(longitude);
        holder.latitudeView.setText(latitude);
        holder.nameView.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //                被點擊的動作
            @Override
            public void onClick(View v) {
                Log.e("show_choose",Integer.toString(position));
                String stringId=Integer.toString(id);
//                Log.e("test","id:"+id);
                Uri uri= Contract.Entry.CONTENT_URI2;
                uri=uri.buildUpon().appendPath(stringId).build();
                Cursor c=mContext.getContentResolver().query(uri,null,null,null,null);

//                Log.e("after","true");
                String msg="";
            if(c.getCount()==1){
                c.moveToFirst();
                msg=String.valueOf(c.getInt(c.getColumnIndex("_id")));
                Log.e("msg",msg);
            }else{
                msg="null";
            }
            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
                    Log.e("position", String.valueOf(position));
                }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Uri gmmIntentUri = Uri.parse(("geo:0.0?q="+latitude+","+longitude));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(mapIntent);
            }
                Log.e("position1", String.valueOf(position));
                return false;
        }
        });


    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor cursor) {
        if (mCursor == cursor) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = cursor;

        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    public class LocationViewHolder extends RecyclerView.ViewHolder  {
        public View ItemView;
        TextView idView;
        TextView longitudeView;
        TextView latitudeView;
        TextView nameView;


        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemView = itemView;
            idView = (TextView) itemView.findViewById(R.id.show_id);
            longitudeView = (TextView) itemView.findViewById(R.id.show_longitude);
            latitudeView = (TextView) itemView.findViewById(R.id.show_latitude);
            nameView = (TextView) itemView.findViewById(R.id.show_name);
        }

    }

}
