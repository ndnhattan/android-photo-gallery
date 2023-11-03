package com.nhom12.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ForgotPasswordFragment extends Fragment {
    private View view;
    private Button btnConfirm;
    private Button btnChangePass;
    EditText answerEdt;
    EditText newPass;
    EditText rePass;
    TextView questionText;
    String password;
    String answer;
    SharedPreferences settings;
    LinearLayout answerQuestionView;
    LinearLayout changePassVIew;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forgot_password, container,false);
        settings = getActivity().getSharedPreferences("PREFS",0);
        password = settings.getString("password","");
        answer = settings.getString("answer","");

        getParentFragmentManager().beginTransaction().add(this,"frag_forgot_password");
        if(password.equals("")||answer.equals("")){
            Toast.makeText(getActivity(),"Failed setting, can't get back password", Toast.LENGTH_SHORT).show();
            Fragment thisFrag = getParentFragmentManager().findFragmentByTag("frag_forgot_password");
            getParentFragmentManager().beginTransaction()
                    .remove(thisFrag)
                    .commit();
        }
        mapping();
        changePassVIew.setVisibility(View.INVISIBLE);
        event();
        return view;
    }

    public void event(){
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answerText = answerEdt.getText().toString();
                if(!answerText.equals(answer)){
                    Toast.makeText(getActivity(),"Wrong answer", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getActivity(),"Correct", Toast.LENGTH_SHORT).show();
                answerQuestionView.setVisibility(View.INVISIBLE);
                changePassVIew.setVisibility(View.VISIBLE);
            }
        });
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassText = newPass.getText().toString();
                String rePassText = rePass.getText().toString();
                if(!newPassText.equals(rePassText)){
                    Toast.makeText(getActivity(),"Password doesn't match", Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("password",newPassText);
                    editor.apply();
                    Toast.makeText(getActivity(),"Password has been changed successfully", Toast.LENGTH_SHORT).show();
                    Fragment thisFrag = getParentFragmentManager().findFragmentByTag("frag_forgot_password");
                    getParentFragmentManager().beginTransaction()
                            .remove(thisFrag)
                            .commit();
                }
            }
        });
    }
    public void mapping(){
        answerQuestionView = view.findViewById(R.id.answer_view);
        changePassVIew = view.findViewById(R.id.changepass_view);
        questionText = view.findViewById(R.id.dialog_question);
        answerEdt = view.findViewById(R.id.dialog_editAnswer);
        btnConfirm = view.findViewById(R.id.btnForgot);

        newPass = view.findViewById(R.id.edtNewpass);
        rePass = view.findViewById(R.id.edtRepass);
        btnChangePass = view.findViewById(R.id.btnChangepass);
    }
}