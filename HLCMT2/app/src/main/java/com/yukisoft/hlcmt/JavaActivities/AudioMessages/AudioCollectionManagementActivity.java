package com.yukisoft.hlcmt.JavaActivities.AudioMessages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import com.yukisoft.hlcmt.JavaActivities.MeditationTimes.ViewMeditationTimes;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.AudioAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Comparators.AudioComparator;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioCollectionModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class AudioCollectionManagementActivity extends AppCompatActivity {

    EditText collectionName, collectionDetails;
    ImageView collectionIcon, btnAdded, btnAll;
    Button addToCollection, btnSave;

    private RecyclerView audioRecyclerView;
    public AudioAdapter audioAdapter;
    private RecyclerView.LayoutManager audioLayoutManager;

    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.Audio);
    private CollectionReference collection = FirebaseFirestore.getInstance().collection(CollectionName.AudioCategory);

    private ArrayList<AudioModel> AudioList = new ArrayList<>();
    private ArrayList<AudioModel> displayAudioList = new ArrayList<>();
    private ArrayList<AudioModel> collectionList = new ArrayList<>();
    private ArrayList<AudioModel> newToCollection = new ArrayList<>();
    private ArrayList<AudioModel> removeList = new ArrayList<>();

    private boolean adding = false;

    private MediaPlayer mediaPlayer;

    AudioCollectionModel collectionModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_collection_management);

        initViews();

        Intent i = getIntent();
        collectionModel = (new Gson()).fromJson(i.getStringExtra(AudioCollectionActivity.AUDIO_COLLECTION), AudioCollectionModel.class);

        collectionName = findViewById(R.id.txtCatName);
        collectionIcon = findViewById(R.id.imgCollectionIcon);
        collectionDetails = findViewById(R.id.txtCollectionDetails);

        btnAdded = findViewById(R.id.btnAdded);
        btnAll = findViewById(R.id.btnAll);
        btnSave = findViewById(R.id.btnSave);

        if (collectionModel != null) {
            collectionName.setText(collectionModel.getName());
            collectionIcon.setImageResource(R.drawable.ic_album);
            collectionDetails.setText(collectionModel.getDetails());
        } else {
            Toast.makeText(this, "New collection created!", Toast.LENGTH_SHORT).show();
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
                                    if (collectionModel != null)
                                        if (c.equals(collectionModel.getId()))
                                            collectionList.add(tempMsg);
                        }
                    }
                }
                Collections.sort(collectionList, new AudioComparator());
                Collections.sort(AudioList, new AudioComparator());
                displayAudioList.clear();
                displayAudioList.addAll(collectionList);
                audioAdapter.notifyDataSetChanged();
            }
        });

        btnAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAdded.setImageResource(R.color.colorPrimary);
                btnAll.setImageResource(R.color.colorBg);

                adding = false;
                displayAudioList.clear();
                displayAudioList.addAll(collectionList);
                Collections.sort(displayAudioList, new AudioComparator());
                audioAdapter.notifyDataSetChanged();
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAll.setImageResource(R.color.colorPrimary);
                btnAdded.setImageResource(R.color.colorBg);

                adding = true;
                displayAudioList.clear();
                displayAudioList.addAll(AudioList);
                audioAdapter.notifyDataSetChanged();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AudioCollectionManagementActivity.this, R.style.MyDialogTheme)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove message?")
                        .setMessage("Save changes to " + collectionName.getText().toString())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (collectionModel != null) updateCollection();
                                else addCollection();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void updateCollection() {
        collectionModel.setName(collectionName.getText().toString());
        collectionModel.setDetails(collectionDetails.getText().toString());
        collection.document(collectionModel.getId()).set(collectionModel).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AudioCollectionManagementActivity.this, "Unable to update " + collectionModel.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        if (!newToCollection.isEmpty()) {
            for (final AudioModel a : newToCollection) {
                a.addToCollection(collectionModel.getId());
                final String name = collectionName.getText().toString();

                messages.document(a.getId()).set(a)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AudioCollectionManagementActivity.this, "" +
                                        "Unable to add " + a.getTitle() + " to " + name, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        if (!removeList.isEmpty()) {
            for (final AudioModel a : removeList) {
                a.removeCollection(collectionModel.getId());
                Log.d("delete", "remove " + collectionModel.getId() + " - " + a.getTitle());
                final String name = collectionName.getText().toString();

                messages.document(a.getId()).set(a)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AudioCollectionManagementActivity.this, "" +
                                        "Unable to remove " + a.getTitle() + " from " + name, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void addCollection() {
        final AudioCollectionModel collectionModel = new AudioCollectionModel("demoUrl", collectionName.getText().toString(), collectionDetails.getText().toString());
        collection.add(collectionModel)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    collectionModel.setId(documentReference.getId());

                    if (!newToCollection.isEmpty()) {
                        for (final AudioModel a : newToCollection) {
                            a.addToCollection(collectionModel.getId());
                            final String name = collectionName.getText().toString();

                            messages.document(a.getId()).set(a)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AudioCollectionManagementActivity.this, "" +
                                                    "Unable to add " + a.getTitle() + " to " + name, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                    if (!removeList.isEmpty()) {
                        for (final AudioModel a : removeList) {
                            a.removeCollection(collectionModel.getId());
                            Log.d("delete", "remove " + collectionModel.getId() + " - " + a.getTitle());
                            final String name = collectionName.getText().toString();

                            messages.document(a.getId()).set(a)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AudioCollectionManagementActivity.this, "" +
                                                    "Unable to remove " + a.getTitle() + " from " + name, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AudioCollectionManagementActivity.this, "Unable to update " + collectionModel.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
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
                /*try{
                    playTrack(position);
                } catch (IOException e) {
                    Toast.makeText(AudioCollectionManagementActivity.this, "Unable to play.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }*/

                final AudioModel audioFile = displayAudioList.get(position);


                if (adding) {
                    new AlertDialog.Builder(AudioCollectionManagementActivity.this, R.style.MyDialogTheme)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Remove message?")
                            .setMessage("Add " + audioFile.getTitle() + " to " + collectionName.getText().toString())
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean exists = false;

                                    for (AudioModel a : collectionList)
                                        if (a.getId().equals(audioFile.getId()))
                                            exists = true;

                                    for (AudioModel a : newToCollection)
                                        if (a.getId() == audioFile.getId())
                                            exists = true;

                                    for (AudioModel r : removeList)
                                        if (r.getId().equals(audioFile.getId()))
                                            removeList.remove(audioFile);

                                    if (!exists) {
                                        collectionList.add(audioFile);
                                        newToCollection.add(audioFile);
                                        Toast.makeText(AudioCollectionManagementActivity.this, audioFile.getTitle()+ " added to " + collectionName.getText().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    new AlertDialog.Builder(AudioCollectionManagementActivity.this, R.style.MyDialogTheme)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Add to collection?")
                            .setMessage("Are you sure you want to remove this message from the collection?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean found = false;
                                    for (AudioModel a : collectionList) {
                                        if (a.getId().equals(audioFile.getId())){
                                            found = true;
                                            collectionList.remove(audioFile);
                                            Log.d("add", collectionList.indexOf(audioFile) + " - " + a.getTitle());
                                            break;
                                        }
                                    }

                                    if (found)
                                        for (AudioModel a : newToCollection)
                                            if (a.getId().equals(audioFile.getId())){
                                                newToCollection.remove(a);
                                                break;
                                            }

                                    if(!removeList.contains(audioFile))
                                        removeList.add(audioFile);

                                    if(!found)
                                        Toast.makeText(AudioCollectionManagementActivity.this, "Error encountered!\n", Toast.LENGTH_SHORT).show();
                                    else {
                                        displayAudioList.clear();
                                        displayAudioList.addAll(collectionList);
                                        Collections.sort(displayAudioList, new AudioComparator());
                                        audioAdapter.notifyDataSetChanged();
                                    }
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
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
