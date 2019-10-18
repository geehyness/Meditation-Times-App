package com.yukisoft.hlcmt.JavaActivities.AudioMessages;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.yukisoft.hlcmt.JavaActivities.Dashboard.Items.AudioMSGActivity;
import com.yukisoft.hlcmt.JavaActivities.DatePickerFragment;
import com.yukisoft.hlcmt.JavaRepositories.Fixed.CollectionName;
import com.yukisoft.hlcmt.JavaRepositories.Models.AudioModel;
import com.yukisoft.hlcmt.JavaRepositories.Models.UserModel;
import com.yukisoft.hlcmt.MainActivity;
import com.yukisoft.hlcmt.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddAudioActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private EditText txtTitle, txtDetails, txtSpeaker, txtDate;
    private TextView fileName;
    private ImageView btnCloseFile;
    private CheckBox setTitle;
    private Button btnDatePicker;

    private final int REQ_CODE_PICK_SOUND_FILE = 1;
    private Uri audioFileUri;

    private StorageReference storageReference;
    private ProgressBar progressBarUpload;
    private StorageTask uploadTask;

    private boolean clearTitle = false;
    private Date date;

    private UserModel currentUser;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);

        currentUser = (new Gson().fromJson(getIntent().getStringExtra(MainActivity.LOGGED_IN_USER), UserModel.class));
        Log.d("user", currentUser.getName());

        storageReference = FirebaseStorage.getInstance().getReference("messages");

        txtTitle = findViewById(R.id.txtTitle);
        txtDetails = findViewById(R.id.txtAudioDetails);
        txtSpeaker = findViewById(R.id.txtSpeaker);
        txtDate = findViewById(R.id.txtDate);
        btnDatePicker = findViewById(R.id.btnPickDate);

        fileName = findViewById(R.id.lblFileName);
        btnCloseFile = findViewById(R.id.btnCloseFile);
        Button btnUpload = findViewById(R.id.btnUpload);
        progressBarUpload = findViewById(R.id.progressBar);
        setTitle = findViewById(R.id.cbSetTitle);

        btnCloseFile.setVisibility(View.GONE);

        fileName.setTextColor(getResources().getColor(R.color.colorPrimary));
        fileName.setText("Upload File");

        fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(Intent.createChooser(intent, "Pick audio file"), REQ_CODE_PICK_SOUND_FILE);
            }
        });

        btnCloseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioFileUri = null;
                fileName.setTextColor(getResources().getColor(R.color.colorPrimary));
                fileName.setText("Upload File");
                btnCloseFile.setVisibility(View.GONE);

                if (setTitle.isChecked()){
                    setTitle.setChecked(false);
                    txtTitle.setText("");
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(AddAudioActivity.this, "File upload already in progress!", Toast.LENGTH_SHORT).show();
                } else {
                    String title = txtTitle.getText().toString();
                    String details = txtDetails.getText().toString();
                    String speaker = txtSpeaker.getText().toString();


                    if(TextUtils.isEmpty(title)){
                        txtTitle.setError("Please input a message title.");
                        return;
                    } else {
                        txtTitle.setError(null);
                    }

                    if(TextUtils.isEmpty(details)){
                        txtDetails.setError("Please input message details.");
                        return;
                    } else {
                        txtTitle.setError(null);
                    }

                    if(date == null){
                        txtDate.setError("When was this message preached?");
                        return;
                    } else {
                        txtTitle.setError(null);
                    }

                    if(TextUtils.isEmpty(speaker)){
                        txtSpeaker.setError("Please input the speaker's name.");
                        return;
                    } else {
                        txtSpeaker.setText(null);
                    }

                    uploadFile(title, details, date, speaker);
                }
            }
        });

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        setTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (audioFileUri == null){
                    setTitle.setChecked(false);
                    Toast.makeText(AddAudioActivity.this, "Select file first", Toast.LENGTH_SHORT).show();
                } else {
                    if (setTitle.isChecked()){
                        txtTitle.setText(getFileName(audioFileUri));
                        clearTitle = true;
                    } else {
                        if (clearTitle)
                            txtTitle.setText("");
                    }
                }
            }
        });

        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void afterTextChanged(Editable editable) {
                clearTitle = false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_SOUND_FILE && resultCode == RESULT_OK) {
            if ((data != null) && (data.getData() != null)) {
                audioFileUri = data.getData();

                fileName.setTextColor(getResources().getColor(R.color.colorText));
                fileName.setText(getFileName(audioFileUri));

                btnCloseFile.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getFileName(Uri audioFileUri){
        String results = null;

        if (audioFileUri != null){
            if(audioFileUri.getScheme().equals("content")) {
                try (Cursor cursor = getContentResolver().query(audioFileUri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        results = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    Log.d("Filename error", e.getMessage());
                }
            }

            if(results == null){
                results = audioFileUri.getPath();
                int cut = results.lastIndexOf("/");
                if (cut != -1) results = results.substring(cut + 1);
            }
        } else {
            results = "Unable to get filename";
        }

        return results;
    }

    private String fileExtension(Uri uri){
        if (uri != null){
            ContentResolver cr = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cr.getType(uri));
        } else {
            return null;
        }
    }

    private void uploadFile(final String title, final String details, final Date date, final String speaker){
        if (audioFileUri != null){
            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + fileExtension(audioFileUri));
            uploadTask = fileRef.putFile(audioFileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBarUpload.setProgress(0);
                            }
                        }, 5000);

                        Calendar calendar = Calendar.getInstance();

                        AudioModel upload = new AudioModel(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(),
                                title, details, date, calendar.getTime(), speaker);

                        final FirebaseFirestore ff = FirebaseFirestore.getInstance();
                        ff.collection(CollectionName.Audio).document(title).set(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddAudioActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddAudioActivity.this, "Error:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }). addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddAudioActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progressBarUpload.setProgress((int) progress, true);
                        } else {
                            progressBarUpload.setProgress((int) progress);
                        }
                    }
                });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Discard Upload?")
                .setMessage("When you go back any unsaved changes will be lost. Do you wish to continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(AddAudioActivity.this, AudioMSGActivity.class)
                                .putExtra(MainActivity.LOGGED_IN_USER, (new Gson()).toJson(currentUser)));
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, date);
        String datePreached= DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        this.date = c.getTime();
        txtDate.setText(datePreached);
    }
}
