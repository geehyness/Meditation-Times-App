package com.hw.hlcmt;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hw.hlcmt.JavaRepositories.CollectionName;
import com.hw.hlcmt.JavaRepositories.MessageModel;
import com.hw.hlcmt.JavaRepositories.MyProgressDialog;
import com.hw.hlcmt.JavaRepositories.UserModel;

import java.text.DateFormat;
import java.util.Calendar;

public class WriteMeditationTimes extends AppCompatActivity {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private EditText titleUI;
    private EditText dateUI;
    private EditText msgUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_meditation_times);

        titleUI = findViewById(R.id.txtTitleMT);
        dateUI = findViewById(R.id.txtDateMT);
        msgUI = findViewById(R.id.txtMessageMT);

        Button post = findViewById(R.id.btnPostMT);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    void post(){
        Calendar c = Calendar.getInstance();
        final String title = titleUI.getText().toString();
        final String strDate = DateFormat.getDateInstance().format(c.getTime());
        final int week = Integer.valueOf(dateUI.getText().toString());
        final String message = msgUI.getText().toString();

        if (TextUtils.isEmpty(title)){
            Toast.makeText(WriteMeditationTimes.this, "Title cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strDate)){
            Toast.makeText(WriteMeditationTimes.this, "Week cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(message)){
            Toast.makeText(WriteMeditationTimes.this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Toast.makeText(WriteMeditationTimes.this, "To be implemented!", Toast.LENGTH_SHORT).show();

        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final FirebaseFirestore ff = FirebaseFirestore.getInstance();
        final String loginId = fbAuth.getUid();

        final DocumentReference user = ff.document(CollectionName.User+"/"+loginId);
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);

                if(userModel != null){
                    MessageModel msg = new MessageModel(title, message, userModel.getName(), strDate, week, R.drawable.ic_meditation_times);
                    ff.collection(CollectionName.Messages).add(msg).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressDialog.dismiss();
                            Toast.makeText(WriteMeditationTimes.this, "Message posted successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(WriteMeditationTimes.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(WriteMeditationTimes.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
