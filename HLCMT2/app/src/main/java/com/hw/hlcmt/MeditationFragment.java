package com.hw.hlcmt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaRepositories.CollectionName;
import com.hw.hlcmt.JavaRepositories.Language;
import com.hw.hlcmt.JavaRepositories.MTAdapter;
import com.hw.hlcmt.JavaRepositories.MTComparator;
import com.hw.hlcmt.JavaRepositories.MessageModel;
import com.hw.hlcmt.JavaRepositories.UserModel;
import com.hw.hlcmt.JavaRepositories.UserType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MeditationFragment extends Fragment {
    private RecyclerView mtRecyclerView;
    public MTAdapter mtAdapter;
    private RecyclerView.LayoutManager mtLayoutManager;
    private FloatingActionButton btnAddMT;
    private FloatingActionButton btnAdmin;

    private UserModel currentUser;
    private ArrayList<MessageModel> MTList = new ArrayList<>();
    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.Messages);
    public static final String MESSAGE_JSON = "MessageModel";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_meditation, container, false);
        btnAddMT = v.findViewById(R.id.btnAddMT);
        btnAdmin = v.findViewById(R.id.btnAdmin);
        btnAddMT.hide();
        btnAdmin.hide();

        Intent i = getActivity().getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        if(currentUser != null){
            if(currentUser.isAdmin() || currentUser.isWriter())
                btnAddMT.show();
            if(currentUser.isAdmin())
                btnAdmin.show();
        }

        buildRecyclerView(v);

        messages.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(getContext(), "Error While Loading! \nError - " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(getContext(), WriteMeditationTimes.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser));
                startActivity(i);
                getActivity().finish();
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), UserManagementActivity.class)
                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser));
                startActivity(i);
                getActivity().finish();
            }
        });

        return v;
    }

    private void buildRecyclerView(View v){
        mtRecyclerView = v.findViewById(R.id.MTRecyclerView);
        mtRecyclerView.setHasFixedSize(true);
        mtLayoutManager = new LinearLayoutManager(getContext());
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

                Intent i = new Intent(getContext(), ViewMeditationTimes.class);
                i.putExtra(MESSAGE_JSON, messageJSON);
                i.putExtra(MainActivity.LOGGED_IN_USER, userJSON);
                startActivity(i);
                getActivity().finish();
            }
        });
    }
}
