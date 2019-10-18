package com.yukisoft.hlcmt.JavaActivities.Dashboard.Items;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.JavaRepositories.UIElements.MyProgressDialog;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

public class SettingsActivity extends AppCompatActivity {
    private UserModel currentUser;
    private CheckBox en;
    private CheckBox ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent i = getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        en = findViewById(R.id.cbEnglish);
        ss = findViewById(R.id.cbSiswati);
        en.setChecked(SettingsActivity.this.currentUser.isEnglish());
        ss.setChecked(SettingsActivity.this.currentUser.isSiswati());

        en.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!en.isChecked()){
                    if(!ss.isChecked()){
                        Toast.makeText(SettingsActivity.this, "One language has to be selected at all times!", Toast.LENGTH_SHORT).show();
                        en.setChecked(true);
                    }
                }
            }
        });
        ss.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!ss.isChecked()){
                    if(!en.isChecked()){
                        Toast.makeText(SettingsActivity.this, "One language has to be selected at all times!", Toast.LENGTH_SHORT).show();
                        ss.setChecked(true);
                    }
                }
            }
        });

        Button save = findViewById(R.id.btnSaveSettings);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
        finish();
    }

    private void saveSettings(){
        FirebaseFirestore ff = FirebaseFirestore.getInstance();

        currentUser.setEnglish(en.isChecked());
        currentUser.setSiswati(ss.isChecked());

        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        ff.collection(CollectionName.User).document(currentUser.getUserId()).set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(SettingsActivity.this, "Setting Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, HomeActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SettingsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
