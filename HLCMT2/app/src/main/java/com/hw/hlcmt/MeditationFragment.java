package com.hw.hlcmt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hw.hlcmt.JavaRepositories.CollectionName;
import com.hw.hlcmt.JavaRepositories.MTAdapter;
import com.hw.hlcmt.JavaRepositories.MessageModel;
import com.hw.hlcmt.JavaRepositories.UserModel;
import com.hw.hlcmt.JavaRepositories.UserType;

import java.util.ArrayList;
import java.util.HashSet;

public class MeditationFragment extends Fragment {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton btnAddMT;

    private ArrayList<MessageModel> MTList = new ArrayList<>();
    private HashSet<MessageModel> set = new HashSet<>();
    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.Messages);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_meditation, container, false);
        btnAddMT = v.findViewById(R.id.btnAddMT);
        btnAddMT.hide();

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

                        // If String is not in set, add it to the list and the set.
                        if (!set.contains(tempMsg)) {
                            MTList.add(tempMsg);
                            set.add(tempMsg);
                        }
                    }

                    mRecyclerView = v.findViewById(R.id.recyclerView);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(getContext());
                    mAdapter = new MTAdapter(MTList);

                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });

        getUser();

        btnAddMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), WriteMeditationTimes.class);
                startActivity(i);
            }
        });

        return v;
    }

    private void getUser(){
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        final String loginId = fbAuth.getUid();

        final DocumentReference user = ff.document(CollectionName.User+"/"+loginId);
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);

                if(userModel != null){
                    if(userModel.getUserType().equals(UserType.ADMIN) ||
                            userModel.getUserType().equals(UserType.WRITER)){
                        btnAddMT.show();
                    }
                }
            }
        });
    }
}
