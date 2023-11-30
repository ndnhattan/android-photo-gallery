package com.nhom12.test.activities.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom12.test.R;

import java.util.ArrayList;
import java.util.List;

public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {
    private final OnItemSelected mOnItemSelected;
    private final List<ToolModel> mToolList = new ArrayList<>();

    public EditingToolsAdapter(OnItemSelected onItemSelected) {
        this.mOnItemSelected = onItemSelected;

        mToolList.add(new ToolModel("Brush", R.drawable.ic_brush, ToolType.BRUSH));
        mToolList.add(new ToolModel("Text", R.drawable.ic_text, ToolType.TEXT));
        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER));
        mToolList.add(new ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI));
        mToolList.add(new ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER));
        mToolList.add(new ToolModel("Crop", R.drawable.icon_crop, ToolType.CROP));
    }

    public interface OnItemSelected {
        void onToolSelected(ToolType toolType);
    }

    public static class ToolModel {
        public final String mToolName;
        public final int mToolIcon;
        public final ToolType mToolType;

        public ToolModel(String toolName, int toolIcon, ToolType toolType) {
            this.mToolName = toolName;
            this.mToolIcon = toolIcon;
            this.mToolType = toolType;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_editing_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.txtTool.setText(item.mToolName);
        holder.imgToolIcon.setImageResource(item.mToolIcon);
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imgToolIcon;
        public final TextView txtTool;

        public ViewHolder(View itemView) {
            super(itemView);
            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            txtTool = itemView.findViewById(R.id.txtTool);

            itemView.setOnClickListener(v ->
                    mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).mToolType));
        }
    }
}