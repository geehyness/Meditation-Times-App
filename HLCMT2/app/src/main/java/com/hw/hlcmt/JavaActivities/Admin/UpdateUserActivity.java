package com.hw.hlcmt.JavaActivities.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.hw.hlcmt.JavaRepositories.Models.UserModel;
import com.hw.hlcmt.MainActivity;
import com.hw.hlcmt.R;

public class UpdateUserActivity extends AppCompatActivity {
    private UserModel currentAdminUser;
    private UserModel currentUser;
    private Switch isAdmin;
    private Switch isWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        Intent i = getIntent();
        currentAdminUser = (new Gson()).fromJson(i.getStringExtra(MainActivity.LOGGED_IN_USER), UserModel.class);
        currentUser = (new Gson()).fromJson(i.getStringExtra(UserManagementActivity.USER_MANAGEMENT), UserModel.class);

        TextView name = findViewById(R.id.tvUIUsername);
        TextView email = findViewById(R.id.tvUIEmail);
        isAdmin = findViewById(R.id.swIsAdmin);
        isWriter = findViewById(R.id.swIsWriter);

        if(currentUser != null && currentAdminUser !=null && currentAdminUser.isAdmin()){
            name.setText(currentUser.getName());
            email.setText(currentUser.getEmail());
            isAdmin.setChecked(currentUser.isAdmin());
            isWriter.setChecked(currentUser.isWriter());
        }

        isAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isAdmin.isChecked()){
                    new AlertDialog.Builder(UpdateUserActivity.this, R.style.MyDialogTheme)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Make " + currentUser.getName() + " an admin?")
                            .setMessage("We recommend that you give the admin role to only people you trust. Do you wish to continue?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isAdmin.setChecked(true);
                                }

                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isAdmin.setChecked(false);
                                }
                            })
                            .show();
                }
            }
        });

        Button btnSave = findViewById(R.id.btnSaveUser);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UpdateUserActivity.this, UserManagementActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentAdminUser)));
        finish();
    }

    private void save(){
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Save user privileges?")
                .setMessage("Next time the user logs in, they will have the privileges you have set. Do you wish to continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentUser.setAdmin(isAdmin.isChecked());
                        currentUser.setWriter(isWriter.isChecked());

                        FirebaseFirestore ff = FirebaseFirestore.getInstance();

                        ff.collection(CollectionName.User).document(currentUser.getUserId()).set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdateUserActivity.this, "User privileges updated.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(UpdateUserActivity.this, UserManagementActivity.class)
                                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentAdminUser)));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateUserActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
