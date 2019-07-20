package com.yukisoft.hlcmt.JavaActivities.UserManagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.UIElements.MyProgressDialog;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

public class DeleteAccountActivity extends AppCompatActivity {
    private UserModel currentUser;
    private String userJSON;
    private String userConfirm;

    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        Intent i = getIntent();
        userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        fbAuth.signOut();

        Button delete = findViewById(R.id.btnDelAccount);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        Button back = findViewById(R.id.btnDelBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home();
            }
        });
    }

    @Override
    public void onBackPressed() {
        home();
    }

    private void home(){
        Intent i = new Intent(DeleteAccountActivity.this, LoginActivity.class);
        i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
        startActivity(i);
        finish();
    }

    private void delete(){
        EditText username = findViewById(R.id.txtDelEmail),
                password = findViewById(R.id.txtDelPass);

        final String uemail = username.getText().toString().trim();
        String upass = password.getText().toString().trim();

        // Validating input
        if (TextUtils.isEmpty(uemail)) {
            Toast.makeText(DeleteAccountActivity.this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
            username.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            Toast.makeText(DeleteAccountActivity.this, "Email is invalid!", Toast.LENGTH_SHORT).show();
            username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(upass)) {
            Toast.makeText(DeleteAccountActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return;
        }

        if (upass.length() < 8) {
            Toast.makeText(DeleteAccountActivity.this, "Password should be atleast 8 characters long!", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return;
        }

        CheckBox delConf = (CheckBox) findViewById(R.id.cbDelConfirm);
        if (!delConf.isChecked()){
            Toast.makeText(DeleteAccountActivity.this, "Confirm your intent to delete your account by ticking the Check Box", Toast.LENGTH_SHORT).show();
            return;
        }

        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        try{
            fbAuth.signInWithEmailAndPassword(uemail, upass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            final String loginId = fbAuth.getUid();

                            if (loginId.equals(currentUser.getUserId())){
                                new AlertDialog.Builder(DeleteAccountActivity.this, R.style.MyDialogTheme)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Delete Account?")
                                        .setMessage("This action cannot be reversed! Do you wish to continue?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseUser user = fbAuth.getCurrentUser();
                                                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FirebaseFirestore ff = FirebaseFirestore.getInstance();
                                                        final String loginId = fbAuth.getUid();

                                                        DocumentReference userRef = ff.document(CollectionName.User+"/"+loginId);
                                                        userRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(DeleteAccountActivity.this, "Account Deleted", Toast.LENGTH_SHORT).show();

                                                                Intent i = new Intent(DeleteAccountActivity.this, LoginActivity.class);
                                                                startActivity(i);
                                                                DeleteAccountActivity.this.finish();
                                                            }
                                                        });
                                                    }
                                                });
                                            }

                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                progressDialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(DeleteAccountActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e){
            progressDialog.dismiss();
            Toast.makeText(DeleteAccountActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
