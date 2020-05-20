package com.thetravella.librarymanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.thetravella.librarymanagementsystem.BookRequests.BookRequest;
import com.thetravella.librarymanagementsystem.Books.Book;
import com.thetravella.librarymanagementsystem.Books.BookAdapterSmall;
import com.thetravella.librarymanagementsystem.Books.BooksAdapter;
import com.thetravella.librarymanagementsystem.FirebaseFunctions.CoreFunctions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookReview extends AppCompatActivity implements View.OnClickListener {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String BOOK_NAME, ABOUT, COVER, CATEGORY, AUTHOR_ID, BOOK_ID;
    private ImageView bookCover;
    private TextView txtBookname, txtAboutTheBook;
    private Button btnReserveBook;

    private RecyclerView recyclerView;
    private BookAdapterSmall booksAdapter;
    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private CollectionReference BooksDbRef = Fdb.collection("Books");
    private CollectionReference BookRequests = Fdb.collection("Requests");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String USER_EMAIL;
    private static final int NOT_RETURNED = 0;
    private static final int NOT_CONFIRMED = 0;
    private static final String CONFIRMED_BY = "none";

    private ProgressDialog dialog;
    private CoreFunctions coreFunctions = new CoreFunctions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_review);
        bookCover = findViewById(R.id.bookCover);
        txtBookname = findViewById(R.id.bookName);
        recyclerView = findViewById(R.id.smallBooksRecyclerview);
        txtAboutTheBook = findViewById(R.id.txtAboutThisBook);
        btnReserveBook = findViewById(R.id.btnReserve);
        dialog = new ProgressDialog(this);
        dialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        dialog.setIndeterminate(true);
        checkAuth();

        if( savedInstanceState == null ){
            Bundle extras = getIntent().getExtras();
            if( extras == null ){
                startActivity(new Intent(BookReview.this, Home.class));
                finish();
            } else{
                BOOK_NAME = extras.getString("title");
                CATEGORY = extras.getString("category");
                COVER = extras.getString("image");
                AUTHOR_ID = extras.getString("author");
                ABOUT = extras.getString("about");
                BOOK_ID = extras.getString("id");
                Picasso.get().load(COVER).placeholder(R.drawable.books).into(bookCover);
                txtBookname.setText(BOOK_NAME);
                txtAboutTheBook.setText(ABOUT);
                loadSimilarBooks();
            }
        } else {
            BOOK_NAME = (String)savedInstanceState.getSerializable("title");
            CATEGORY = (String)savedInstanceState.getSerializable("category");
            COVER = (String)savedInstanceState.getSerializable("image");
            AUTHOR_ID = (String)savedInstanceState.getSerializable("author");
            ABOUT = (String)savedInstanceState.getSerializable("about");
            BOOK_ID = (String)savedInstanceState.getSerializable("id");
            Picasso.get().load(COVER).placeholder(R.drawable.books).into(bookCover);
            txtBookname.setText(BOOK_NAME);
            txtAboutTheBook.setText(ABOUT);
            loadSimilarBooks();
        }

        final androidx.appcompat.widget.Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorPrimaryDark));
        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.colorPrimaryDark));
        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.mAppbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if(scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if( scrollRange + i == 0){
                    collapsingToolbarLayout.setTitle(BOOK_NAME);
                } else if ( scrollRange + i >0 ){
                    collapsingToolbarLayout.setTitle("");
                }

            }
        });

        btnReserveBook.setOnClickListener(this);
    }

    private void checkAuth() {
        FirebaseUser UserAuth = mAuth.getCurrentUser();
        if( UserAuth != null ){//If user is already Logged In
            USER_EMAIL = UserAuth.getEmail();
        } else {
            startActivity(new Intent(BookReview.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    private void createbookRequest() {
        dialog.setMessage("Processing Request...");
        dialog.show();

        String dateToday = coreFunctions.getDateToday();

        final BookRequest bookRequest = new BookRequest(USER_EMAIL, BOOK_ID, NOT_RETURNED, NOT_CONFIRMED,
                CONFIRMED_BY, dateToday, dateToday, dateToday, dateToday);

        //Check If This book was borrowed by this person and hasn't returned it
        BookRequests.whereEqualTo("book", BOOK_ID).whereEqualTo("user", USER_EMAIL)
                .whereEqualTo("return_status", 0)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        dialog.dismiss();
                        if(queryDocumentSnapshots.isEmpty()) {
                            BookRequests.add(bookRequest)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            startActivity(new Intent(BookReview.this, UserBookRequests.class)
                                                    .putExtra("Request", 1)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            Toast.makeText(BookReview.this, "Success, Your Request Has Been Sent!!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(BookReview.this, "Sorry, You Borrowed This Book And It Seems You Haven't Returned It Yet", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(BookReview.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadSimilarBooks() {
        Query query = BooksDbRef.whereEqualTo("category", CATEGORY).whereEqualTo("author", AUTHOR_ID).orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Book> books = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();
        booksAdapter = new BookAdapterSmall(books);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(booksAdapter);

        booksAdapter.onSmallBookSelected(new BookAdapterSmall.onSmallBookSelected() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Book book = documentSnapshot.toObject(Book.class);
                startActivity(new Intent(BookReview.this, BookReview.class)
                        .putExtra("title", book.getTitle())
                        .putExtra("category", book.getCategory())
                        .putExtra("author", book.getAuthor())
                        .putExtra("image", book.getCover())
                        .putExtra("about", book.getDescription())
                        .putExtra("id", documentSnapshot.getId()));
                finish();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        booksAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // booksAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(BookReview.this, Home.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnReserveBook)) {
            createbookRequest();
        }
    }

}
