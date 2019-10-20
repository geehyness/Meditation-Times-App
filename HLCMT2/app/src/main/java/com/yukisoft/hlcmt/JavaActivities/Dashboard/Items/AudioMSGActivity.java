package com.yukisoft.hlcmt.JavaActivities.Dashboard.Items;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaActivities.AudioMessages.AddAudioActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.AudioAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Comparators.AudioComparator;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class AudioMSGActivity extends AppCompatActivity implements View.OnClickListener {
    private UserModel currentUser;

    // Recycler View
    private RecyclerView audioRecyclerView;
    public AudioAdapter audioAdapter;
    private RecyclerView.LayoutManager audioLayoutManager;

    private EditText txtSearch;

    private ArrayList<AudioModel> AudioList = new ArrayList<>();
    private ArrayList<AudioModel> displayAudioList = new ArrayList<>();
    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.Audio);
    public static final String MESSAGE_JSON = "MessageModel";

    // Media Player
    private int length;
    private AudioModel audio = null;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView lblSheetTitle, lblPlayTime, lblPlayLength, lblDetails, lblDetailsTitle;
    private BottomSheetBehavior detailsSheetBehaviour, playerSheetBehaviour;
    private ImageView btnPlaySmall, btnPlayPause, btnPrev, btnNext, btnRepeat, btnShuffle, btnOpenDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_msg);

        Intent i = getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        View bottomSheetDetails = findViewById(R.id.bottom_sheet_details);
        detailsSheetBehaviour = BottomSheetBehavior.from(bottomSheetDetails);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        playerSheetBehaviour = BottomSheetBehavior.from(bottomSheet);

        initViews();

        FloatingActionButton upload = findViewById(R.id.btnAddAudio);
        upload.hide();
        if(currentUser!=null && currentUser.isAdmin()){
            upload.show();
        }

        messages.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot msg : queryDocumentSnapshots){
                        AudioModel tempMsg = msg.toObject(AudioModel.class);
                        tempMsg.setId(msg.getId());

                        boolean exists = false;

                        for (AudioModel m : displayAudioList)
                            if(m.getId().equals(tempMsg.getId()))
                                exists = true;

                        if(!exists) {
                            displayAudioList.add(tempMsg);
                        }
                    }
                }
                Collections.sort(displayAudioList, new AudioComparator());
                Collections.sort(AudioList, new AudioComparator());
                audioAdapter.notifyDataSetChanged();
            }
        });

        btnPlaySmall.setOnClickListener(this);
        btnPlayPause.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnOpenDetails.setOnClickListener(this);
        upload.setOnClickListener(this);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void afterTextChanged(Editable editable) {
                txtSearch.removeTextChangedListener(this);
                search();
                txtSearch.setSelection(editable.length()); //moves the pointer to end
                txtSearch.addTextChangedListener(this);
            }
        });

        detailsSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //mTextViewState.setText("Collapsed");
                        btnOpenDetails.setImageResource(R.drawable.ic_mp_more);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //mTextViewState.setText("Dragging...");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        //mTextViewState.setText("Expanded");
                        btnOpenDetails.setImageResource(R.drawable.ic_mp_less);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        //mTextViewState.setText("Hidden");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //mTextViewState.setText("Settling...");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //mTextViewState.setText("Sliding...");
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int place = seekBar.getProgress();
                mediaPlayer.seekTo(place);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AudioMSGActivity.this, HomeActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
        finish();
    }

    private void search(){
        String input = txtSearch.getText().toString();
        displayAudioList.clear();

        if (input.isEmpty()){
            displayAudioList = AudioList;
            audioAdapter.notifyDataSetChanged();
        } else {
            for (AudioModel m : AudioList){
                if (m.getTitle().toLowerCase().contains(input) ||
                        m.getDetails().toLowerCase().contains(input)){
                    displayAudioList.add(m);
                    Log.d("search", String.valueOf(m.getTitle()));
                }
            }
            audioAdapter.notifyDataSetChanged();
        }

        audioAdapter.notifyDataSetChanged();
    }

    private void initViews(){
        initMediaPlayer();

        // SEARCH OPTIONS
        txtSearch = findViewById(R.id.txtSearchAudio);

        // RECYCLER VIEW SETUP
        audioRecyclerView = findViewById(R.id.audioRecyclerView);
        audioRecyclerView.setHasFixedSize(true);
        audioLayoutManager = new LinearLayoutManager(this);
        audioAdapter = new AudioAdapter(displayAudioList);

        // RECYCLER VIEW FINAL SETUP AND CLICK LISTENER
        audioRecyclerView.setLayoutManager(audioLayoutManager);
        audioRecyclerView.setAdapter(audioAdapter);
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                AudioModel message = displayAudioList.get(position);
                String messageJSON = (new Gson()).toJson(message);
                String userJSON = (new Gson()).toJson(currentUser);

                Log.d("MTItem", "Item - "  + messageJSON);

                if (mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                audio = message;

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(message.getPath());
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();

                        lblSheetTitle.setText(audio.getTitle());
                        lblDetails.setText(audio.getTitle());
                        lblDetailsTitle.setText(audio.getDetails());
                        btnPlaySmall.setImageResource(R.drawable.ic_mp_pause_small);
                        btnPlayPause.setImageResource(R.drawable.ic_mp_pause);

                        length = mediaPlayer.getDuration();
                        seekBar.setMax(length);

                        lblPlayTime.setText(String.valueOf(length));
                        lblPlayLength.setText(DateUtils.formatElapsedTime(length));
                    }
                });

                mediaPlayer.prepareAsync();

                Toast.makeText(AudioMSGActivity.this, message.getPath(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initMediaPlayer() {
        lblSheetTitle = findViewById(R.id.lblSheetTitle);
        lblPlayLength = findViewById(R.id.lblPlayLength);
        lblPlayTime = findViewById(R.id.lblPlayTime);
        lblDetails = findViewById(R.id.txtDetails);
        lblDetailsTitle = findViewById(R.id.txtDetailsTitle);
        seekBar = findViewById(R.id.seekBar);

        btnPlaySmall = findViewById(R.id.btnSheetPlay);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnOpenDetails = findViewById(R.id.btnOpenDetails);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.btnSheetPlay):
            case (R.id.btnPlayPause):
                //Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();
                if (mediaPlayer != null){
                    if(mediaPlayer.isPlaying()) {
                        btnPlaySmall.setImageResource(R.drawable.ic_mp_play_small);
                        btnPlayPause.setImageResource(R.drawable.ic_mp_play);
                        mediaPlayer.pause();
                    } else {
                        btnPlaySmall.setImageResource(R.drawable.ic_mp_pause_small);
                        btnPlayPause.setImageResource(R.drawable.ic_mp_pause);
                        mediaPlayer.start();
                    }
                } else
                    Toast.makeText(this, "Select a message to start playing it.", Toast.LENGTH_SHORT).show();
                break;

            case (R.id.btnPrev):
                // TODO: 2019/10/19 play prev audio
                break;

            case (R.id.btnNext):
                // TODO: 2019/10/19 play next audio
                break;

            case (R.id.btnRepeat):
                // TODO: 2019/10/19 toggle repeat
                break;

            case (R.id.btnShuffle):
                // TODO: 2019/10/19 toggle shuffle
                break;

            case (R.id.btnOpenDetails):
                if(detailsSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    btnOpenDetails.setImageResource(R.drawable.ic_mp_less);
                    detailsSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    btnOpenDetails.setImageResource(R.drawable.ic_mp_more);
                    detailsSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;

            case (R.id.btnAddAudio):
                startActivity(new Intent(AudioMSGActivity.this, AddAudioActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                break;
        }
    }
}
