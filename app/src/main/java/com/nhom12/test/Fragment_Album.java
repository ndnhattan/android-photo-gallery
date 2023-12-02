package com.nhom12.test;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.nhom12.test.adapter.GridAlbumAdapter;
import com.nhom12.test.adapter.ListImageAdapter;
import com.nhom12.test.adapter.SpaceItemDecoration;
import com.nhom12.test.database.AlbumDbHelper;
import com.nhom12.test.database.DatabaseSingleton;
import com.nhom12.test.structures.Album;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Album#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Album extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Variable
    MainActivity main;
    ImageButton imgBtnAll;
    ArrayList<String> nameAlbum =new ArrayList<>();
    ArrayList<Integer> iconAlbum =new ArrayList<>();
    RecyclerView myGridView;
    GridAlbumAdapter adapter;
    ArrayList<Album> albumList = new ArrayList<>();
    AlbumDbHelper albumDbHelper;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Album.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Album newInstance(String param1, String param2) {
        Fragment_Album fragment = new Fragment_Album();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        try {
            main = (MainActivity) getActivity();
            albumDbHelper = DatabaseSingleton.getInstance(main).getDbHelper();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    private void listImages() {

        String[] projection = {
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA, // Path to the image file
                MediaStore.Images.Media.DATE_ADDED
        };
        String sortOrder = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC, " + MediaStore.Images.Media.DATE_ADDED + " DESC"; // Sort by date added in descending order

        Cursor result = main.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);
        String currentAlbumName = null;
        result.moveToPosition(-1);
        // Add album favorite
        albumDbHelper.addAlbum("Favorite", -1); // id 1
        // Add album remove
        albumDbHelper.addAlbum("Remove", -1); // id 2
        // Add album private
        albumDbHelper.addAlbum("Private", -1);
        while (result.moveToNext()) {
            int bucketNameColIndex = result.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            String bucketName = result.getString(bucketNameColIndex);
            int idColIndex = result.getColumnIndex(MediaStore.Images.Media._ID);
            long id = result.getLong(idColIndex);
            int imagesDataColIndex = result.getColumnIndex(MediaStore.Images.Media.DATA);
            String imagesData = result.getString(imagesDataColIndex);
            int dateColumnIndex = result.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            String imageDate = result.getString(dateColumnIndex);

            if(currentAlbumName == null || !currentAlbumName.equals(bucketName)){
                currentAlbumName = bucketName;
                albumDbHelper.addAlbum(bucketName, id);
            }
            long albumID = albumDbHelper.getAlbumIdByAlbumName(bucketName);
            albumDbHelper.addImage(id,imagesData, imageDate);
            albumDbHelper.addAlbumImage(albumID, id);
        }
    }

    public void loadAllALbum(){
        Cursor cursor = albumDbHelper.readAllAlbum();
        Album currentAlbum;
        albumList.clear();
        cursor.moveToPosition(-1);
        while(cursor.moveToNext()){
            String imageData = albumDbHelper.getImagePathByImageId(cursor.getLong(2));
            currentAlbum = new Album(cursor.getLong(0), cursor.getString(1),imageData);
            albumList.add(currentAlbum);
        }
    }

    Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment__album, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_album);

        mToolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.add_album) {
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                        final Dialog dialog = new Dialog(main);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.layout_add_album);
                        Window window = dialog.getWindow();
                        if(window == null){
                            return false;
                        }
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                        WindowManager.LayoutParams windowAttributes= window.getAttributes();
                        windowAttributes.gravity = Gravity.CENTER;
                        window.setAttributes(windowAttributes);

                        dialog.setCancelable(true);
                        EditText edtCreateAlbum = dialog.findViewById(R.id.edt_createAlbum);
                        Button btnSubmit = dialog.findViewById(R.id.btnSubmit_createAlbum);
                        Button btnExit = dialog.findViewById(R.id.btnExit_createAlbum);

                        btnSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = edtCreateAlbum.getText().toString();
                                if(!name.equals("")){
                                    albumDbHelper.addAlbum(name, -1);
                                    long albumID = albumDbHelper.getAlbumIdByAlbumName(name);
                                    Album newAlbum = new Album(albumID, name, "");
                                    albumList.add(newAlbum);
                                    Toast.makeText(main, "Created", Toast.LENGTH_SHORT).show();
                                    adapter = new GridAlbumAdapter(main, albumList);
                                    myGridView.setAdapter(adapter);
                                    adapter.setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            // Xử lý khi một item được click
                                            Toast.makeText(main, "Name: " + albumList.get(position).getName(), Toast.LENGTH_SHORT).show();
                                            Fragment_Album_Photo fragmentPhoto = Fragment_Album_Photo.newInstance(albumList.get(position).getAlbumID(), albumList.get(position).getName());
                                            FragmentTransaction fr = getFragmentManager().beginTransaction();
                                            fr.replace(R.id.body_container, fragmentPhoto);
                                            fr.commit();
                                        }
                                    });
                                }
                                dialog.dismiss();
                            }
                        });

                        btnExit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        return true;
                    }
                });
                return true;
            } else if (id == R.id.search) {
                Toast.makeText(getActivity(), "Search", Toast.LENGTH_LONG).show();
                return true;
            } else
                return false;

        });

        listImages();
        loadAllALbum();
        myGridView = ((RecyclerView) rootView.findViewById(R.id.layout_grid_album));
        adapter = new GridAlbumAdapter(main, albumList);
        myGridView.setAdapter(adapter);
        myGridView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        myGridView.addItemDecoration(new SpaceItemDecoration(12));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Xử lý khi một item được click
                Fragment_Album_Photo fragmentPhoto = Fragment_Album_Photo.newInstance(albumList.get(position).getAlbumID(), albumList.get(position).getName()); //fragment hien thi danh sach cac anh theo ten album
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.body_container, fragmentPhoto);
                fr.commit();
            }
        });

        return rootView;
    }


}