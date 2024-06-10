package com.example.thejournalapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddJournalActivity extends AppCompatActivity {
    // widgets
    private ImageView addPhotoBtn, postJournalImg;
    private TextView postTimeAdded;
    private EditText postTitle, postDescription;
    private ProgressBar progressBar;
    private Button postSaveJournalBtn;

    // firebase firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Journal");

    // firebase storage
    private StorageReference storageReference;

    // firebase auth
    private String userId, userName;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    // Using Activity Result Launcher de lay hinh anh trong thu vien may ao
    private ActivityResultLauncher<String> mTakePhoto;
    private Uri imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_journal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addPhotoBtn = findViewById(R.id.post_journal_camera_btn);
        postJournalImg = findViewById(R.id.post_journal_img);

        postTimeAdded = findViewById(R.id.post_time_added);
        Timestamp timestamp = Timestamp.now();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        String time = simpleDateFormat.toString();
        postTimeAdded.setText(time);

        postTitle = findViewById(R.id.post_journal_title);
        postDescription = findViewById(R.id.post_journal_description);
        progressBar = findViewById(R.id.post_progressBar);
        postSaveJournalBtn = findViewById(R.id.post_save_journal_btn);

        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userName = currentUser.getDisplayName();
        }

        mTakePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                imgUrl = o;
                Log.e("imgurl:", String.valueOf(imgUrl));
                postJournalImg.setImageURI(o);
            }
        });
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTakePhoto.launch("image/*");
            }
        });

        postSaveJournalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postSaveJournal();
            }
        });
    }

    private void postSaveJournal() {
        String title = postTitle.getText().toString().trim();
        String description = postDescription.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && imgUrl != null) {
            // the saving path of the images in Firebase Storage:
            // ........./journal_images/my_image_202310071430.png
            final StorageReference filePhotoPath = storageReference.child("journal_images").child("my_images" + Timestamp.now().getSeconds());

            // uploading the img
            filePhotoPath.putFile(imgUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePhotoPath.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imgUrl = uri.toString();
                                            // Creating a Journal Object
                                            Journal newJournal = new Journal();
                                            newJournal.setTitle(title);
                                            newJournal.setDescription(description);
                                            newJournal.setImgUrl(imgUrl);
                                            newJournal.setTimeAdded(new Timestamp(new Date()));
                                            newJournal.setUserId(userId);
                                            newJournal.setUserName(userName);

                                            collectionReference.add(newJournal)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Intent intent = new Intent(AddJournalActivity.this, JournalListActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("error in add new journal to firestore", Objects.requireNonNull(e.getMessage()));
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("error in download url from storage", Objects.requireNonNull(e.getMessage()));
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("error in put img url to storage", Objects.requireNonNull(e.getMessage()));
                        }
                    });
        }
    }
}