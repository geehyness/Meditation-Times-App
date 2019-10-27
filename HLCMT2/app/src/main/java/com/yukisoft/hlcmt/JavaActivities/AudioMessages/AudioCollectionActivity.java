package com.yukisoft.hlcmt.JavaActivities.AudioMessages;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.AudioMSGActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.MeditationTimesActivity;
import com.yukisoft.hlcmt.JavaActivities.MeditationTimes.ViewMeditationTimes;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.AudioAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.AudioCollectionAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioCollectionModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

import java.io.IOException;
import java.util.ArrayList;

public class AudioCollectionActivity extends AppCompatActivity {

    private static final String AUDIO_COLLECTION = "Audio";

    private RecyclerView catRecyclerView;
    public AudioCollectionAdapter audioCollectionAdapter;
    private RecyclerView.LayoutManager catLayoutManager;

    private SearchView txtSearch;

    private ArrayList<AudioCollectionModel> catList = new ArrayList<>();
    private ArrayList<AudioCollectionModel> displayCatList = new ArrayList<>();

    private UserModel currentUser;

    private CollectionReference collection = FirebaseFirestore.getInstance().collection(CollectionName.AudioCategory);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_collection);

        Intent i = getIntent();
        currentUser = (new Gson()).fromJson(i.getStringExtra(MainActivity.LOGGED_IN_USER), UserModel.class);

        initViews();

        collection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot msg : queryDocumentSnapshots){
                        AudioCollectionModel tempMsg = msg.toObject(AudioCollectionModel.class);
                        tempMsg.setId(msg.getId());

                        boolean exists = false;

                        for (AudioCollectionModel m : catList)
                            if(m.getId().equals(tempMsg.getId()))
                                exists = true;

                        if(!exists) {
                            catList.add(tempMsg);
                        }
                    }

                    for (AudioCollectionModel current : catList)
                        if (displayCatList.size() < 5 )
                            displayCatList.add(current);
                        else
                            break;
                }

                audioCollectionAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initViews() {

        // SEARCH OPTIONS
        txtSearch = findViewById(R.id.txtSearch);

        // CATEGORY RECYCLER VIEW SETUP
        catRecyclerView = findViewById(R.id.collectionListView);
        catRecyclerView.setHasFixedSize(false);
        audioCollectionAdapter = new AudioCollectionAdapter(displayCatList);
        catLayoutManager = new LinearLayoutManager(this);
        catRecyclerView.setLayoutManager(catLayoutManager);
        catRecyclerView.setAdapter(audioCollectionAdapter);
        audioCollectionAdapter.setOnItemClickListener(new AudioCollectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                AudioCollectionModel collection = displayCatList.get(position);
                String messageJSON = (new Gson()).toJson(collection);
                String userJSON = (new Gson()).toJson(currentUser);

                Log.d("MTItem", "Item - "  + messageJSON);

                Intent i = new Intent(AudioCollectionActivity.this, AudioCollectionManagementActivity.class);
                i.putExtra(AUDIO_COLLECTION, messageJSON);
                i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
                startActivity(i);
                finish();
            }
        });
    }
}
