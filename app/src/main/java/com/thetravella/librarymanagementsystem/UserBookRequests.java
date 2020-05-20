package com.thetravella.librarymanagementsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;
import com.thetravella.librarymanagementsystem.BookRequests.BookRequest;
import com.thetravella.librarymanagementsystem.BookRequests.RequestsAdapter;
import com.thetravella.librarymanagementsystem.BookRequests.UserBookRequestsAdapter;
import com.thetravella.librarymanagementsystem.FirebaseFunctions.CoreFunctions;

import java.util.HashMap;

public class UserBookRequests extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserBookRequestsAdapter adapter;
    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private CollectionReference BookRequests = Fdb.collection("Requests");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String USER_EMAIL;
    private int REQUEST_TYPE;
    private Query query;
    private CoreFunctions coreFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_book_requests);
        recyclerView = findViewById(R.id.myRequestsRecyclerview);

        FirebaseUser UserAuth = mAuth.getCurrentUser();
        if( UserAuth != null ){//If user is already Logged In
            USER_EMAIL = UserAuth.getEmail();
        } else {
            startActivity(new Intent(UserBookRequests.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

        if( savedInstanceState == null ){
            Bundle extras = getIntent().getExtras();
            if( extras == null ){
                startActivity(new Intent(UserBookRequests.this, Home.class));
                finish();
            } else{
                REQUEST_TYPE = extras.getInt("Request");
                if(REQUEST_TYPE == 1) {
                    loadBookRequests(USER_EMAIL);
                    this.setTitle("My Requests");
                } else if(REQUEST_TYPE == 9) {
                    this.setTitle("Over Dues");
                    loadOverDues(USER_EMAIL);
                } else if(REQUEST_TYPE == 4) {
                    this.setTitle("All Over Dues");
                    loadallOverDues();
                }
            }
        } else {
            REQUEST_TYPE = (int)savedInstanceState.getSerializable("Request");
            if(REQUEST_TYPE == 1) {
                loadBookRequests(USER_EMAIL);
                this.setTitle("My Requests");
            } else if(REQUEST_TYPE == 9) {
                this.setTitle("Over Dues");
                loadOverDues(USER_EMAIL);
            } else if(REQUEST_TYPE == 4) {
                this.setTitle("All Over Dues");
                loadallOverDues();
            }
        }

    }

    private void loadallOverDues() {
        coreFunctions = new CoreFunctions();

        query = BookRequests
                .whereLessThanOrEqualTo("return_date", coreFunctions.getDateToday())
                .whereEqualTo("confirm_status", 8)
                .whereEqualTo("return_status", 0);

        FirestoreRecyclerOptions<BookRequest> books = new FirestoreRecyclerOptions.Builder<BookRequest>()
                .setQuery(query, BookRequest.class)
                .build();

        adapter = new UserBookRequestsAdapter(books);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadOverDues(String emailAddress){
        coreFunctions = new CoreFunctions();

        query = BookRequests.whereEqualTo("user", emailAddress)
                .whereLessThanOrEqualTo("return_date", coreFunctions.getDateToday())
                .whereEqualTo("confirm_status", 8)
                .whereEqualTo("return_status", 0);

        FirestoreRecyclerOptions<BookRequest> books = new FirestoreRecyclerOptions.Builder<BookRequest>()
                .setQuery(query, BookRequest.class)
                .build();

        adapter = new UserBookRequestsAdapter(books);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadBookRequests(String emailAddress) {
        query = BookRequests.whereEqualTo("user", emailAddress)
                .orderBy("confirm_status", Query.Direction.ASCENDING)
                .whereEqualTo("return_status", 0);

        FirestoreRecyclerOptions<BookRequest> books = new FirestoreRecyclerOptions.Builder<BookRequest>()
                .setQuery(query, BookRequest.class)
                .build();

        adapter = new UserBookRequestsAdapter(books);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UserBookRequests.this, Home.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
