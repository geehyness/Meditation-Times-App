package com.hw.hlcmt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.hw.hlcmt.JavaRepositories.UserModel;

public class DeleteAccountActivity extends AppCompatActivity {
    private UserModel currentUser;
    private String userJSON;
    private String userConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        Intent i = getIntent();
        userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DeleteAccountActivity.this, HomeActivity.class);
        i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
        startActivity(i);
        finish();
    }
}
