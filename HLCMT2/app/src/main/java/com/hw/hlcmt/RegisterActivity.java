package com.hw.hlcmt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hw.hlcmt.JavaRepositories.MyProgressDialog;
import com.hw.hlcmt.JavaRepositories.UserModel;
import com.hw.hlcmt.JavaRepositories.UserType;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Button reg = findViewById(R.id.btnRegister);
        TextView login = findViewById(R.id.tvLogin);

        reg.setOnClickListener(this);
        login.setOnClickListener(this);

        /*final ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setMessage("Checking user status");
        progressBar.show();
        if(fbAuth.getCurrentUser() != null){
            startActivity(new Intent(this, HomeActivity.class));
        }
        progressBar.hide();*/
    }

    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.btnRegister)){
            registerUser();
        }
        if (v == findViewById(R.id.tvLogin)){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void registerUser() {
        EditText editTextName = findViewById(R.id.txtRegUsername),
                 editTextEmail = findViewById(R.id.txtRegUserEmail),
                 editTextPassword = findViewById(R.id.txtRegPassword);

        final String uname = editTextName.getText().toString().trim();
        final String uemail = editTextEmail.getText().toString().trim();
        String upass = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(uname)) {
            Toast.makeText(RegisterActivity.this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            editTextName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(uemail)) {
            Toast.makeText(RegisterActivity.this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            Toast.makeText(RegisterActivity.this, "Email is invalid!", Toast.LENGTH_SHORT).show();
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(upass)) {
            Toast.makeText(RegisterActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            editTextPassword.requestFocus();
            return;
        }

        if (upass.length() < 8) {
            Toast.makeText(RegisterActivity.this, "Password should be atleast 8 characters long!", Toast.LENGTH_SHORT).show();
            editTextPassword.requestFocus();
            return;
        }




/**
 *
 *
 */
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        try{
            fbAuth.createUserWithEmailAndPassword(uemail, upass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                                UserModel user = new UserModel(fbAuth.getUid(), uname, uemail, UserType.NORMAL);

                                FirebaseFirestore ff = FirebaseFirestore.getInstance();

                            ff.collection("User").document(user.getUserId()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Registration Successful. Follow link in uemail to verify your uemail address!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(RegisterActivity.this, EmailVerifyActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Registration Failed.\n\nError - " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e){
            Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }
}
