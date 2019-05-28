package com.hw.hlcmt;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hw.hlcmt.JavaRepositories.MyProgressDialog;
import com.hw.hlcmt.JavaRepositories.UserModel;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private EditText username = null;
    private EditText email = null;
    private EditText password = null;

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



        /**
         *  GETTING CURRENT USER'S INFO
         */
        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        try{
            FirebaseFirestore ff = FirebaseFirestore.getInstance();
            final String loginId = fbAuth.getUid();

            DocumentReference user = ff.document("User/"+loginId);
            user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    progressDialog.dismiss();
                    UserModel userModel = documentSnapshot.toObject(UserModel.class);

                    username.setText(userModel.getName());
                    email.setText(userModel.getEmail());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    fbAuth.signOut();
                    Toast.makeText(getContext(), "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Try logging out then back in!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();



        /**
         *  BUTTON FUNCTIONALITY
         */
        Button btnUpdate = v.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        if (v.equals(R.id.btnUpdate)){
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
}
