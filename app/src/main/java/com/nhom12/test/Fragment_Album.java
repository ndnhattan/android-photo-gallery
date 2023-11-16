package com.nhom12.test;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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
    ArrayList<String> nameAlbum =new ArrayList<>(Arrays.asList("Picture Camera", "Favorite", "Private", "Remove"));
    ArrayList<Integer> iconAlbum =new ArrayList<>(Arrays.asList(R.drawable.icon_all_album, R.drawable.icon_favorive_album, R.drawable.icon_pri_album, R.drawable.icon_remove_album));
    GridView myGridView;
    GridAlbumAdapter adapter;

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
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
        //main = (MainActivity) getActivity();
    }

    Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment__album, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_album);
        myGridView = ((GridView) rootView.findViewById(R.id.layout_grid_album));

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
                                nameAlbum.add(name);
                                iconAlbum.add(R.drawable.icon_all_album);
                                Toast.makeText(main, "Created", Toast.LENGTH_SHORT).show();
                                adapter = new GridAlbumAdapter(main, R.layout.item_list_album, nameAlbum, iconAlbum);
                                myGridView.setAdapter(adapter);
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



        adapter = new GridAlbumAdapter(main, R.layout.item_list_album, nameAlbum, iconAlbum);
        myGridView.setAdapter(adapter);
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                main.onMsgFromFragToMain("ALBUM", null);
            }
        });

        return rootView;
    }


}