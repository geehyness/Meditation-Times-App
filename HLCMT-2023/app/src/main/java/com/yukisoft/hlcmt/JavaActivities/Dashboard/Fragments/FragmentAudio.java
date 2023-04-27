package com.yukisoft.hlcmt.JavaActivities.Dashboard.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaActivities.AudioMessages.AddAudioActivity;
import com.yukisoft.hlcmt.JavaActivities.AudioMessages.AudioCollectionActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.AudioMSGActivity;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.AudioAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.AudioCollectionAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Comparators.AudioComparator;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioCollectionModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class FragmentAudio extends Fragment {
    private UserModel currentUser;

    private RecyclerView catRecyclerView;
    public AudioAdapter audioAdapter;
    public AudioCollectionAdapter audioCollectionAdapter;
    private RecyclerView.LayoutManager catLayoutManager;

    private SearchView txtSearch;

    private ConstraintLayout messageView, collectionView;
    private ImageView extendCollection;

    private ArrayList<AudioModel> AudioList = new ArrayList<>();
    private ArrayList<AudioModel> displayAudioList = new ArrayList<>();

    private ArrayList<AudioCollectionModel> catList = new ArrayList<>();
    private ArrayList<AudioCollectionModel> displayCatList = new ArrayList<>();

    private String currentCollection = null;
    private TextView txtCatName;
    private TextView txtCatDetails;

    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.Audio);
    private CollectionReference collection = FirebaseFirestore.getInstance().collection(CollectionName.AudioCategory);
    public static final String MESSAGE_JSON = "MessageModel";

    // Media Player
    private int length, track;
    private AudioModel audio = null;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView lblSheetTitle, lblPlayTime, lblPlayLength, lblDetails, lblDetailsTitle;
    private BottomSheetBehavior detailsSheetBehaviour, playerSheetBehaviour;
    private ImageView btnPlaySmall, btnPlayPause, btnPrev, btnNext, btnRepeat, btnShuffle, btnOpenDetails, btnDeleteCollection, btnAddMenu;
    int curPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        Intent i = getActivity().getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        View bottomSheetDetails = view.findViewById(R.id.bottom_sheet_details);
        detailsSheetBehaviour = BottomSheetBehavior.from(bottomSheetDetails);

        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        playerSheetBehaviour = BottomSheetBehavior.from(bottomSheet);

        initViews(view);

        txtCatName = view.findViewById(R.id.txtCatName);
        txtCatDetails = view.findViewById(R.id.txtCollectionDetails);
        txtCatDetails.setVisibility(View.GONE);
        messageView = view.findViewById(R.id.messageView);
        collectionView = view.findViewById(R.id.collectionView);
        extendCollection = view.findViewById(R.id.extendCollection);

        txtSearch = view.findViewById(R.id.txtSearch);

        btnAddMenu.setVisibility(View.GONE);
        if(currentUser!=null && currentUser.isAdmin()){
            btnAddMenu.setVisibility(View.VISIBLE);
        }

        messages.addSnapshotListener((queryDocumentSnapshots, e) -> {
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
                    }
                }
            }
            Collections.sort(AudioList, new AudioComparator());
            displayAudioList.addAll(AudioList);

            audioAdapter.notifyDataSetChanged();
        });

        collection.addSnapshotListener((queryDocumentSnapshots, e) -> {
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
                    if (displayCatList.size() < 5)
                        displayCatList.add(current);
                    else
                        break;

                if (catList.isEmpty()){
                    txtCatDetails.setText("There are no collections available");
                    txtCatDetails.setVisibility(View.VISIBLE);
                    extendCollection.setVisibility(View.GONE);
                } else {
                    txtCatDetails.setVisibility(View.GONE);
                    extendCollection.setVisibility(View.VISIBLE);
                }
            }

            audioCollectionAdapter.notifyDataSetChanged();
        });

        btnPlaySmall.setOnClickListener(this::onClick);
        btnPlayPause.setOnClickListener(this::onClick);
        btnPrev.setOnClickListener(this::onClick);
        btnNext.setOnClickListener(this::onClick);
        btnRepeat.setOnClickListener(this::onClick);
        btnShuffle.setOnClickListener(this::onClick);
        btnOpenDetails.setOnClickListener(this::onClick);
        extendCollection.setOnClickListener(this::onClick);
        btnDeleteCollection.setOnClickListener(this::onClick);

        /*txtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void afterTextChanged(Editable editable) {
                txtSearch.removeTextChangedListener(this);
                search();
                txtSearch.setSelection(editable.length()); //moves the pointer to end
                txtSearch.addTextChangedListener(this);
            }
        });*/

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
                if (mediaPlayer != null){
                    int place = seekBar.getProgress();
                    mediaPlayer.seekTo(place);
                }
            }
        });

        txtSearch.setOnCloseListener(() -> {
            collectionView.setVisibility(View.VISIBLE);
            txtSearch.setBackgroundColor(getResources().getColor(R.color.colorBgDark));
            return false;
        });
        txtSearch.setOnSearchClickListener(v -> {
            collectionView.setVisibility(View.GONE);
            txtSearch.setBackgroundColor(getResources().getColor(R.color.colorBg));
        });
        txtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAudio(newText);
                return false;
            }
        });

        return view;
    }

    private void searchAudio(String input) {
        displayAudioList.clear();

        for (AudioModel a : AudioList) {
            if (a.getTitle().toLowerCase().contains(input.toLowerCase()) ||
                    a.getDetails().toLowerCase().contains(input.toLowerCase()) ||
                    a.getSpeaker().toLowerCase().contains(input.toLowerCase())){
                displayAudioList.add(a);
            }
        }

        audioAdapter.notifyDataSetChanged();
    }

    /*private void search(){
        String input = txtSearch.get().toString();
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
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    public void showAddMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.add_item_menu);
        popup.show();
    }

    public void showItemMenu(View v, int position) {
        curPosition = position;
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        if (currentUser.isAdmin())
            popup.inflate(R.menu.audio_menu_admin);
        else
            popup.inflate(R.menu.audio_menu);
        popup.show();
    }

    private void initViews(View view){
        initMediaPlayer(view);

        btnDeleteCollection = view.findViewById(R.id.btnDeleteCollection);
        btnDeleteCollection.setVisibility(View.GONE);
        btnAddMenu = view.findViewById(R.id.btnAddMenu);
        btnAddMenu.setOnClickListener(this::showAddMenu);

        // SEARCH OPTIONS
        txtSearch = view.findViewById(R.id.txtSearch);

        // CATEGORY RECYCLER VIEW SETUP
        catRecyclerView = view.findViewById(R.id.catRecyclerView);
        catRecyclerView.setHasFixedSize(false);
        audioCollectionAdapter = new AudioCollectionAdapter(displayCatList);
        catLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        catRecyclerView.setLayoutManager(catLayoutManager);
        catRecyclerView.setAdapter(audioCollectionAdapter);
        audioCollectionAdapter.setOnItemClickListener(position -> {
            AudioCollectionModel collection = displayCatList.get(position);
            Toast.makeText(getContext(), collection.getName(), Toast.LENGTH_SHORT).show();

            currentCollection = collection.getId();
            txtCatName.setText(collection.getName());
            catRecyclerView.setVisibility(View.GONE);
            txtCatDetails.setText(collection.getDetails());
            txtCatDetails.setVisibility(View.VISIBLE);

            displayAudioList.clear();

            for (AudioModel a : AudioList)
                for (String s : a.getCollections())
                    if (s.equals(collection.getId()))
                        displayAudioList.add(a);

            audioAdapter.notifyDataSetChanged();

            messageView.setVisibility(View.VISIBLE);
            extendCollection.setVisibility(View.GONE);
            btnDeleteCollection.setVisibility(View.VISIBLE);
        });
/*
        btnShuffle.setOnLongClickListener(v -> {
            Toast.makeText(AudioMSGActivity.this, "Stop", Toast.LENGTH_SHORT).show();
            return false;
        });*/

        // AUDIO RECYCLER VIEW SETUP AND CLICK LISTENER
        // Recycler View
        RecyclerView audioRecyclerView = view.findViewById(R.id.audioRecyclerView);
        audioRecyclerView.setHasFixedSize(true);
        audioAdapter = new AudioAdapter(displayAudioList);
        RecyclerView.LayoutManager audioLayoutManager = new LinearLayoutManager(getContext());
        audioRecyclerView.setLayoutManager(audioLayoutManager);
        audioRecyclerView.setAdapter(audioAdapter);
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                /*try{
                    playTrack(position);
                } catch (IOException e) {
                    Toast.makeText(AudioMSGActivity.this, "Unable to play.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }*/

                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(displayAudioList.get(position).getPath()));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    new AlertDialog.Builder(getContext(), R.style.MyDialogTheme)
                            .setIcon(R.drawable.ic_error)
                            .setTitle("Error")
                            .setMessage("Link to resource may be invalid!\nWould you like to report this error?")
                            .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: 2020/06/07 REPORT LINK

                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onMoreClick(int position) {
                Toast.makeText(getContext(), "Yay - " + position, Toast.LENGTH_SHORT).show();
                showItemMenu(getActivity().getWindow().getDecorView().findViewById(R.id.audioRecyclerView), position);
            }
        });

        //audioAdapter.setOnItemLongClickListener(position -> Toast.makeText(AudioMSGActivity.this, "Position - " + position, Toast.LENGTH_SHORT).show());
    }

    private void playTrack(int position) throws IOException {
        track = position;
        AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

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
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();

                lblSheetTitle.setText(audio.getTitle());
                lblDetails.setText(audio.getTitle());
                lblDetailsTitle.setText(audio.getDetails());
                btnPlaySmall.setImageResource(R.drawable.ic_mp_pause_small);
                btnPlayPause.setImageResource(R.drawable.ic_mp_pause);

                length = mediaPlayer.getDuration();
                seekBar.setMax(length);

                // Thread (Update positionBar & timeLabel)
                new Thread(() -> {
                    while (mediaPlayer != null) {
                        try {
                            Message msg = new Message();
                            msg.what = mediaPlayer.getCurrentPosition();
                            handler.sendMessage(msg);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) { Log.d("error", e.getMessage()); }
                    }
                }).start();

                lblPlayTime.setText(String.valueOf(length));
                lblPlayLength.setText(DateUtils.formatElapsedTime(length));
            });

            mediaPlayer.prepareAsync();
            Toast.makeText(getContext(), message.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            seekBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            lblPlayTime.setText(elapsedTime);

            String remainingTime = createTimeLabel(mediaPlayer.getDuration()-currentPosition);
            lblPlayLength.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    private void initMediaPlayer(View v) {
        lblSheetTitle = v.findViewById(R.id.lblSheetTitle);
        lblPlayLength = v.findViewById(R.id.lblPlayLength);
        lblPlayTime = v.findViewById(R.id.lblPlayTime);
        lblDetails = v.findViewById(R.id.txtDetails);
        lblDetailsTitle = v.findViewById(R.id.txtDetailsTitle);
        seekBar = v.findViewById(R.id.seekBar);

        btnPlaySmall = v.findViewById(R.id.btnSheetPlay);
        btnPlayPause = v.findViewById(R.id.btnPlayPause);
        btnPrev = v.findViewById(R.id.btnPrev);
        btnNext = v.findViewById(R.id.btnNext);
        btnRepeat = v.findViewById(R.id.btnRepeat);
        btnShuffle = v.findViewById(R.id.btnShuffle);
        btnOpenDetails = v.findViewById(R.id.btnOpenDetails);
    }

    private void deleteCollection(){
        ArrayList<AudioModel> deleteList = new ArrayList<>();
        if (currentCollection != null) {
            for (AudioModel a : AudioList) {
                for (String s : a.getCollections()) {
                    if (s.equals(currentCollection)) {
                        deleteList.add(a);
                    }
                }
            }

            if (!deleteList.isEmpty()) {
                for (AudioModel a : deleteList) {
                    a.removeCollection(currentCollection);
                    messages.document(a.getId()).set(a);
                }
            }

            collection.document(currentCollection).delete();
            currentCollection = null;
        }

        startActivity(new Intent(getContext(), AudioMSGActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
        //finish();
    }

    //@Override
    public void onClick(View view) {
        if(view.getId()==R.id.btnSheetPlay | view.getId()==R.id.btnPlayPause) {
            //Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    btnPlaySmall.setImageResource(R.drawable.ic_mp_play_small);
                    btnPlayPause.setImageResource(R.drawable.ic_mp_play);
                    mediaPlayer.pause();
                } else {
                    btnPlaySmall.setImageResource(R.drawable.ic_mp_pause_small);
                    btnPlayPause.setImageResource(R.drawable.ic_mp_pause);
                    mediaPlayer.start();
                }
            } else
                Toast.makeText(getContext(), "Select a message to start playing it.", Toast.LENGTH_SHORT).show();
        } else if (view.getId()==R.id.btnPrev) {
            // TODO: 2019/10/19 play prev audio
        } else if (view.getId()==R.id.btnNext) {
            if (mediaPlayer != null)
                if (track < displayAudioList.size() - 1)
                    try {
                        playTrack(track + 1);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Unable to play.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                else
                    try {
                        playTrack(0);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Unable to play.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            else
                Toast.makeText(getContext(), "Select a message to start playing it.", Toast.LENGTH_SHORT).show();
        } else if (view.getId()==R.id.btnRepeat) {
            // TODO: 2019/10/19 toggle repeat
        } else if (view.getId()==R.id.btnShuffle) {
            // TODO: 2019/10/19 toggle shuffle
        } else if (view.getId()==R.id.btnOpenDetails) {
            if (detailsSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                btnOpenDetails.setImageResource(R.drawable.ic_mp_less);
                detailsSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                btnOpenDetails.setImageResource(R.drawable.ic_mp_more);
                detailsSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        } else if (view.getId()==R.id.extendCollection) {
            if (catRecyclerView.getLayoutManager() != catLayoutManager) {
                catRecyclerView.setLayoutManager(catLayoutManager);
                messageView.setVisibility(View.VISIBLE);

                displayCatList.clear();
                for (AudioCollectionModel current : catList)
                    if (displayCatList.size() < 5)
                        displayCatList.add(current);
                    else
                        break;

                audioCollectionAdapter.notifyDataSetChanged();

                extendCollection.setImageResource(R.drawable.ic_arrow_down);
                messageView.setVisibility(View.VISIBLE);
            } else {
                catRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

                displayCatList.clear();
                displayCatList.addAll(catList);

                audioCollectionAdapter.notifyDataSetChanged();

                extendCollection.setImageResource(R.drawable.ic_arrow_up);
                messageView.setVisibility(View.GONE);
            }
        } else if (view.getId()==R.id.btnDeleteCollection){
            AudioCollectionModel collectionModel = null;

            for (AudioCollectionModel c : catList)
                if (c.getId().equals(currentCollection))
                    collectionModel = c;

            if (collectionModel != null)
                new AlertDialog.Builder(getContext(), R.style.MyDialogTheme)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove Collection?")
                        .setMessage("Are you sure you want to delete " + collectionModel.getName())
                        .setPositiveButton("Yes", (dialog, which) -> deleteCollection())
                        .setNegativeButton("No", null)
                        .show();
        }
    }

    //@Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.menuAddAudio) {
            startActivity(new Intent(getContext(), AddAudioActivity.class)
                    .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
            return true;
        } else if (item.getItemId()==R.id.menuAddAudioCollection) {
            startActivity(new Intent(getContext(), AudioCollectionActivity.class)
                    .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
            return true;
        } else if (item.getItemId()==R.id.audioPlay) {
            try {
                playTrack(curPosition);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else if (item.getItemId()==R.id.audioDetails) {
            Toast.makeText(getContext(), "Details coming soon!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId()==R.id.audioEdit) {
            Toast.makeText(getContext(), "Edit", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId()==R.id.audioDelete)
            Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
        else
            return false;

        return false;
    }
}
