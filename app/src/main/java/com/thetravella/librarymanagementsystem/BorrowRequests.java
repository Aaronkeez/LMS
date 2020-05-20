package com.thetravella.librarymanagementsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.thetravella.librarymanagementsystem.BookRequests.BookRequest;
import com.thetravella.librarymanagementsystem.BookRequests.RequestsAdapter;
import com.thetravella.librarymanagementsystem.Books.Book;
import com.thetravella.librarymanagementsystem.Books.BooksAdapter;
import com.thetravella.librarymanagementsystem.FirebaseFunctions.CoreFunctions;

import java.util.HashMap;

import javax.annotation.Nullable;

public class BorrowRequests extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RequestsAdapter adapter;
    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private CollectionReference BookRequests = Fdb.collection("Requests");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String USER_EMAIL;
    private CoreFunctions coreFunctions = new CoreFunctions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrow_requests);
        recyclerView = findViewById(R.id.bookRequestsRecyclerview);
        loadBookRequests();
    }

    private void loadBookRequests() {
        Query query = BookRequests.whereEqualTo("confirm_status", 0).orderBy("request_date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<BookRequest> books = new FirestoreRecyclerOptions.Builder<BookRequest>()
                .setQuery(query, BookRequest.class)
                .build();

        adapter = new RequestsAdapter(books);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.onRequestConfirmListener(new RequestsAdapter.onRequestConfirmation() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("confirm_status", 8);
                hashMap.put("confirmed_by", USER_EMAIL);
                hashMap.put("confirm_date", coreFunctions.getDateToday());
                hashMap.put("return_date", coreFunctions.getReturnDate());

                BookRequests.document(documentSnapshot.getId()).update(hashMap);
            }
        });

        adapter.onRequestDeclineListener(new RequestsAdapter.onRequestDecline() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("confirm_status", 9);
                hashMap.put("confirmed_by", USER_EMAIL);
                hashMap.put("confirm_date", coreFunctions.getDateToday());

                BookRequests.document(documentSnapshot.getId()).update(hashMap);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        FirebaseUser UserAuth = mAuth.getCurrentUser();
        if( UserAuth != null ){//If user is already Logged In
            USER_EMAIL = UserAuth.getEmail();
        } else {
            startActivity(new Intent(BorrowRequests.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(BorrowRequests.this, Home.class));
        finish();
    }
}
