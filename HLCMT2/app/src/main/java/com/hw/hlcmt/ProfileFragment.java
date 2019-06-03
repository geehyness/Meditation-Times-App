package com.hw.hlcmt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaRepositories.CollectionName;
import com.hw.hlcmt.JavaRepositories.MyProgressDialog;
import com.hw.hlcmt.JavaRepositories.UserModel;

public class ProfileFragment extends Fragment {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private EditText username = null;
    private EditText email = null;
    private EditText password = null;

    private UserModel currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**
         *  UI VARIABLES
         */
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        username = v.findViewById(R.id.txtProfileName);
        email = v.findViewById(R.id.txtProfileEmail);
        password = v.findViewById(R.id.txtProfilePassword);

        username.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);

        /**
         *  GETTING CURRENT USER'S INFO
         */

        Intent i = getActivity().getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        username.setText(currentUser.getName());
        email.setText(currentUser.getEmail());
        password.setText("12345678");



        //progressDialog.dismiss();

        /**
         *  BUTTON FUNCTIONALITY
         */
        Button btnUpdate = v.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Action unavailable as of yet", Toast.LENGTH_SHORT).show();
                //update();
            }
        });
        TextView delete = v.findViewById(R.id.tvDeleteAccount);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DeleteAccountActivity.class);
                String userJSON = (new Gson()).toJson(currentUser);
                i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
                startActivity(i);
                getActivity().finish();
            }
        });
        TextView editEmail = v.findViewById(R.id.tvEditEmail);
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Action unavailable as of yet", Toast.LENGTH_SHORT).show();
                return;
            }
        });
        TextView editPass = v.findViewById(R.id.tvEditPass);
        editPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Action unavailable as of yet", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        return v;
    }

    private void update(){
        if (username != null && password != null && email != null){
            String uname = username.getText().toString().trim();
            String uemail = email.getText().toString().trim();
            String upass = password.getText().toString().trim();

            if (TextUtils.isEmpty(uname)) {
                Toast.makeText(getContext(), "Username cannot be empty!", Toast.LENGTH_SHORT).show();
                username.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(uemail)) {
                Toast.makeText(getContext(), "Email cannot be empty!", Toast.LENGTH_SHORT).show();
                email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
                Toast.makeText(getContext(), "Email is invalid!", Toast.LENGTH_SHORT).show();
                email.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(upass)) {
                Toast.makeText(getContext(), "Password cannot be empty!", Toast.LENGTH_SHORT).show();
                email.requestFocus();
                return;
            }

            if (upass.length() < 8) {
                Toast.makeText(getContext(), "Password should be atleast 8 characters long!", Toast.LENGTH_SHORT).show();
                password.requestFocus();
                return;
            }

            Toast.makeText(getContext(), "Update email and username to be implemented!", Toast.LENGTH_SHORT).show();
        }
    }
}
