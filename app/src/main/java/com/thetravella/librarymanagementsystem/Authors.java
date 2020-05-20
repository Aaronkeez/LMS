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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thetravella.librarymanagementsystem.BookAuthors.Author;
import com.thetravella.librarymanagementsystem.BookAuthors.AuthorsAdapter;
import com.thetravella.librarymanagementsystem.FirebaseFunctions.AuthorFunctions;

public class Authors extends AppCompatActivity implements View.OnClickListener {
    private Dialog authorDialog;
    private AuthorFunctions authFx;

    //Params
    private EditText txtFirstName, txtLasName, txtBio;
    private Button btnAddAuthor;

    //Fb
    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private AuthorsAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authors);
        authFx = new AuthorFunctions(this);
        recyclerView = findViewById(R.id.authorsRecyclerview);

        authorDialog = new Dialog(this);
        authorDialog.setContentView(R.layout.dialog_author);
        authorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window1 = authorDialog.getWindow();
        WindowManager.LayoutParams wlp1 = window1.getAttributes();
        wlp1.gravity = Gravity.BOTTOM;
        window1.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window1.setAttributes(wlp1);

        txtFirstName = authorDialog.findViewById(R.id.txtFirstName);
        txtLasName = authorDialog.findViewById(R.id.txtLastName);
        txtBio = authorDialog.findViewById(R.id.txtBio);
        btnAddAuthor = authorDialog.findViewById(R.id.btnCreateAuthor);

        btnAddAuthor.setOnClickListener(this);
        loadAuthors();
    }

    private void loadAuthors(){
        CollectionReference authorsRef = Fdb.collection("Authors");
        Query query = authorsRef.orderBy("firstname", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Author> options = new FirestoreRecyclerOptions.Builder<Author>()
                .setQuery(query, Author.class)
                .build();

        adapter = new AuthorsAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Listen To Clicks
        adapter.setOnAuthorClickListener(new AuthorsAdapter.onAuthorSelectedListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Author selectedAuthor = documentSnapshot.toObject(Author.class);

                startActivity(new Intent(Authors.this, AuthorDetails.class)
                .putExtra("FirstName", selectedAuthor.getFirstname())
                .putExtra("Id", documentSnapshot.getId()));
            }
        });
    }

    private void createAuthor(){
        String fName = txtFirstName.getText().toString().trim().toUpperCase();
        String lName = txtLasName.getText().toString().trim().toUpperCase();
        String bio = txtBio.getText().toString().trim();

        if(fName.isEmpty()){
            Toast.makeText(this, "Sorry, First Name Is Required", Toast.LENGTH_SHORT).show();
        } else if(lName.isEmpty()){
            Toast.makeText(this, "Sorry, Last Name Is Required", Toast.LENGTH_SHORT).show();
        } else if(bio.isEmpty()){
            Toast.makeText(this, "Please Add A Small Bio About the Author", Toast.LENGTH_SHORT).show();
        } else {
            Author author = new Author(fName,lName,bio);
            authFx.createAuthor(author);
            authorDialog.dismiss();
        }
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
        startActivity(new Intent(Authors.this, Authors.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.authors_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_author:
                authorDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnAddAuthor)){
            createAuthor();
        }
    }
}
