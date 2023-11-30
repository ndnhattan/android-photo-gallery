package com.nhom12.test;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.nhom12.test.adapter.ListImageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Favorite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Favorite extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Favorite() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Love.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Favorite newInstance(String param1, String param2) {
        Fragment_Favorite fragment = new Fragment_Favorite();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //MainActivity main;
    //RecyclerView recyclerView;
    Button favor;


    public static Fragment_Favorite newInstance(String strArg) {
        Fragment_Favorite fragment = new Fragment_Favorite();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //main = (MainActivity) getActivity();
//        try {
//            main = (MainActivity) getActivity();
//        } catch (IllegalStateException e) {
//            throw new IllegalStateException("MainActivity must implement callbacks");
//        }
    }

    Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment__favorite, container, false);
        favor = (Button) rootView.findViewById(R.id.button_favor);
        //recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_Favor);
        favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                List<String> imgPathList = DataLocalManager.getListImg();
//
//                // Kiểm tra xem danh sách có ít nhất một phần tử không
//                if (!imgPathList.isEmpty()) {
//                    // Lấy phần tử thứ nhất từ danh sách
//                    String firstImagePath = imgPathList.get(0);
//
//                    // Tạo chuỗi để hiển thị trong Toast
//                    String toastMessage = "Phần tử thứ nhất trong danh sách: " + firstImagePath;
//
//                    // Hiển thị Toast
//                    Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
//                } else {
//                    // Hiển thị thông báo nếu danh sách rỗng
//                    Toast.makeText(view.getContext(), "Danh sách ảnh trống", Toast.LENGTH_SHORT).show();
//                }
            }

        });

//        recyclerView.setAdapter(new ListImageAdapter(main, rs));
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(main);
//        recyclerView.setLayoutManager(linearLayoutManager);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_favorite);

        mToolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.multi_select) {
                Toast.makeText(getActivity(), "Choose", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.search) {
                Toast.makeText(getActivity(), "Search", Toast.LENGTH_LONG).show();
                return true;
            } else
                return false;

        });



        return rootView;
    }
}