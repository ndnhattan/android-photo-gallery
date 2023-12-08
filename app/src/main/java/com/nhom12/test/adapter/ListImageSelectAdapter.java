package com.nhom12.test.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nhom12.test.R;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ListImageSelectAdapter extends RecyclerView.Adapter<ListImageSelectAdapter.ViewHolder> {
    private ArrayList<Cursor> rs;
    private Context context;

    public ListImageSelectAdapter(Context context, ArrayList<Cursor> rs) {
        this.context = context;
        this.rs = rs;
    }

    public void setData(ArrayList<Cursor> rs) {
        this.rs = rs;
        notifyDataSetChanged();
    }

    @Override
    public ListImageSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_list_image, parent, false);

        ListImageSelectAdapter.ViewHolder viewHolder = new ListImageSelectAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListImageSelectAdapter.ViewHolder holder, int position) {
        Cursor result = rs.get(position);

        result.moveToFirst();
        String imageDate = result.getString(2);
        Timestamp tms = new Timestamp(Long.parseLong(imageDate) * 1000);
        Date date = new Date(tms.getTime());
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDate();

        holder.textView.setText(String.valueOf(day) + " tháng " + String.valueOf(month + 1) + ", năm " + String.valueOf(year + 1900));
        holder.recyclerView.setAdapter(new GridImageSelectAdapter(context, result, position));
        holder.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        //holder.recyclerView.addItemDecoration(new SpaceItemDecoration(12));
    }

    @Override
    public int getItemCount() {
        return rs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
}