package com.hw.hlcmt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaRepositories.UserModel;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private DrawerLayout drawer;
    private UserModel currentUser;

    private TextView nameTV;
    private TextView emailTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MeditationFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_message);
        }

        nameTV = findViewById(R.id.tvNavUname);
        emailTV = findViewById(R.id.tvNavEmail);

        //nameTV.setText(currentUser.getName());
        //emailTV.setText(currentUser.getEmail());
    }

    @Override
    public void onClick(View v) {
        /*if(v == findViewById(R.id.btnLogout)){
            fbAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }*/
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_message:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MeditationFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_info:
                startActivity(new Intent(this, InformationActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                finish();
                break;
            case R.id.nav_logout:
                fbAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
