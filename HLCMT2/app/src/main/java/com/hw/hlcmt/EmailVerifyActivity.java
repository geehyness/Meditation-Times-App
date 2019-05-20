package com.hw.hlcmt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class EmailVerifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        //FirebaseAuth fa = FirebaseAuth.getInstance();

        //String id = fa.getUid();
        //String id2 = fa.getCurrentUser().getUid();
        //String email = fa.getCurrentUser().getEmail();

        //TextView tv = findViewById(R.id.textView4);
        //tv.setText("UID 1 - " + id + "\nUID2 - " + id2 + "\nEmail - " + email);
    }
}
