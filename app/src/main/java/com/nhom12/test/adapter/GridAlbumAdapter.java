package com.nhom12.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nhom12.test.OnItemClickListener;
import com.nhom12.test.R;
import com.nhom12.test.structures.Album;

import java.util.ArrayList;

public class GridAlbumAdapter extends RecyclerView.Adapter<GridAlbumAdapter.ViewHolder> {
    Context context;
    ArrayList<Album> albumList;
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public GridAlbumAdapter(Context context, ArrayList<Album> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_list_album, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String albumName = albumList.get(position).getName();
        holder.textView.setText(albumName);
        String pathFirstImage = albumList.get(position).getFirstImagesData();
        if(!pathFirstImage.equals("")){
            // Create RequestOptions to specify image loading options
            RequestOptions options = new RequestOptions()
                    //.placeholder(R.drawable.placeholder) // Placeholder image while loading
                    //.error(R.drawable.error) // Error image if loading fails
                    .diskCacheStrategy(DiskCacheStrategy.ALL); // Caching strategy

            // Load the image with Glide
            Glide.with(context)
                    .load(albumList.get(position).getFirstImagesData())
                    .apply(options) // Apply the RequestOptions
                    .into(holder.imageView); // Display the image in the ImageView
        } else {
            if(albumName.equals("Favorite")){
                holder.imageView.setImageResource(R.drawable.icon_favorite_album);
            } else if (albumName.equals("Remove")){
                holder.imageView.setImageResource(R.drawable.icon_garbage_album);
            } else if (albumName.equals("Private")){
                holder.imageView.setImageResource(R.drawable.icon_private_album);
            } else {
                holder.imageView.setImageResource(R.drawable.icon_create_default_album);
            }
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textAlbum);
            imageView = itemView.findViewById(R.id.buttonAll);
        }

    }

}
