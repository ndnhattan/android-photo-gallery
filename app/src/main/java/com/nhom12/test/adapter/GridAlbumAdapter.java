package com.nhom12.test.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nhom12.test.R;

import java.util.ArrayList;

public class GridAlbumAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<Integer> iconAlbum;
    ArrayList<String> nameAlbum;


    public GridAlbumAdapter(@NonNull Context context, int layoutToBeInflated, ArrayList<String> nameAlbum, ArrayList<Integer> iconAlbum) {
        super(context, R.layout.item_list_album, nameAlbum);
        this.context = context;
        this.nameAlbum = nameAlbum;
        this.iconAlbum = iconAlbum;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.item_list_album, null);
        TextView textAlbum = (TextView) row.findViewById(R.id.textAlbum);
        ImageView icon = (ImageView) row.findViewById(R.id.buttonAll);
        icon.setImageResource(iconAlbum.get(position));
        textAlbum.setText(nameAlbum.get(position));
        return (row);
    }

}
