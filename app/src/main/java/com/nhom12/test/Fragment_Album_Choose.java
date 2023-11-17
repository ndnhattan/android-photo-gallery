package com.nhom12.test;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nhom12.test.activities.DetailPhotoActivity;
import com.nhom12.test.adapter.GridAlbumAdapter;
import com.nhom12.test.adapter.SpaceItemDecoration;
import com.nhom12.test.database.AlbumDbHelper;
import com.nhom12.test.database.DatabaseSingleton;
import com.nhom12.test.structures.Album;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Album_Choose#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Album_Choose extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private long imageId;


    // Variable
    DetailPhotoActivity mainDetail;
    ArrayList<Album> albumList = new ArrayList<>();
    AlbumDbHelper albumDbHelper;
    RecyclerView recyclerView;
    GridAlbumAdapter adapter;

    public Fragment_Album_Choose() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Fragment_Album_Choose.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Album_Choose newInstance(long imageId) {
        Fragment_Album_Choose fragment = new Fragment_Album_Choose();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, imageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageId = getArguments().getLong(ARG_PARAM1);
        }
        setHasOptionsMenu(true);
        try {
            mainDetail = (DetailPhotoActivity) getActivity();
            albumDbHelper = DatabaseSingleton.getInstance(mainDetail).getDbHelper();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment__album__choose, container, false);
        loadAllALbum();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.layout_grid_album);
        adapter = new GridAlbumAdapter(mainDetail, albumList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new SpaceItemDecoration(12));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Xử lý khi một item được click
                albumDbHelper.moveImageToAlbum(imageId, albumList.get(position).getName());
                Toast.makeText(mainDetail, "Move Successfull", Toast.LENGTH_SHORT).show();
                mainDetail.onBackPressedExit();
            }
        });


        return  rootView;
    }

    public void loadAllALbum(){
        Cursor cursor = albumDbHelper.readAllAlbum();
        Album currenAlbum;
        albumList.clear();
        cursor.moveToPosition(-1);
        while(cursor.moveToNext()){
            currenAlbum = new Album(cursor.getString(0));
            currenAlbum.addFirstImagesData(cursor.getString(1));
            albumList.add(currenAlbum);
        }
    }
}