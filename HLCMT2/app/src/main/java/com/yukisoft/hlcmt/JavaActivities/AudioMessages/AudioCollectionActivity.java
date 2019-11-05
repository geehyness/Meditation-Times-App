package com.yukisoft.hlcmt.JavaActivities.AudioMessages;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.SearchView;

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
import com.yukisoft.hlcmt.JavaRepositories.Adapters.AudioCollectionListAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioCollectionModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

import java.io.IOException;
import java.util.ArrayList;

public class AudioCollectionActivity extends AppCompatActivity {

    public static final String AUDIO_COLLECTION = "Audio";

    private RecyclerView catRecyclerView;
    public AudioCollectionListAdapter audioCollectionAdapter;
    private RecyclerView.LayoutManager catLayoutManager;

    private SearchView txtSearch;

    private ArrayList<AudioCollectionModel> catList = new ArrayList<>();
    private ArrayList<AudioCollectionModel> displayCatList = new ArrayList<>();

    private UserModel currentUser;
    private Button newCollection;

    private CollectionReference collection = FirebaseFirestore.getInstance().collection(CollectionName.AudioCategory);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_collection);

        Intent i = getIntent();
        currentUser = (new Gson()).fromJson(i.getStringExtra(MainActivity.LOGGED_IN_USER), UserModel.class);

        initViews();

        newCollection = findViewById(R.id.btnNewCollection);
        newCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userJSON = (new Gson()).toJson(currentUser);
                Intent i = new Intent(AudioCollectionActivity.this, AudioCollectionManagementActivity.class);
                i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
                startActivity(i);
                finish();
            }
        });

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

                    displayCatList.addAll(catList);
                }

                audioCollectionAdapter.notifyDataSetChanged();
            }
        });

        txtSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                /* collectionView.setVisibility(View.VISIBLE); */
                txtSearch.setBackgroundColor(getResources().getColor(R.color.colorBgDark));
                return false;
            }
        });
        txtSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setBackgroundColor(getResources().getColor(R.color.colorBg));
            }
        });
        txtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchCollection(newText);
                return false;
            }
        });
    }

    private void searchCollection(String input) {
        displayCatList.clear();

        for (AudioCollectionModel a : catList) {
            if (a.getName().toLowerCase().contains(input.toLowerCase()) ||
                    a.getDetails().toLowerCase().contains(input.toLowerCase())){
                displayCatList.add(a);
            }
        }

        audioCollectionAdapter.notifyDataSetChanged();
    }

    private void initViews() {

        // SEARCH OPTIONS
        txtSearch = findViewById(R.id.txtSearchCollection);

        // CATEGORY RECYCLER VIEW SETUP
        catRecyclerView = findViewById(R.id.collectionListView);
        catRecyclerView.setHasFixedSize(false);
        audioCollectionAdapter = new AudioCollectionListAdapter(displayCatList);
        catLayoutManager = new LinearLayoutManager(this);
        catRecyclerView.setLayoutManager(catLayoutManager);
        catRecyclerView.setAdapter(audioCollectionAdapter);
        audioCollectionAdapter.setOnItemClickListener(new AudioCollectionListAdapter.OnItemClickListener() {
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
