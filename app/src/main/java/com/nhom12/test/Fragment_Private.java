package com.nhom12.test;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Private#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Private extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Private() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Private.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Private newInstance(String param1, String param2) {
        Fragment_Private fragment = new Fragment_Private();
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

        //main = (MainActivity) getActivity();
    }

    Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment__private, container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_private);

        mToolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.multi_select) {
                Toast.makeText(getActivity(), "Choose", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.search) {
                Toast.makeText(getActivity(), "Search", Toast.LENGTH_LONG).show();
                return true;
            } else if(id == R.id.change_password){
                Toast.makeText(getActivity(), "Change password", Toast.LENGTH_LONG).show();
                return true;
            } else
                return false;

        });



        return rootView;
    }
}