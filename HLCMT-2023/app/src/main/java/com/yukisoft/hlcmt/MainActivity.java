package com.yukisoft.hlcmt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.BNV_HomeActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.JavaActivities.UserManagement.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    public static final String LOGGED_IN_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    FirebaseFirestore ff = FirebaseFirestore.getInstance();
                    final String loginId = user.getUid();

                    final DocumentReference userDoc = ff.document(CollectionName.User+"/"+loginId);
                    userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);

                            if (userModel != null) {
                                startActivity(new Intent(MainActivity.this, BNV_HomeActivity.class));
                                finish();
//
//                                String userJSON = (new Gson()).toJson(userModel);
//                                Intent i = new Intent(MainActivity.this, HomeActivity.class);
//                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                i.putExtra(LOGGED_IN_USER, userJSON);
//                                startActivity(i);
//                                finish();
                            }
                        }
                    });
                } else {
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
