package com.nhom12.test.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nhom12.test.OnItemClickListener;
import com.nhom12.test.R;
import com.nhom12.test.structures.Album;

import java.util.ArrayList;
import com.nhom12.test.R;

import java.util.ArrayList;
public class GridAlbumAdapter extends RecyclerView.Adapter<GridAlbumAdapter.ViewHolder> implements Filterable {
    Context context;
    ArrayList<Album> albumList;
    ArrayList<Album> albumListTemp;
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public GridAlbumAdapter(Context context, ArrayList<Album> albumList) {
        this.context = context;
        this.albumList = albumList;
        this.albumListTemp = albumList;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String albumName = albumList.get(position).getName();
        holder.textView.setText(albumName);
        String pathFirstImage = albumList.get(position).getFirstImagesData();
            if(albumName.equals("Favorite")){
                holder.imageView.setImageResource(R.drawable.icon_favorite_album);
            } else if (albumName.equals("Remove")){
                holder.imageView.setImageResource(R.drawable.icon_garbage_album);
            } else if (albumName.equals("Private")){
                holder.imageView.setImageResource(R.drawable.icon_private_album);
            } else if(pathFirstImage.equals("")) {
                holder.imageView.setImageResource(R.drawable.icon_create_default_album);
            } else {
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
            }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(albumList.get(position));
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
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String keyWord = charSequence.toString();
                if(keyWord.isEmpty()){
                    albumList = albumListTemp;
                } else {
                    ArrayList<Album> albums = new ArrayList<>();
                    for(Album album : albumListTemp){
                        if(album.getName().toLowerCase().contains(keyWord.toLowerCase())){
                            albums.add(album);
                        }
                    }
                    albumList = albums;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = albumList;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                albumList = (ArrayList<Album>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}