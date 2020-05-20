package com.thetravella.librarymanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thetravella.librarymanagementsystem.Books.Book;
import com.thetravella.librarymanagementsystem.Books.BooksAdapter;
import com.thetravella.librarymanagementsystem.Categories.BookCategory;
import com.thetravella.librarymanagementsystem.Categories.CategoriesAdapter;

public class AuthorDetails extends AppCompatActivity implements View.OnClickListener {
    private String FIRST_NAME, AUTHOR_ID;
    private Dialog dialog;
    private RecyclerView bookCategoriesRecyclerview;
    private FloatingActionButton fab;

    // Book Categories Dialog Recyclerview
    private CategoriesAdapter adapter;
    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private CollectionReference CategoriesRef = Fdb.collection("Categories");

    // Author Books Recyclerview
    private RecyclerView authorBooksRecyclerview;
    private BooksAdapter booksAdapter;
    private CollectionReference BooksDbRef = Fdb.collection("Books");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_details);
        authorBooksRecyclerview = findViewById(R.id.authorBooksRecyclerView);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_category);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window1 = dialog.getWindow();
        WindowManager.LayoutParams wlp1 = window1.getAttributes();
        wlp1.gravity = Gravity.BOTTOM;
        wlp1.height = 5000;
        window1.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window1.setAttributes(wlp1);

        bookCategoriesRecyclerview = dialog.findViewById(R.id.dialogBooksCategoriesRecyclerview);
        fab = findViewById(R.id.fabAdd_new_book);

        //Get Author Details
        if( savedInstanceState == null ){
            Bundle extras = getIntent().getExtras();
            if( extras == null ){
                startActivity(new Intent(AuthorDetails.this, Authors.class));
                finish();
            } else{
                FIRST_NAME = extras.getString("FirstName");
                AUTHOR_ID = extras.getString("Id");
                setTitle("Books By " + FIRST_NAME);
            }
        } else {
            FIRST_NAME = (String)savedInstanceState.getSerializable("FirstName");
            AUTHOR_ID = (String)savedInstanceState.getSerializable("Id");
            setTitle("Books By " + FIRST_NAME);
        }

        //Load Category Options
        loadCategories();
        loadAuthorBooks();
        fab.setOnClickListener(this);
    }

    private void loadAuthorBooks() {
        Query query = BooksDbRef.whereEqualTo("author", AUTHOR_ID).orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Book> books = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();
        booksAdapter = new BooksAdapter(books);
        authorBooksRecyclerview.setHasFixedSize(true);
        authorBooksRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        authorBooksRecyclerview.setAdapter(booksAdapter);

    }

    private void loadCategories(){
        Query query = CategoriesRef.orderBy("category", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<BookCategory> options = new FirestoreRecyclerOptions.Builder<BookCategory>()
                .setQuery(query, BookCategory.class)
                .build();
        adapter = new CategoriesAdapter(options);
        bookCategoriesRecyclerview.setHasFixedSize(true);
        bookCategoriesRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        bookCategoriesRecyclerview.setAdapter(adapter);

        adapter.onCategoryClickListener(new CategoriesAdapter.onCategoryItemclick() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                BookCategory selectedCategory = documentSnapshot.toObject(BookCategory.class);
                startActivity(new Intent(AuthorDetails.this, CreateNewBook.class)
                .putExtra("AuthorId", AUTHOR_ID)
                .putExtra("CategoryId", documentSnapshot.getId())
                .putExtra("CategoryName", selectedCategory.getCategory())
                .putExtra("AuthorName", FIRST_NAME));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        booksAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
        booksAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        booksAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AuthorDetails.this, Authors.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.author_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_create_new_book:
                // Create New Book Function
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(fab)){
            dialog.show();
        }
    }
}
