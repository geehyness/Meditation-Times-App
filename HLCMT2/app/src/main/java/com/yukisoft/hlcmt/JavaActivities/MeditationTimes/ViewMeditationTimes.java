package com.yukisoft.hlcmt.JavaActivities.MeditationTimes;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.MeditationTimesActivity;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.CommentAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Models.CommentModel;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.MTAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Models.MessageModel;
import com.yukisoft.hlcmt.JavaRepositories.UIElements.MyProgressDialog;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewMeditationTimes extends AppCompatActivity {
    private MessageModel message;
    private UserModel currentUser;
    private TextView btnDeleteMessage;
    private TextView btnEditMessage;

    private RecyclerView commentRecyclerView;
    public CommentAdapter commentAdapter;
    private RecyclerView.LayoutManager commentLayoutManager;

    private ArrayList<CommentModel> commentList = new ArrayList<>();
    private String name;
    private CollectionReference commentsRef = FirebaseFirestore.getInstance().collection(CollectionName.Comments);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meditation_times);

        Intent i = getIntent();
        String messageJSON = i.getStringExtra(MeditationTimesActivity.MESSAGE_JSON);
        message = (new Gson()).fromJson(messageJSON, MessageModel.class);

        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        TextView titleUI = findViewById(R.id.tvViewTitle);
        TextView dateUI = findViewById(R.id.tvViewDate);
        TextView authorUI = findViewById(R.id.tvViewAuthor);
        TextView messageUI = findViewById(R.id.tvViewMessage);

        titleUI.setText(message.getTitle());
        dateUI.setText("Week " + message.getWeek() + " - " + message.getYear());
        authorUI.setText(message.getAuthor() + " (" + message.getDate()+")");
        messageUI.setText(message.getMessage());

        buildRecyclerView();

        Button commentButton = findViewById(R.id.btnComment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment();
            }
        });

        btnDeleteMessage = findViewById(R.id.tvDeleteMessage);
        btnEditMessage = findViewById(R.id.tvEditMessage);
        if (!currentUser.isAdmin()) {
            btnDeleteMessage.setVisibility(View.INVISIBLE);
            btnEditMessage.setVisibility(View.INVISIBLE);
        }

        btnDeleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMessage();
            }
        });
        btnEditMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMessage();
            }
        });

        commentsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(ViewMeditationTimes.this, "Error While Loading! \nError - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot msg : queryDocumentSnapshots){
                        CommentModel tempComment = msg.toObject(CommentModel.class);
                        tempComment.setCommentId(msg.getId());

                        boolean exists = false;

                        for (CommentModel c : commentList)
                            if(c.getCommentId().equals(tempComment.getCommentId()))
                                exists = true;

                        if(!exists)
                            if(tempComment.getMessageId().equals(message.getMsgId()))
                                commentList.add(tempComment);
                    }

                    boolean unsorted = true;
                    while(unsorted){
                        unsorted = false;
                    }
                }
                Collections.sort(commentList, new Comparator<CommentModel>() {
                    @Override
                    public int compare(CommentModel o1, CommentModel o2) {
                        return Integer.valueOf(o2.getNumber()).compareTo(o1.getNumber());
                    }
                });
                commentAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ViewMeditationTimes.this, MeditationTimesActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
        finish();
    }

    private void editMessage(){
        Intent i = new Intent(this, WriteMeditationTimes.class);
        i.putExtra(MeditationTimesActivity.MESSAGE_JSON, (new Gson()).toJson(message))
            .putExtra(MainActivity.LOGGED_IN_USER, (new Gson().toJson(currentUser)));
        startActivity(i);
        finish();
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
                                startActivity(new Intent(ViewMeditationTimes.this, HomeActivity.class)
                                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
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

            final CommentModel commentModel = new CommentModel(commentList.size(), message.getMsgId(), currentUser.getUserId(), currentUser.getName(), strComment);

            ff.collection(CollectionName.Comments).document("comment"+commentList.size()+"_"+message.getMsgId())
                    .set(commentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(ViewMeditationTimes.this, "Comment posted successfully", Toast.LENGTH_LONG).show();
                    commentText.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ViewMeditationTimes.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void buildRecyclerView(){
        commentRecyclerView = findViewById(R.id.commentRecycler);
        commentRecyclerView.setHasFixedSize(true);
        commentLayoutManager = new LinearLayoutManager(ViewMeditationTimes.this);
        commentAdapter = new CommentAdapter(commentList);
        //ViewCompat.setNestedScrollingEnabled(commentRecyclerView, false);

        commentRecyclerView.setLayoutManager(commentLayoutManager);
        commentRecyclerView.setAdapter(commentAdapter);

        commentAdapter.setOnItemClickListener(new MTAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CommentModel commentModel = commentList.get(position);

                if (commentModel.getUserId().equals(currentUser.getUserId())){
                    final DocumentReference commentRef = FirebaseFirestore.getInstance().document(CollectionName.Comments+"/"+commentModel.getCommentId());

                    new AlertDialog.Builder(ViewMeditationTimes.this, R.style.MyDialogTheme)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Delete Comment?")
                            .setMessage("This action cannot be reversed! Do you wish to continue?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    commentRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ViewMeditationTimes.this, "Comment Deleted!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ViewMeditationTimes.this, ViewMeditationTimes.class)
                                                .putExtra(MeditationTimesActivity.MESSAGE_JSON, (new Gson()).toJson(message))
                                                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
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
                Log.d("MTItem", "Item - "+commentModel.getUserId());
            }
        });
    }
}
