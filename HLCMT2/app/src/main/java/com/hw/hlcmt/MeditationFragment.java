package com.hw.hlcmt;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hw.hlcmt.JavaRepositories.ExampleAdapter;
import com.hw.hlcmt.JavaRepositories.MTItem;
import com.hw.hlcmt.JavaRepositories.UserModel;

import java.util.ArrayList;

public class MeditationFragment extends Fragment {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton btnAddMT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meditation, container, false);
        btnAddMT = v.findViewById(R.id.btnAddMT);
        btnAddMT.hide();

        ArrayList<MTItem> exampleList = new ArrayList<>();
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 1", "Line 2"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 3", "Line 4"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 5", "Line 6"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 7", "Line 8"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 9", "Line 10"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 11", "Line 12"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 13", "Line 14"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 15", "Line 16"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 17", "Line 18"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 19", "Line 20"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 21", "Line 22"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 23", "Line 24"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 25", "Line 26"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 27", "Line 28"));
        exampleList.add(new MTItem(R.drawable.ic_chat, "Line 29", "Line 30"));

        mRecyclerView = v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ExampleAdapter(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        UserModel userModel = getUser();
        if(userModel != null){
            btnAddMT.show();
        }

        return v;
    }

    private UserModel getUser(){
        final UserModel[] temp = new UserModel[1];
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        final String loginId = fbAuth.getUid();

        final DocumentReference user = ff.document("User/"+loginId);
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                temp[0] = documentSnapshot.toObject(UserModel.class);
                Toast.makeText(getContext(), "Name - " + temp[0].getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return temp[0];
    }
}
