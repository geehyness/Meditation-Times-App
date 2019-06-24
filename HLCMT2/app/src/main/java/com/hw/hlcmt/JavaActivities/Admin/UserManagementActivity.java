package com.hw.hlcmt.JavaActivities.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.hw.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.hw.hlcmt.JavaRepositories.Adapters.UserAdapter;
import com.hw.hlcmt.JavaRepositories.Models.UserModel;
import com.hw.hlcmt.MainActivity;
import com.hw.hlcmt.R;

import java.util.ArrayList;

public class UserManagementActivity extends AppCompatActivity {
    private RecyclerView userRecycler;
    public UserAdapter userAdapter;
    private RecyclerView.LayoutManager userLayoutManager;
    private ArrayList<UserModel> UserList = new ArrayList<>();
    private UserModel currentAdminUser;
    private CollectionReference userRef = FirebaseFirestore.getInstance().collection(CollectionName.User);

    public static final String USER_MANAGEMENT = "user manager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        Intent i = getIntent();
        currentAdminUser = (new Gson()).fromJson(i.getStringExtra(MainActivity.LOGGED_IN_USER), UserModel.class);

        buildRecyclerView();

        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Toast.makeText(UserManagementActivity.this, "Error While Loading! \nError - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (queryDocumentSnapshots != null){
                    for(DocumentSnapshot usr : queryDocumentSnapshots){
                        UserModel tempUser = usr.toObject(UserModel.class);
                        tempUser.setUserId(usr.getId());

                        boolean exists = false;

                        for (UserModel u : UserList)
                            if(u.getUserId().equals(tempUser.getUserId()))
                                exists = true;

                        if(!exists) {
                            UserList.add(tempUser);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UserManagementActivity.this, HomeActivity.class)
                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentAdminUser)));
        finish();
    }

    private void buildRecyclerView(){
        userRecycler = findViewById(R.id.userRecyclerView);
        userRecycler.setHasFixedSize(true);
        userLayoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(UserList);

        userRecycler.setLayoutManager(userLayoutManager);
        userRecycler.setAdapter(userAdapter);

        userAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UserModel user = UserList.get(position);

                if (!user.getUserId().equals(currentAdminUser.getUserId())){
                    String userJSON = (new Gson()).toJson(user);
                    String adminUserJSON = (new Gson()).toJson(currentAdminUser);

                    Intent i = new Intent(UserManagementActivity.this, UpdateUserActivity.class);
                    i.putExtra(USER_MANAGEMENT, userJSON);
                    i.putExtra(MainActivity.LOGGED_IN_USER, adminUserJSON);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(UserManagementActivity.this, "You can not modify your own permissions!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
