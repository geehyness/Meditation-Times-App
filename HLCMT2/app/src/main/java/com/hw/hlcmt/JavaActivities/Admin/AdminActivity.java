package com.hw.hlcmt.JavaActivities.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.hw.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.hw.hlcmt.JavaRepositories.Models.UserModel;
import com.hw.hlcmt.MainActivity;
import com.hw.hlcmt.R;

public class AdminActivity extends AppCompatActivity {
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Intent i = getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
        finish();
    }
}
