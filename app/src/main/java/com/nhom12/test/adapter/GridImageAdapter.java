package com.nhom12.test.adapter;

import android.app.usage.ExternalStorageStats;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.nhom12.test.R;
import com.nhom12.test.activities.DetailPhotoActivity;

import androidx.recyclerview.widget.RecyclerView;

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ViewHolder> {
    private Cursor rs;
    private Context context;

    public GridImageAdapter(Context context, Cursor rs) {
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
                Intent myIntent = new Intent(context, DetailPhotoActivity.class);
                myIntent.putExtra("path", path);
                int dateColumnIndex = rs.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                String imageDate = rs.getString(dateColumnIndex);
                myIntent.putExtra("date", imageDate);
                context.startActivity(myIntent);
            }
        });
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