package com.yukisoft.hlcmt.JavaActivities.Dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Fragments.FragmentAudio;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Fragments.FragmentDiscussions;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Fragments.FragmentMT;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Fragments.FragmentNews;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Fragments.FragmentProfile;
import com.yukisoft.hlcmt.R;

public class BNV_HomeActivity extends AppCompatActivity {

    Fragment mt = null,
            audio = null,
            discussions = null,
            news = null,
            profile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bnv_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentMT()).commit();
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

//                if(item.getItemId()==R.id.nav_mt) {
//                    if (mt == null)
//                        mt = new FragmentMT();
//                    selectedFragment = mt;
//                } else if (item.getItemId()==R.id.nav_audio) {
//                    if (audio == null)
//                        audio = new FragmentAudio();
//                    selectedFragment = audio;
//                } else if (item.getItemId()==R.id.nav_talk) {
//                    if (discussions == null)
//                        discussions = new FragmentDiscussions();
//                    selectedFragment = discussions;
//                } else if (item.getItemId()==R.id.nav_news) {
//                    if (news == null)
//                        news = new FragmentNews();
//                    selectedFragment = news;
//                } else if (item.getItemId()==R.id.nav_profile) {
//                    if (profile == null)
//                        profile = new FragmentProfile();
//                    selectedFragment = profile;
//                }

                if(item.getItemId()==R.id.nav_mt)
                    selectedFragment = new FragmentMT();
                else if (item.getItemId()==R.id.nav_audio)
                    selectedFragment =  new FragmentAudio();
                else if (item.getItemId()==R.id.nav_talk)
                    selectedFragment = new FragmentDiscussions();
                else if (item.getItemId()==R.id.nav_news)
                    selectedFragment = new FragmentNews();
                else if (item.getItemId()==R.id.nav_profile)
                    selectedFragment = new FragmentProfile();

                assert selectedFragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();

                return true;
            };
}