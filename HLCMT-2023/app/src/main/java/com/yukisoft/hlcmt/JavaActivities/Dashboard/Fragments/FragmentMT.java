package com.yukisoft.hlcmt.JavaActivities.Dashboard.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.MeditationTimesActivity;
import com.yukisoft.hlcmt.JavaActivities.MeditationTimes.ViewMeditationTimes;
import com.yukisoft.hlcmt.JavaActivities.MeditationTimes.WriteMeditationTimes;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.MTAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Comparators.MTComparator;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.Language;
import com.yukisoft.hlcmt.JavaRepositories.Models.MessageModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentMT extends Fragment {
    private UserModel currentUser;

    private RecyclerView mtRecyclerView;
    public MTAdapter mtAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;
    private FloatingActionButton btnAddMT;

    private Spinner yearPicker;
    private EditText txtSearch;

    private ArrayList<MessageModel> MTList = new ArrayList<>();
    private ArrayList<MessageModel> displayMTList = new ArrayList<>();
    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.Messages);
    public static final String MESSAGE_JSON = "MessageModel";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mt, container, false);

        btnAddMT = v.findViewById(R.id.btnAddAudio);
        btnAddMT.hide();

        Intent i = getActivity().getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        if(currentUser.isWriter())
            btnAddMT.show();

        initViews(v);

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
                initYearPicker(v);
                mtAdapter.notifyDataSetChanged();
            }
        });

        btnAddMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), WriteMeditationTimes.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser));
                startActivity(i);
                //finish();
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

        return v;
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

    private void initYearPicker(View v){
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
        adapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearPicker.setAdapter(adapter);
    }

    private void initViews(View v){
        // SEARCH OPTIONS AND YEAR PICKER
        yearPicker = v.findViewById(R.id.spYear);
        txtSearch = v.findViewById(R.id.txtSearchMT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtSearch.setFocusedByDefault(false);
        }

        // RECYCLER VIEW SETUP
        mtRecyclerView = v.findViewById(R.id.audioRecyclerView);
        mtRecyclerView.setHasFixedSize(true);
        mtLayoutManager = new LinearLayoutManager(v.getContext());
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

                Intent i = new Intent(v.getContext(), ViewMeditationTimes.class);
                i.putExtra(MESSAGE_JSON, messageJSON);
                i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
                startActivity(i);
                //finish();
            }
        });
    }
}
