package com.hw.hlcmt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hw.hlcmt.JavaRepositories.MyProgressDialog;
import com.hw.hlcmt.JavaRepositories.UserModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Button login = findViewById(R.id.btnLogin);
        TextView reg = findViewById(R.id.tvRegister);

        login.setOnClickListener(this);
        reg.setOnClickListener(this);

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
        if (v == findViewById(R.id.btnLogin)){
            login();
        }
        if (v == findViewById(R.id.tvRegister)){
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit HLC-MT")
                .setMessage("Are you sure you want to close the application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void login(){
        final UserModel[] currUser = {null};
        EditText username = findViewById(R.id.txtUsername),
                 password = findViewById(R.id.txtPassword);

        final String uemail = username.getText().toString().trim();
        String upass = password.getText().toString().trim();

        // Validating input
        if (TextUtils.isEmpty(uemail)) {
            Toast.makeText(LoginActivity.this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
            username.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            Toast.makeText(LoginActivity.this, "Email is invalid!", Toast.LENGTH_SHORT).show();
            username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(upass)) {
            Toast.makeText(LoginActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return;
        }

        if (upass.length() < 8) {
            Toast.makeText(LoginActivity.this, "Password should be atleast 8 characters long!", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return;
        }















        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        try{
            fbAuth.signInWithEmailAndPassword(uemail, upass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                //UserModel userModel = new UserModel(uname, uemail, "normal");

                                FirebaseFirestore ff = FirebaseFirestore.getInstance();
                                final String loginId = fbAuth.getUid();

                                CollectionReference userRef = ff.collection("User");
                                userRef.get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                progressDialog.dismiss();
                                                for (QueryDocumentSnapshot userSnapshot : queryDocumentSnapshots) {
                                                    UserModel user = userSnapshot.toObject(UserModel.class);
                                                    user.setUserIdDoc(userSnapshot.getId());

                                                    /*if(user.getUserId() == loginId){
                                                        currUser[0] = user;
                                                        return;
                                                    }*/

                                                    if (user.getUserId().equals(loginId)){
                                                        Toast.makeText(LoginActivity.this, "Welcome back " + user.getName(), Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                        LoginActivity.this.finish();
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, fbAuth.getUid(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(LoginActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Failed. Try again later!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e){
            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }











        /*ProgressDialog progressBar = new ProgressDialog(LoginActivity.this);
        progressBar.setMessage("Logging in");
        progressBar.show();

        //Login
        fbAuth.signInWithEmailAndPassword(uemail, upass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseFirestore ff = FirebaseFirestore.getInstance();
                CollectionReference userRef = ff.collection("User");
                userRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot user : queryDocumentSnapshots){
                            UserModel u = user.toObject(UserModel.class);
                            if(u.email == uemail){
                                Toast.makeText(LoginActivity.this, "Welcome back " + u.name, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                LoginActivity.this.finish();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        progressBar.hide();*/
    }
}
