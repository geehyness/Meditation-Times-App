package com.hw.hlcmt.JavaActivities.MeditationTimes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.hw.hlcmt.JavaActivities.Dashboard.HomeActivity;
import com.hw.hlcmt.JavaActivities.Dashboard.Items.MeditationTimesActivity;
import com.hw.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.hw.hlcmt.JavaRepositories.Fixed.Language;
import com.hw.hlcmt.JavaRepositories.Models.MessageModel;
import com.hw.hlcmt.JavaRepositories.UIElements.MyProgressDialog;
import com.hw.hlcmt.JavaRepositories.Models.UserModel;
import com.hw.hlcmt.MainActivity;
import com.hw.hlcmt.R;

import java.text.DateFormat;
import java.util.Calendar;

public class WriteMeditationTimes extends AppCompatActivity {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private EditText titleUI;
    private EditText weekUI;
    private EditText msgUI;
    private EditText yearUI;
    private Spinner langUI;

    private UserModel currentUser;
    private String msgAuthor = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_meditation_times);

        Intent i = getIntent();
        String userJSON = i.getStringExtra(MainActivity.LOGGED_IN_USER);
        currentUser = (new Gson()).fromJson(userJSON, UserModel.class);

        titleUI = findViewById(R.id.txtTitleMT);
        weekUI = findViewById(R.id.txtMTWeek);
        yearUI = findViewById(R.id.txtMsgYear);
        msgUI = findViewById(R.id.txtMessageMT);
        langUI = findViewById(R.id.spLanguage);

        Calendar c = Calendar.getInstance();
        yearUI.setText((String.valueOf(c.get(Calendar.YEAR))));

        MessageModel message = (new Gson()).fromJson(getIntent()
                                .getStringExtra(MeditationTimesActivity.MESSAGE_JSON), MessageModel.class);
        if (message!= null){
            populateViews(message);
            msgAuthor = message.getAuthor();
        } else {
            msgAuthor = currentUser.getName();
        }

        msgUI.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (msgUI.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        String[] lang = {Language.English.toString(), Language.Siswati.toString()};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lang);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langUI.setAdapter(adapter);

        Button post = findViewById(R.id.btnPostMT);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Discard message?")
            .setMessage("When you go back any unsaved changes will be lost. Do you wish to continue?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(WriteMeditationTimes.this, HomeActivity.class)
                            .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                    finish();
                }

            })
            .setNegativeButton("No", null)
            .show();
    }

    void post(){
        Calendar c = Calendar.getInstance();
        final String title = titleUI.getText().toString();
        final String strDate = DateFormat.getDateInstance().format(c.getTime());
        final String week = weekUI.getText().toString();
        final String year = yearUI.getText().toString();
        final String language = langUI.getSelectedItem().toString();
        final String message = msgUI.getText().toString();

        int tempPic = 0;
        if (language.equals(Language.English.toString())){
            tempPic = R.drawable.lang_en;
        } else if (language.equals(Language.Siswati.toString())){
            tempPic = R.drawable.lang_ss;
        }
        final int pic = tempPic;

        if (TextUtils.isEmpty(title)){
            Toast.makeText(WriteMeditationTimes.this, "Title cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strDate)){
            Toast.makeText(WriteMeditationTimes.this, "Week cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(message)){
            Toast.makeText(WriteMeditationTimes.this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(week)){
            Toast.makeText(WriteMeditationTimes.this, "Week cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(year)){
            Toast.makeText(WriteMeditationTimes.this, "Year cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Toast.makeText(WriteMeditationTimes.this, "To be implemented!", Toast.LENGTH_SHORT).show();

        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final MessageModel msg = new MessageModel(title, message, msgAuthor, strDate, Integer.valueOf(week), Integer.valueOf(year), pic, language);
        msg.setLastEditor(currentUser.getName());

        final FirebaseFirestore ff = FirebaseFirestore.getInstance();

        new AlertDialog.Builder(WriteMeditationTimes.this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Post new Meditation Times?")
                .setMessage("If a post exists for the selected week and language, it will be overwritten. Do you wish to continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ff.collection(CollectionName.Messages).document(year+"week"+week+language).set(msg).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(WriteMeditationTimes.this, "Message posted successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(WriteMeditationTimes.this, MeditationTimesActivity.class)
                                        .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(WriteMeditationTimes.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                })
                .setNegativeButton("No", null)
                .show();
        }

    private void populateViews(MessageModel message){
        titleUI.setText(message.getTitle());
        weekUI.setText(String.valueOf(message.getWeek()));
        yearUI.setText(String.valueOf(message.getYear()));
        if(message.getLanguage().equals(Language.English)){
            langUI.setSelection(0);
        } else {
            langUI.setSelection(1);
        }
        msgUI.setText(message.getMessage());
    }
}
