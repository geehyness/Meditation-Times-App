package com.hw.hlcmt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaRepositories.CollectionName;
import com.hw.hlcmt.JavaRepositories.Comment;
import com.hw.hlcmt.JavaRepositories.MessageModel;
import com.hw.hlcmt.JavaRepositories.MyProgressDialog;
import com.hw.hlcmt.JavaRepositories.UserModel;
import com.hw.hlcmt.JavaRepositories.UserType;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class ViewMeditationTimes extends AppCompatActivity {
    private MessageModel message;
    private TextView btnDeleteMessage;
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meditation_times);

        Intent i = getIntent();
        String messageJSON = i.getStringExtra(MeditationFragment.MESSAGE_JSON);

        message = (new Gson()).fromJson(messageJSON, MessageModel.class);

        TextView titleUI = findViewById(R.id.tvViewTitle);
        TextView dateUI = findViewById(R.id.tvViewDate);
        TextView authorUI = findViewById(R.id.tvViewAuthor);
        TextView messageUI = findViewById(R.id.tvViewMessage);

        titleUI.setText(message.getTitle());
        dateUI.setText("Week" + message.getWeek() + " - " + message.getYear());
        authorUI.setText(message.getAuthor() + "(" + message.getDate()+")");
        messageUI.setText(message.getMessage());

        Button commentButton = findViewById(R.id.btnComment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment();
            }
        });

        btnDeleteMessage = findViewById(R.id.tvDeleteMessage);
        btnDeleteMessage.setVisibility(View.INVISIBLE);
        getUser();
        btnDeleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMessage();
            }
        });
    }

    private void getUser(){
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        final String loginId = fbAuth.getUid();

        final DocumentReference user = ff.document(CollectionName.User+"/"+loginId);
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);

                if(userModel != null){
                    if(userModel.getUserType().equals(UserType.ADMIN))
                        btnDeleteMessage.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void deleteMessage(){
        final DocumentReference messageRef = FirebaseFirestore.getInstance().document(CollectionName.Messages+"/"+message.getMsgId());

        new AlertDialog.Builder(ViewMeditationTimes.this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Meditation Times Post?")
                .setMessage("This action cannot be reversed! Do you wish to continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        messageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ViewMeditationTimes.this, "Message Deleted!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ViewMeditationTimes.this, "Unable to delete message!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void comment(){
        final EditText commentText = findViewById(R.id.txtComment);
        final String strComment = commentText.getText().toString();

        if(TextUtils.isEmpty(strComment)){
            Toast.makeText(ViewMeditationTimes.this, "Input comment!", Toast.LENGTH_SHORT).show();
            return;
        }

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
                    final Comment msg = new Comment(message.getMsgId(), userModel.getName(), strComment);

                    ff.collection(CollectionName.Messages).add(msg).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressDialog.dismiss();
                            Toast.makeText(ViewMeditationTimes.this, "Message posted successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ViewMeditationTimes.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ViewMeditationTimes.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
