package com.hw.hlcmt;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaRepositories.CollectionName;
import com.hw.hlcmt.JavaRepositories.InformationModel;
import com.hw.hlcmt.JavaRepositories.UserModel;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;

import javax.annotation.Nullable;

public class InformationActivity extends AppCompatActivity {
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Intent i = getIntent();
        currentUser = (new Gson()).fromJson(i.getStringExtra(MainActivity.LOGGED_IN_USER), UserModel.class);

        TextView privacy = findViewById(R.id.privacy);
        TextView terms = findViewById(R.id.terms);

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this, PrivacyActivity.class));
            }
        });
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this, TermsActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
        finish();
    }
}
