package com.nhom12.test.activities.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        int layoutId = getLayoutId();
        ////////////   NOTE
        /// == or !=

        if (layoutId != 0) {
            throw new IllegalStateException("Invalid layout id");
        }
        return inflater.inflate(layoutId, container, false);
    }
}