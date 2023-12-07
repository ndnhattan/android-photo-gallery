package com.nhom12.test.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nhom12.test.Fragment_Photo;
import com.nhom12.test.R;
import com.nhom12.test.activities.DetailPhotoActivity;
import com.nhom12.test.activities.SelectActivity;
import com.nhom12.test.database.AlbumDbHelper;
import com.nhom12.test.database.DatabaseSingleton;

import java.util.ArrayList;

public class GridImageSelectAdapter extends RecyclerView.Adapter<GridImageSelectAdapter.ViewHolder> {
    private Cursor rs;
    private Context context;
    private int index;
    private ArrayList<String> album;
    private static int REQUEST_CODE_PIC = 10;
    AlbumDbHelper albumDbHelper; // db

    public GridImageSelectAdapter(Context context, Cursor rs, int index) {
        this.context = context;
        this.rs = rs;
        this.index = index;
    }

    @Override
    public GridImageSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        albumDbHelper = DatabaseSingleton.getInstance(context).getDbHelper();

        View view = inflater.inflate(R.layout.item_grid_image_select, parent, false);

        GridImageSelectAdapter.ViewHolder viewHolder = new GridImageSelectAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GridImageSelectAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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

            }
        });
        if (SelectActivity.checkedArr.contains(SelectActivity.indexArr.get(index) + position)) {
            holder.checkBox.setChecked(true);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectActivity.checkedArr.contains(SelectActivity.indexArr.get(index) + position)) {
                    SelectActivity.checkedArr.remove(SelectActivity.indexArr.get(index) + position);
                } else {
                    SelectActivity.checkedArr.add(SelectActivity.indexArr.get(index) + position);
                }
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectActivity.checkedArr.contains(SelectActivity.indexArr.get(index) + position)) {
                    SelectActivity.checkedArr.remove(SelectActivity.indexArr.get(index) + position);
                } else {
                    SelectActivity.checkedArr.add(SelectActivity.indexArr.get(index) + position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return rs.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            checkBox = itemView.findViewById(R.id.my_checkbox);
        }

    }
}
