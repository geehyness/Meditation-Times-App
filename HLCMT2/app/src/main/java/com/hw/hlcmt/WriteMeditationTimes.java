package com.hw.hlcmt;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hw.hlcmt.JavaRepositories.CollectionName;
import com.hw.hlcmt.JavaRepositories.Language;
import com.hw.hlcmt.JavaRepositories.MessageModel;
import com.hw.hlcmt.JavaRepositories.MyProgressDialog;
import com.hw.hlcmt.JavaRepositories.UserModel;

import java.text.DateFormat;
import java.util.Calendar;

public class WriteMeditationTimes extends AppCompatActivity {
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private EditText titleUI;
    private EditText dateUI;
    private EditText msgUI;
    private EditText yearUI;
    private Spinner langUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_meditation_times);

        titleUI = findViewById(R.id.txtTitleMT);
        dateUI = findViewById(R.id.txtDateMT);
        msgUI = findViewById(R.id.txtMessageMT);
        yearUI = findViewById(R.id.txtYear);
        langUI = findViewById(R.id.spLanguage);

        Calendar c = Calendar.getInstance();
        yearUI.setText((String.valueOf(c.get(Calendar.YEAR))));

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

    void post(){
        Calendar c = Calendar.getInstance();
        final String title = titleUI.getText().toString();
        final String strDate = DateFormat.getDateInstance().format(c.getTime());
        final String week = dateUI.getText().toString();
        final String year = yearUI.getText().toString();
        final String language = langUI.getSelectedItem().toString();
        final String message = msgUI.getText().toString();

        int tempPic = 0;
        if (language.equals(Language.English)){
            tempPic = R.drawable.lang_en;
        } else if (language.equals(Language.Siswati)){
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

        final FirebaseFirestore ff = FirebaseFirestore.getInstance();
        final String loginId = fbAuth.getUid();

        final DocumentReference user = ff.document(CollectionName.User+"/"+loginId);
        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);

                if(userModel != null){
                    final MessageModel msg = new MessageModel(title, message, userModel.getName(), strDate, Integer.valueOf(week), Integer.valueOf(year), pic, language);

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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(WriteMeditationTimes.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
