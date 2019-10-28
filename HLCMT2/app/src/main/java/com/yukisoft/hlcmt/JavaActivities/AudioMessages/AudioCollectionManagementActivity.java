package com.yukisoft.hlcmt.JavaActivities.AudioMessages;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.AudioAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Comparators.AudioComparator;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioCollectionModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioModel;
import com.yukisoft.hlcmt.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class AudioCollectionManagementActivity extends AppCompatActivity {

    EditText collectionName;
    ImageView collectionIcon;
    Button addToCollection, save;

    private RecyclerView audioRecyclerView;
    public AudioAdapter audioAdapter;
    private RecyclerView.LayoutManager audioLayoutManager;

    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.Audio);

    private ArrayList<AudioModel> AudioList = new ArrayList<>();
    private ArrayList<AudioModel> displayAudioList = new ArrayList<>();

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_collection_management);

        initViews();

        Intent i = getIntent();
        final AudioCollectionModel collectionModel = (new Gson()).fromJson(i.getStringExtra(AudioCollectionActivity.AUDIO_COLLECTION), AudioCollectionModel.class);

        collectionName = findViewById(R.id.txtCatName);
        collectionIcon = findViewById(R.id.imgCollectionIcon);
        addToCollection = findViewById(R.id.btnAddToCollection);

        if (collectionModel != null) {
            collectionName.setText(collectionModel.getName());
            collectionIcon.setImageResource(R.drawable.ic_album);
        } else {
            finish();
        }

        messages.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot msg : queryDocumentSnapshots){
                        AudioModel tempMsg = msg.toObject(AudioModel.class);
                        tempMsg.setId(msg.getId());

                        boolean exists = false;

                        for (AudioModel m : AudioList)
                            if(m.getId().equals(tempMsg.getId()))
                                exists = true;

                        if(!exists) {
                            AudioList.add(tempMsg);

                            Log.d("audio", tempMsg.getId());

                            if (tempMsg.getCollections() != null)
                                for (String c : tempMsg.getCollections())
                                    if (c.equals(collectionModel.getId()))
                                        displayAudioList.add(tempMsg);
                        }
                    }
                }
                Collections.sort(displayAudioList, new AudioComparator());
                Collections.sort(AudioList, new AudioComparator());
                audioAdapter.notifyDataSetChanged();
            }
        });

        addToCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int a = 0; a < 5; a++) {
                    final AudioModel tempAudio = AudioList.get(a);
                    tempAudio.addToCollection("1");

                    final int finalA = a;
                    messages.document(tempAudio.getId()).set(tempAudio).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            AudioList.get(finalA).setCollections(tempAudio.getCollections());
                            displayAudioList.add(tempAudio);
                            audioAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void initViews(){
        // AUDIO RECYCLER VIEW SETUP AND CLICK LISTENER
        audioRecyclerView = findViewById(R.id.collectionItems);
        audioRecyclerView.setHasFixedSize(true);
        audioAdapter = new AudioAdapter(displayAudioList);
        audioLayoutManager = new LinearLayoutManager(this);
        audioRecyclerView.setLayoutManager(audioLayoutManager);
        audioRecyclerView.setAdapter(audioAdapter);
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try{
                    playTrack(position);
                } catch (IOException e) {
                    Toast.makeText(AudioCollectionManagementActivity.this, "Unable to play.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();

            mediaPlayer.release();
        }
    }

    private void playTrack(int position) throws IOException {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus for playback
        AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
                return;
            }
        };
        int result = am.requestAudioFocus(focusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);


        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            AudioModel message = displayAudioList.get(position);

            if (mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(message.getPath());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

            mediaPlayer.prepareAsync();
            Toast.makeText(AudioCollectionManagementActivity.this, message.getPath(), Toast.LENGTH_SHORT).show();
        }
    }
}
