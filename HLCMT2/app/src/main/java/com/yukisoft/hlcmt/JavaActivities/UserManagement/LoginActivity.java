package com.yukisoft.hlcmt.JavaActivities.UserManagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.UIElements.MyProgressDialog;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

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
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseFirestore ff = FirebaseFirestore.getInstance();
                        final String loginId = fbAuth.getUid();

                        DocumentReference user = ff.document(CollectionName.User+"/"+loginId);
                        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                progressDialog.dismiss();
                                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                Toast.makeText(LoginActivity.this, "Welcome back " + userModel.getName(), Toast.LENGTH_SHORT).show();

                                String userJSON = (new Gson()).toJson(userModel);
                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
                                startActivity(i);
                                LoginActivity.this.finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                fbAuth.signOut();
                                Toast.makeText(LoginActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e){
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
