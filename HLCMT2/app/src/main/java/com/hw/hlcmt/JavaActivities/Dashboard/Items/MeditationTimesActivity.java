package com.hw.hlcmt.JavaActivities.Dashboard.Items;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.hw.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.hw.hlcmt.JavaRepositories.Fixed.Language;
import com.hw.hlcmt.JavaRepositories.Adapters.MTAdapter;
import com.hw.hlcmt.JavaRepositories.Comparators.MTComparator;
import com.hw.hlcmt.JavaRepositories.Models.MessageModel;
import com.hw.hlcmt.JavaRepositories.Models.UserModel;
import com.hw.hlcmt.MainActivity;
import com.hw.hlcmt.R;
import com.hw.hlcmt.JavaActivities.MeditationTimes.ViewMeditationTimes;
import com.hw.hlcmt.JavaActivities.MeditationTimes.WriteMeditationTimes;

import java.util.ArrayList;
import java.util.Collections;

public class MeditationTimesActivity extends AppCompatActivity {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private UserModel currentUser;

    private RecyclerView mtRecyclerView;
    public MTAdapter mtAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;
    private FloatingActionButton btnAddMT;

    private ArrayList<MessageModel> MTList = new ArrayList<>();
    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.Messages);
    public static final String MESSAGE_JSON = "MessageModel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation_times);
        btnAddMT = findViewById(R.id.btnAddMT);
        btnAddMT.hide();

        Intent i = getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        if(currentUser.isWriter())
            btnAddMT.show();

        buildRecyclerView();

        messages.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(MeditationTimesActivity.this, "Error While Loading! \nError - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot msg : queryDocumentSnapshots){
                        MessageModel tempMsg = msg.toObject(MessageModel.class);
                        tempMsg.setMsgId(msg.getId());

                        boolean exists = false;

                        for (MessageModel m : MTList)
                            if(m.getMsgId().equals(tempMsg.getMsgId()))
                                exists = true;

                        if(!exists) {
                            if (currentUser.isEnglish() && tempMsg.getLanguage().equals(Language.English.toString()))
                                MTList.add(tempMsg);
                            if (currentUser.isSiswati() && tempMsg.getLanguage().equals(Language.Siswati.toString()))
                                MTList.add(tempMsg);
                        }
                    }
                }
                Collections.sort(MTList, new MTComparator());
                mtAdapter.notifyDataSetChanged();
            }
        });

        btnAddMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MeditationTimesActivity.this, WriteMeditationTimes.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser));
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MeditationTimesActivity.this, HomeActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
        finish();
    }

    private void buildRecyclerView(){
        mtRecyclerView = findViewById(R.id.MTRecyclerView);
        mtRecyclerView.setHasFixedSize(true);
        mtLayoutManager = new LinearLayoutManager(this);
        mtAdapter = new MTAdapter(MTList);

        mtRecyclerView.setLayoutManager(mtLayoutManager);
        mtRecyclerView.setAdapter(mtAdapter);

        mtAdapter.setOnItemClickListener(new MTAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MessageModel message = MTList.get(position);
                String messageJSON = (new Gson()).toJson(message);
                String userJSON = (new Gson()).toJson(currentUser);

                Log.d("MTItem", "Item - "+messageJSON);

                Intent i = new Intent(MeditationTimesActivity.this, ViewMeditationTimes.class);
                i.putExtra(MESSAGE_JSON, messageJSON);
                i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
                startActivity(i);
                finish();
            }
        });
    }
}
