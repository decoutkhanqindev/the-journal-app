package com.example.thejournalapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class JournalListActivity extends AppCompatActivity {
    // firebase auth
    private FirebaseAuth firebaseAuth;
//    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    // firebase firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Journal");

    // firebase storage
    private StorageReference storageReference;

    // list of journal
    private ArrayList<Journal> journalArrayList;

    // toolbar
    private Toolbar toolbar;

    // recyclerView
    private RecyclerView recyclerView;
    private JournalAdapter adapter;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_journal_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // widgets
        toolbar = findViewById(R.id.journal_list_toolbar);
        setSupportActionBar(toolbar);// Use androidx.appcompat.widget.Toolbar

        recyclerView = findViewById(R.id.journal_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        journalArrayList = new ArrayList<>();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JournalListActivity.this, AddJournalActivity.class);
                startActivity(intent);
            }
        });
    }

    // adding menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.journal_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_sign_out) {
            if (currentUser != null) {
                Intent intent = new Intent(JournalListActivity.this, MainActivity.class);
                startActivity(intent);
            }

        }
        return super.onOptionsItemSelected(item);
    }
    // start firebase db

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot journalFromDB : queryDocumentSnapshots) {
                            Journal journal = journalFromDB.toObject(Journal.class);
                            journalArrayList.add(journal);
                        }

                        adapter = new JournalAdapter(JournalListActivity.this, journalArrayList);

                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JournalListActivity.this, "Opps! Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}