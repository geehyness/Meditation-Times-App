package com.hw.hlcmt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaRepositories.CollectionName;
import com.hw.hlcmt.JavaRepositories.InformationModel;
import com.hw.hlcmt.JavaRepositories.MyProgressDialog;
import com.hw.hlcmt.JavaRepositories.UserModel;

import java.text.DateFormat;
import java.util.Calendar;

public class SettingsFragment extends Fragment {
    private DocumentReference infoRef = FirebaseFirestore.getInstance().document(CollectionName.Information);
    private UserModel currentUser;
    private FloatingActionButton save;
    private FloatingActionButton edit;
    private TextView tvInformationUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        Intent i = getActivity().getIntent();
        currentUser = (new Gson()).fromJson(i.getStringExtra(MainActivity.LOGGED_IN_USER), UserModel.class);

        final TextView txtInformation = v.findViewById(R.id.txtInformation);
        tvInformationUpdate = v.findViewById(R.id.tvInformationUpdate);
        edit = v.findViewById(R.id.btnEditInformation);
        save = v.findViewById(R.id.btnSaveInformation);

        if (!currentUser.isAdmin()) edit.hide();
        save.hide();

        txtInformation.setEnabled(false);

        infoRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(getContext(), "Error While Loading! \nError - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null){
                    InformationModel info = documentSnapshot.toObject(InformationModel.class);

                    if (info != null) {
                        if (info.getDateUpdated() != null) {
                            txtInformation.setText(info.getInformation());
                        }

                        if (info.getInformation() != null) {
                            tvInformationUpdate.setText("Last Updated: " + info.getDateUpdated());
                        }
                    } else {
                        txtInformation.setText("Information missing!");
                        tvInformationUpdate.setText("No last update information.");
                    }
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtInformation.setEnabled(true);
                edit.hide();
                save.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                final String info = txtInformation.getText().toString();
                final String strDate = DateFormat.getDateInstance().format(c.getTime());

                if(TextUtils.isEmpty(info)){
                    Toast.makeText(getContext(), "Information is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                InformationModel infoModel = new InformationModel(strDate,info);

                infoRef.set(infoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Information updated successfully!", Toast.LENGTH_SHORT).show();
                        txtInformation.setEnabled(false);
                        edit.show();
                        save.hide();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to update Information.\n Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return v;
    }
}
