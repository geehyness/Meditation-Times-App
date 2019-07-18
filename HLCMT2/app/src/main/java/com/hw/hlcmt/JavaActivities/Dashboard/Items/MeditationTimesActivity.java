package com.hw.hlcmt.JavaActivities.Dashboard.Items;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
    private UserModel currentUser;

    private RecyclerView mtRecyclerView;
    public MTAdapter mtAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;
    private FloatingActionButton btnAddMT;

    private Spinner yearPicker;
    private ImageView btnSearch;
    private EditText txtSearch;

    private ArrayList<MessageModel> MTList = new ArrayList<>();
    private ArrayList<MessageModel> displayMTList = new ArrayList<>();
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

        initViews();

        messages.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot msg : queryDocumentSnapshots){
                        MessageModel tempMsg = msg.toObject(MessageModel.class);
                        tempMsg.setMsgId(msg.getId());

                        boolean exists = false;

                        for (MessageModel m : displayMTList)
                            if(m.getMsgId().equals(tempMsg.getMsgId()))
                                exists = true;

                        if(!exists) {
                            if (currentUser.isEnglish() && tempMsg.getLanguage().equals(Language.English.toString())){
                                displayMTList.add(tempMsg);
                                MTList.add(tempMsg);
                            }
                            if (currentUser.isSiswati() && tempMsg.getLanguage().equals(Language.Siswati.toString())){
                                displayMTList.add(tempMsg);
                                MTList.add(tempMsg);
                            }
                        }
                    }
                }
                Collections.sort(displayMTList, new MTComparator());
                Collections.sort(MTList, new MTComparator());
                initYearPicker();
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

        yearPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getYearMessages();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("year picker", "Nothing selected");
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MeditationTimesActivity.this, HomeActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
        finish();
    }

    private void search(){
        String input = txtSearch.getText().toString();
        displayMTList.clear();

        if (input.isEmpty()){
            getYearMessages();
        } else {
            for (MessageModel m : MTList){
                if (m.getTitle().toLowerCase().contains(input) ||
                        m.getMessage().toLowerCase().contains(input)){
                    displayMTList.add(m);
                    Log.d("search", String.valueOf(m.getTitle()));
                }
            }

            mtAdapter.notifyDataSetChanged();
        }
    }

    private void getYearMessages(){
        displayMTList.clear();
        String year = yearPicker.getSelectedItem().toString();

        for (int a = 0; a < MTList.size(); a++){
            if (String.valueOf(MTList.get(a).getYear()).equals(year)){
                displayMTList.add(MTList.get(a));
                Log.d("year picker", String.valueOf(a));
            }
        }

        Log.d("year picker", year);
        mtAdapter.notifyDataSetChanged();
    }

    private void initYearPicker(){
        ArrayList<String> years = new ArrayList<>();

        for (MessageModel m : MTList) {
            boolean exists = false;

            for (String y : years){
                if ((String.valueOf(m.getYear()).equals(y))){
                    exists = true;
                }
            }

            if (!exists) {
                years.add(String.valueOf(m.getYear()));
            }
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearPicker.setAdapter(adapter);
    }

    private void initViews(){
        // SEARCH OPTIONS AND YEAR PICKER
        yearPicker = findViewById(R.id.spYear);
        txtSearch = findViewById(R.id.txtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        // RECYCLER VIEW SETUP
        mtRecyclerView = findViewById(R.id.MTRecyclerView);
        mtRecyclerView.setHasFixedSize(true);
        mtLayoutManager = new LinearLayoutManager(this);
        mtAdapter = new MTAdapter(displayMTList);

        // RECYCLER VIEW FINAL SETUP AND CLICK LISTENER
        mtRecyclerView.setLayoutManager(mtLayoutManager);
        mtRecyclerView.setAdapter(mtAdapter);
        mtAdapter.setOnItemClickListener(new MTAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MessageModel message = displayMTList.get(position);
                String messageJSON = (new Gson()).toJson(message);
                String userJSON = (new Gson()).toJson(currentUser);

                Log.d("MTItem", "Item - "  + messageJSON);

                Intent i = new Intent(MeditationTimesActivity.this, ViewMeditationTimes.class);
                i.putExtra(MESSAGE_JSON, messageJSON);
                i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
                startActivity(i);
                finish();
            }
        });
    }
}
