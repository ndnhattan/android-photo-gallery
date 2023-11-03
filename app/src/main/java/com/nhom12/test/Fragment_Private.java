package com.nhom12.test;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;
public class Fragment_Private extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    MainActivity main;
    private View view;

    Button btnCreatePass;
    Button btnEnterPass;
    Button btnForgotPass;
    EditText createPass;
    EditText confirmPass;
    EditText enterPass;
    EditText answer;
    TextInputLayout enterField;
    TextInputLayout createField;
    TextInputLayout confirmField;
    TextInputLayout answerField;
    String password;
    String info_answer;
    SharedPreferences settings;
    boolean isFirst = false;
    private LinearLayout createPassView;
    private LinearLayout enterPassView;
    public Fragment_Private()  {

    }

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
//        try {
//            main = (MainActivity) getActivity();
//        } catch (IllegalStateException e) {
//            throw new IllegalStateException("MainActivity must implement callbacks");
//        }
    }

    Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment__private, container, false);

        //
        enterPass = view.findViewById(R.id.enterPass);
        btnEnterPass = view.findViewById(R.id.btnEnterPass);
        createPass = view.findViewById(R.id.createpass);
        answer = view.findViewById(R.id.answer);


        //
        confirmPass = view.findViewById(R.id.confirmpass);
        btnCreatePass = view.findViewById(R.id.btnCreatePass);
        createPassView = view.findViewById(R.id.frag_createpass);
        enterPassView = view.findViewById(R.id.frag_enterpass);
        enterField = view.findViewById(R.id.enterField);

        //
        createField = view.findViewById(R.id.createField);
        confirmField = view.findViewById(R.id.confirmField);
        answerField = view.findViewById(R.id.answer_field);

        //
        btnForgotPass = view.findViewById(R.id.btnForgotPass);

        //
        //mToolbar = view.findViewById(R.id.toolbar_private);

        //
        settings = getActivity().getSharedPreferences("PREFS",0);
        password = settings.getString("password","");
        info_answer = settings.getString("answer","");

        if(!isFirst || password.isEmpty()) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("password", password);
            editor.putString("answer", info_answer);
            editor.apply();
            isFirst = true;
        }
        if (password.isEmpty()) {
            enterPassView.setVisibility(View.INVISIBLE);
            createPassView.setVisibility(View.VISIBLE);


        } else {
            createPassView.setVisibility(View.INVISIBLE);
            enterPassView.setVisibility(View.VISIBLE);

        }

        eventCreatePass();
        eventEnterPass();

//        mToolbar.setOnMenuItemClickListener(item -> {
//
//            int id = item.getItemId();
//
//            if (id == R.id.multi_select) {
//                Toast.makeText(getActivity(), "Choose", Toast.LENGTH_LONG).show();
//                return true;
//            } else if (id == R.id.search) {
//                Toast.makeText(getActivity(), "Search", Toast.LENGTH_LONG).show();
//                return true;
//            } else if(id == R.id.change_password){
//                Toast.makeText(getActivity(), "Change password", Toast.LENGTH_LONG).show();
//                return true;
//            } else
//                return false;
//
//        });


        return view;
    }
    public void eventCreatePass(){
        btnCreatePass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String createPassText = createPass.getText().toString();
                String confirmPassText = confirmPass.getText().toString();
                String answerQuestionText = answer.getText().toString();

                if(createPassText.isEmpty()||confirmPassText.isEmpty()){
                    createField.setError("Error");
                    confirmField.setError("Error");
                }
                if(answerQuestionText.isEmpty()){
                    answerField.setError("Error");
                }
                else{
                    if(createPassText.equals(confirmPassText)){
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("password",createPassText);
                        editor.putString("answer",answerQuestionText);
                        editor.apply();
                        createPassView.setVisibility(View.INVISIBLE);
                        enterPassView.setVisibility(View.VISIBLE);
                        updatePassword();
                    }
                    else{
                        confirmField.setError("Password doesn't match");
                    }
                }
            }
        });

    }
    public void eventEnterPass(){
        btnEnterPass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                updatePassword();
                String enterPassText = enterPass.getText().toString();
                if(enterPassText.equals(password)){
                    Toast.makeText(getActivity(),"Correct Password", Toast.LENGTH_SHORT).show();
                    accessPrivate();
                    enterPass.setText("");
                }
                else{
                    enterField.setError("Wrong password");
                }
            }
        });
        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.add(R.id.body_container, new ForgotPasswordFragment());
                fr.addToBackStack(null);
                fr.commit();
            }
        });

    }
    public void updatePassword(){
        password = settings.getString("password","");
    }

    public void accessPrivate(){
        FragmentTransaction fr = getFragmentManager().beginTransaction();
        fr.replace(R.id.body_container, new Fragment_Private_Access());
        fr.commit();
    }

}