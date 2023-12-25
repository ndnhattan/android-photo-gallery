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
import com.nhom12.test.activities.DetailRemovePhotoActivity;
import com.nhom12.test.activities.SelectActivity;
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
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    // TODO: Rename and change types of parameters
    private long imageId;
    private long albumId;
    boolean isDetailRemoveActivity = false;
    boolean isSelectMultiple = false;

    // Variable
    DetailPhotoActivity mainDetail;
    DetailRemovePhotoActivity mainDetailRemove;
    SelectActivity mainDetailSelect;
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
    public static Fragment_Album_Choose newInstance(long imageId, long albumId, boolean isDetailRemoveActivity, boolean isSelectMultiple) {
        Fragment_Album_Choose fragment = new Fragment_Album_Choose();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, imageId);
        args.putLong(ARG_PARAM2, albumId);
        args.putBoolean(ARG_PARAM3, isDetailRemoveActivity);
        args.putBoolean(ARG_PARAM4, isSelectMultiple);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageId = getArguments().getLong(ARG_PARAM1);
            albumId = getArguments().getLong(ARG_PARAM2);
            isDetailRemoveActivity = getArguments().getBoolean(ARG_PARAM3);
            isSelectMultiple = getArguments().getBoolean(ARG_PARAM4);
        }
        setHasOptionsMenu(true);
        try {
            if(isDetailRemoveActivity){
                mainDetailRemove = (DetailRemovePhotoActivity)getActivity();
                albumDbHelper = DatabaseSingleton.getInstance(mainDetailRemove).getDbHelper();

            } else if(isSelectMultiple){
                mainDetailSelect = (SelectActivity) getActivity();
                albumDbHelper = DatabaseSingleton.getInstance(mainDetailSelect).getDbHelper();
            } else {
                mainDetail = (DetailPhotoActivity) getActivity();
                albumDbHelper = DatabaseSingleton.getInstance(mainDetail).getDbHelper();
            }
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
        if(isDetailRemoveActivity){
            adapter = new GridAlbumAdapter(mainDetailRemove, albumList);
        } else if (isSelectMultiple){
          adapter = new GridAlbumAdapter(mainDetailSelect, albumList);
        } else {
            adapter = new GridAlbumAdapter(mainDetail, albumList);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new SpaceItemDecoration(12));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Album album) {
                // Xử lý khi một item được click

                if(isSelectMultiple){
                    for(int i=0; i<SelectActivity.checkedArr.size(); i++){
                        SelectActivity.result.moveToPosition(SelectActivity.checkedArr.get(i));
                        imageId = SelectActivity.result.getLong(0);
                        albumId = albumDbHelper.getAlbumIdByImageId(imageId);
                        albumDbHelper.moveImageToAlbum(imageId, albumId, album.getAlbumID());

                        long firstImageIDAlbumCurrent = albumDbHelper.findFirstImageIDAlbum(albumId);
                        albumDbHelper.updateAlbumFirstImage(albumId, firstImageIDAlbumCurrent);
                    }
                    long firstImageIDAlbumNew = albumDbHelper.findFirstImageIDAlbum(album.getAlbumID());
                    albumDbHelper.updateAlbumFirstImage(album.getAlbumID(), firstImageIDAlbumNew);

                } else {
                    albumDbHelper.moveImageToAlbum(imageId, albumId, album.getAlbumID());
                    long firstImageIDAlbumCurrent = albumDbHelper.findFirstImageIDAlbum(albumId);
                    long firstImageIDAlbumNew = albumDbHelper.findFirstImageIDAlbum(album.getAlbumID());
                    albumDbHelper.updateAlbumFirstImage(albumId, firstImageIDAlbumCurrent);
                    albumDbHelper.updateAlbumFirstImage(album.getAlbumID(), firstImageIDAlbumNew);
                }

                if(isDetailRemoveActivity){
                    Toast.makeText(mainDetailRemove, "Restore Successfull", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else if (isSelectMultiple){
                    Toast.makeText(mainDetailSelect, "Successfull", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(mainDetail, "Successfull", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
//                    mainDetail.onBackPressedExit();
                }
            }
        });


        return  rootView;
    }

    public void loadAllALbum(){
        Cursor cursor = albumDbHelper.readAllAlbum();
        Album currentAlbum;
        albumList.clear();
        cursor.moveToPosition(-1);
        while(cursor.moveToNext()){
            String imageData = albumDbHelper.getImagePathByImageId(cursor.getLong(2));
            currentAlbum = new Album(cursor.getLong(0), cursor.getString(1), imageData);
            albumList.add(currentAlbum);
        }
    }
}