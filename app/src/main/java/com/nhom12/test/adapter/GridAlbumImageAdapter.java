package com.nhom12.test.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nhom12.test.R;
import com.nhom12.test.activities.DetailPhotoActivity;
import com.nhom12.test.activities.DetailRemovePhotoActivity;

public class GridAlbumImageAdapter extends RecyclerView.Adapter<GridAlbumImageAdapter.ViewHolder>{
    private Cursor rs;
    private Context context;

    public GridAlbumImageAdapter(Context context, Cursor rs) {
        this.context = context;
        this.rs = rs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_grid_image, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        rs.moveToPosition(position);
        String path = rs.getString(1);
        long imageId = rs.getLong(0);
        Long albumID = rs.getLong(3);

        // Create RequestOptions to specify image loading options
        RequestOptions options = new RequestOptions()
                //.placeholder(R.drawable.placeholder) // Placeholder image while loading
                //.error(R.drawable.error) // Error image if loading fails
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Caching strategy

        // Load the image with Glide
        Glide.with(context)
                .load(path)
                .apply(options) // Apply the RequestOptions
                .into(holder.imageView); // Display the image in the ImageView

        holder.imageView.setLayoutParams(new AbsListView.LayoutParams(250, 250));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(albumID == 2){
                    Intent myIntentRemove = new Intent(context, DetailRemovePhotoActivity.class);
                    myIntentRemove.putExtra("path", path);
                    String imageDate = rs.getString(2);
                    myIntentRemove.putExtra("date", imageDate);
                    myIntentRemove.putExtra("id", imageId);
                    myIntentRemove.putExtra("albumId", albumID);
                    context.startActivity(myIntentRemove);
                } else {
                    Intent myIntent = new Intent(context, DetailPhotoActivity.class);
                    myIntent.putExtra("path", path);
                    String imageDate = rs.getString(2);
                    myIntent.putExtra("date", imageDate);
                    myIntent.putExtra("id", imageId);
                    myIntent.putExtra("albumId", albumID);
                    context.startActivity(myIntent);
                }
            }
        });

        //DT

    }


    @Override
    public int getItemCount() {
        return rs.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }
}
