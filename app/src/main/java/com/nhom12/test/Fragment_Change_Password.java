package com.nhom12.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

public class Fragment_Change_Password extends Fragment {
    private View view;
    private Button btnConfirm;
    private Button btnCancel;
    EditText currentPass;
    EditText newPass;
    EditText retypePass;
    String password;
    SharedPreferences settings;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment__change__password, container,false);
        settings = getActivity().getSharedPreferences("PREFS",0);
        password = settings.getString("password","");
        getParentFragmentManager().beginTransaction().add(this,"frag_change_password");
        if(password.equals("")){
            Toast.makeText(getActivity(),"Password has not found", Toast.LENGTH_SHORT).show();
            Fragment thisFrag = getParentFragmentManager().findFragmentByTag("frag_change_password");
            getParentFragmentManager().beginTransaction().remove(thisFrag).commit();
        }
        mapping();
        eventClick();
        return view;
    }

    public void eventClick(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                Fragment thisFrag = getParentFragmentManager().findFragmentByTag("frag_change_password");
                getParentFragmentManager().beginTransaction()
                        .remove(thisFrag)
                        .commit();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassText = currentPass.getText().toString();
                String newPassText = newPass.getText().toString();
                String retypePassText = retypePass.getText().toString();
                if(!currentPassText.equals(password)){
                    Toast.makeText(getActivity(),"Wrong password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!newPassText.equals(retypePassText)){
                    Toast.makeText(getActivity(),"Password doesn't match", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("password",newPassText);
                editor.apply();
                Toast.makeText(getActivity(),"Password has been changed successfully", Toast.LENGTH_SHORT).show();
                Fragment thisFrag = getParentFragmentManager().findFragmentByTag("frag_change_password");
                getParentFragmentManager().beginTransaction()
                        .remove(thisFrag)
                        .commit();

            }
        });
    }
    public void mapping(){
        currentPass = view.findViewById(R.id.current_pass);
        newPass = view.findViewById(R.id.new_pass);
        retypePass = view.findViewById(R.id.retype_pass);
        btnConfirm = view.findViewById(R.id.btnChangePass);
        btnCancel = view.findViewById(R.id.btnCancel);
    }
}
