package com.example.myapplication.customAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerView extends RecyclerView {
    // The size of the scroll bar thumb in our units.
    private int mThumbHeight = UNDEFINED;

    // Where the RecyclerView cuts off the views when the RecyclerView is scrolled to top.
    // For example, if 1/4 of the view at position 9 is displayed at the bottom of the RecyclerView,
    // mTopCutOff will equal 9.25. This value is used to compute the scroll offset.
    private float mTopCutoff = UNDEFINED;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Retrieves the size of the scroll bar thumb in our arbitrary units.
     *
     * @return Scroll bar thumb height
     */
    @Override
    public int computeVerticalScrollExtent() {
        return (mThumbHeight == UNDEFINED) ? 0 : mThumbHeight;
    }

    /**
     * Compute the offset of the scroll bar thumb in our scroll bar range.
     *
     * @return Offset in scroll bar range.
     */
    @Override
    public int computeVerticalScrollOffset() {
        return (mTopCutoff == UNDEFINED) ? 0 : (int) ((getCutoff() - mTopCutoff) * ITEM_HEIGHT);
    }

    /**
     * Computes the scroll bar range. It will simply be the number of items in the adapter
     * multiplied by the given item height. The scroll extent size is also computed since it
     * will not vary. Note: The RecyclerView must be positioned at the top or this method
     * will throw an IllegalStateException.
     *
     * @return The scroll bar range
     */
    @Override
    public int computeVerticalScrollRange() {
        if (mThumbHeight == UNDEFINED) {
            LinearLayoutManager lm = (LinearLayoutManager) getLayoutManager();
            int firstCompletePositionw = lm.findFirstCompletelyVisibleItemPosition();

            if (firstCompletePositionw != RecyclerView.NO_POSITION) {
                if (firstCompletePositionw != 0) {
                    throw (new IllegalStateException(ERROR_NOT_AT_TOP_OF_RANGE));
                } else {
                    mTopCutoff = getCutoff();
                    mThumbHeight = (int) (mTopCutoff * ITEM_HEIGHT);
                }
            }
        }
        return getAdapter().getItemCount() * ITEM_HEIGHT;
    }

    /**
     * Determine where the RecyclerVIew display cuts off the list of views. The range is
     * zero through (getAdapter().getItemCount() - 1) inclusive.
     *
     * @return The position in the RecyclerView where the displayed views are cut off. If the
     * bottom view is partially displayed, this will be a fractional number.
     */
    private float getCutoff() {
        LinearLayoutManager lm = (LinearLayoutManager) getLayoutManager();
        int lastVisibleItemPosition = lm.findLastVisibleItemPosition();

        if (lastVisibleItemPosition == RecyclerView.NO_POSITION) {
            return 0f;
        }

        View view = lm.findViewByPosition(lastVisibleItemPosition);
        float fractionOfView;

        if (view.getBottom() < getHeight()) { // last visible position is fully visible
            fractionOfView = 0f;
        } else { // last view is cut off and partially displayed
            fractionOfView = (float) (getHeight() - view.getTop()) / (float) view.getHeight();
        }
        return lastVisibleItemPosition + fractionOfView;
    }

    private static final int ITEM_HEIGHT = 1000; // Arbitrary, make largish for smoother scrolling
    private static final int UNDEFINED = -1;
    private static final String ERROR_NOT_AT_TOP_OF_RANGE
            = "RecyclerView must be positioned at the top of its range.";
}
