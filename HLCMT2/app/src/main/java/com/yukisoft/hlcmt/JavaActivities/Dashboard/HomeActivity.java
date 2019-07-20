package com.yukisoft.hlcmt.JavaActivities.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaActivities.Admin.AdminActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.AudioMSGActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.HelpActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.MeditationTimesActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.PrayerReqActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.ProfileActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.SettingsActivity;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

public class HomeActivity extends AppCompatActivity {
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        if(currentUser == null)
            Toast.makeText(this, "Error loading user!", Toast.LENGTH_SHORT).show();

        ImageView btnAdmin = findViewById(R.id.btnDBAdmin);
        if (!currentUser.isAdmin()) btnAdmin.setVisibility(View.INVISIBLE);
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, AdminActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser));
                startActivity(i);
                finish();
            }
        });


        /**
         *
         * DASHBOARD
         *
         */
        LinearLayout mt = findViewById(R.id.btnDBMtMSG);
        mt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", v.toString());
                startActivity(new Intent(HomeActivity.this, MeditationTimesActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                finish();
            }
        });

        LinearLayout audio = findViewById(R.id.btnDBAudioMSG);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", v.toString());
                startActivity(new Intent(HomeActivity.this, AudioMSGActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                finish();
            }
        });

        LinearLayout prayer = findViewById(R.id.btnDBPrayerReq);
        prayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", v.toString());
                startActivity(new Intent(HomeActivity.this, PrayerReqActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                finish();
            }
        });

        LinearLayout settings = findViewById(R.id.btnDBAppSettings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", v.toString());
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                finish();
            }
        });

        LinearLayout profile = findViewById(R.id.btnDBProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", v.toString());
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                finish();
            }
        });

        LinearLayout help = findViewById(R.id.btnDBHelp);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", v.toString());
                startActivity(new Intent(HomeActivity.this, HelpActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                finish();
            }
        });
    }
}
