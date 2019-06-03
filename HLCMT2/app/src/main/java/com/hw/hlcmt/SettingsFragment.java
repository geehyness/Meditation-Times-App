package com.hw.hlcmt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaRepositories.CollectionName;
import com.hw.hlcmt.JavaRepositories.MyProgressDialog;
import com.hw.hlcmt.JavaRepositories.UserModel;

public class SettingsFragment extends Fragment {
    private UserModel currentUser;
    private CheckBox en;
    private CheckBox ss;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        Intent i = getActivity().getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        en = v.findViewById(R.id.cbEnglish);
        ss = v.findViewById(R.id.cbSiswati);
        en.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!en.isChecked()){
                    if(!ss.isChecked()){
                        Toast.makeText(getContext(), "One language has to be selected at all times!", Toast.LENGTH_SHORT).show();
                        en.setChecked(true);
                    }
                }
            }
        });
        ss.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!ss.isChecked()){
                    if(!en.isChecked()){
                        Toast.makeText(getContext(), "One language has to be selected at all times!", Toast.LENGTH_SHORT).show();
                        ss.setChecked(true);
                    }
                }
            }
        });

        Button save = v.findViewById(R.id.btnSaveSettings);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        setSettings();

        return v;
    }

    private void saveSettings(){
        FirebaseFirestore ff = FirebaseFirestore.getInstance();

        currentUser.setEnglish(en.isChecked());
        currentUser.setSiswati(ss.isChecked());

        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        ff.collection(CollectionName.User).document(currentUser.getUserId()).set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Setting Saved", Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Restart App for changes to take effect.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSettings(){
        en.setChecked(SettingsFragment.this.currentUser.isEnglish());
        ss.setChecked(SettingsFragment.this.currentUser.isSiswati());
    }
}
