package com.yukisoft.hlcmt.JavaActivities.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.yukisoft.hlcmt.R;

public class EmailVerifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        final FirebaseAuth fa = FirebaseAuth.getInstance();

        //String id = fa.getUid();
        //String id2 = fa.getCurrentUser().getUid();
        //String email = fa.getCurrentUser().getEmail();

        //TextView tv = findViewById(R.id.textView4);
        //tv.setText("UID 1 - " + id + "\nUID2 - " + id2 + "\nEmail - " + email);

        TextView verifyInfo = findViewById(R.id.tvUserInfoVerify);

        verifyInfo.setText("When you tap the 'CONTINUE' button you will be redirected to the login page. Feel free to login and experience in full what this app has to offer.\n\nEnjoy!");

        Button confirm = findViewById(R.id.btnConfirmEmail);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fa.signOut();
                startActivity(new Intent(EmailVerifyActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
