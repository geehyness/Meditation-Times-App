package com.yukisoft.hlcmt.JavaActivities.AudioMessages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.AudioMSGActivity;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.AudioAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Adapters.AudioCollectionAdapter;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioCollectionModel;
import com.yukisoft.hlcmt.R;

import java.io.IOException;

public class AudioCollectionManagementActivity extends AppCompatActivity {

    private CollectionReference messages = FirebaseFirestore.getInstance().collection(CollectionName.Audio);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_collection_management);
    }
}
