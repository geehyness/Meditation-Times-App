package com.hw.hlcmt.JavaActivities.Dashboard.Items;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.hw.hlcmt.JavaActivities.UserManagement.LoginActivity;
import com.hw.hlcmt.JavaRepositories.Models.UserModel;
import com.hw.hlcmt.MainActivity;
import com.hw.hlcmt.R;
import com.hw.hlcmt.JavaActivities.UserManagement.DeleteAccountActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText username = null;
    private EditText email = null;
    private EditText password = null;
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        username = findViewById(R.id.txtProfileName);
        email = findViewById(R.id.txtProfileEmail);
        password = findViewById(R.id.txtProfilePassword);
        username.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);

        Button btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Action unavailable as of yet", Toast.LENGTH_SHORT).show();
                //update();
            }
        });

        TextView logout = findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth fbAuth = FirebaseAuth.getInstance();
                fbAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });

        TextView delete = findViewById(R.id.tvDeleteAccount);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, DeleteAccountActivity.class);
                String userJSON = (new Gson()).toJson(currentUser);
                i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
                startActivity(i);
                finish();
            }
        });
        TextView editEmail = findViewById(R.id.tvEditEmail);
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Action unavailable as of yet", Toast.LENGTH_SHORT).show();
                return;
            }
        });
        TextView editPass = findViewById(R.id.tvEditPass);
        editPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Action unavailable as of yet", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        setSettings();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
        finish();
    }

    private void setSettings(){
        username.setText(currentUser.getName());
        email.setText(currentUser.getEmail());
        password.setText("12345678");
    }

    private void update(){
        if (username != null && password != null && email != null){
            String uname = username.getText().toString().trim();
            String uemail = email.getText().toString().trim();
            String upass = password.getText().toString().trim();

            if (TextUtils.isEmpty(uname)) {
                Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
                username.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(uemail)) {
                Toast.makeText(this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
                email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
                Toast.makeText(this, "Email is invalid!", Toast.LENGTH_SHORT).show();
                email.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(upass)) {
                Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
                email.requestFocus();
                return;
            }

            if (upass.length() < 8) {
                Toast.makeText(this, "Password should be atleast 8 characters long!", Toast.LENGTH_SHORT).show();
                password.requestFocus();
                return;
            }

            Toast.makeText(this, "Update email and username to be implemented!", Toast.LENGTH_SHORT).show();
        }
    }
}
