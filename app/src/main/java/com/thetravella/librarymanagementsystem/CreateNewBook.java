package com.thetravella.librarymanagementsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.thetravella.librarymanagementsystem.Books.Book;

public class CreateNewBook extends AppCompatActivity implements View.OnClickListener {
    private String AUTHOR_ID, AUTHOR_NAME, CATEGORY_ID, CATEGORY_NAME;
    private ImageView imgBookCover;
    private EditText bookTitle, bookDescription;
    private Button btnCreateBook;

    //Working With Gallery
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri LocalimageUrl;
    private ProgressDialog progressDialog;

    // Fb
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_book);
        imgBookCover = findViewById(R.id.bookCover);
        bookTitle = findViewById(R.id.bookTitle);
        bookDescription = findViewById(R.id.Description);
        btnCreateBook = findViewById(R.id.btnCreateBook);
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        mStorage = FirebaseStorage.getInstance().getReference();

        //Get Author Details
        if( savedInstanceState == null ){
            Bundle extras = getIntent().getExtras();
            if( extras == null ){
                Toast.makeText(this, "Sorry, Missing Params!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(CreateNewBook.this, Authors.class));
                finish();
            } else{
                AUTHOR_ID = extras.getString("AuthorId");
                AUTHOR_NAME = extras.getString("AuthorName");
                CATEGORY_ID = extras.getString("CategoryId");
                CATEGORY_NAME = extras.getString("CategoryName");
                setTitle("New " + CATEGORY_NAME + " Book By " + AUTHOR_NAME);
            }
        } else {
            AUTHOR_ID = (String)savedInstanceState.getSerializable("AuthorId");
            AUTHOR_NAME = (String)savedInstanceState.getSerializable("AuthorName");
            CATEGORY_ID = (String)savedInstanceState.getSerializable("CategoryId");
            CATEGORY_NAME = (String)savedInstanceState.getSerializable("CategoryName");
            setTitle("New " + CATEGORY_NAME + " Book By " + AUTHOR_NAME);
        }
        imgBookCover.setOnClickListener(this);
        btnCreateBook.setOnClickListener(this);
    }

    private void openGallery() {
        imgBookCover.setEnabled(false);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Intent gallery = new Intent();
            gallery.setType("image/*");
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);
        } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            LocalimageUrl = data.getData();
            Picasso.get().load(LocalimageUrl).placeholder(R.drawable.upload).into(imgBookCover);
        } else if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Action Cancelled", Toast.LENGTH_SHORT).show();
        }
        imgBookCover.setEnabled(true);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void createNewBook() {
        progressDialog.setMessage("Uploading...");
        btnCreateBook.setEnabled(false);

        final String inputbookTitle = bookTitle.getText().toString().trim();
        final String inputbookDescription = bookDescription.getText().toString().trim();

        if(LocalimageUrl == null) {
            Toast.makeText(this, "Please Select Book Cover", Toast.LENGTH_SHORT).show();
            btnCreateBook.setEnabled(true);
        } else if (inputbookTitle.isEmpty()) {
            Toast.makeText(this, "All Books Must Have A Title", Toast.LENGTH_SHORT).show();
            btnCreateBook.setEnabled(true);
        } else if (inputbookDescription.isEmpty()) {
            Toast.makeText(this, "All Books Must Have A Description", Toast.LENGTH_SHORT).show();
            btnCreateBook.setEnabled(true);
        } else {
            progressDialog.show();
            String timestamp = String.valueOf(System.currentTimeMillis()/1000);
            final StorageReference filePath = mStorage.child("BookCovers/").child(timestamp +"."+ getFileExtension(LocalimageUrl));

            filePath.putFile(LocalimageUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUri = uri.toString();
                                    Book newBook = new Book(CATEGORY_ID, AUTHOR_ID, inputbookTitle, inputbookDescription, downloadUri);
                                    db.collection("Books").add(newBook)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if(task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(CreateNewBook.this, "Success", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(CreateNewBook.this, AuthorDetails.class)
                                                        .putExtra("FirstName", AUTHOR_NAME)
                                                        .putExtra("Id", AUTHOR_ID));
                                                        finish();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    btnCreateBook.setEnabled(true);
                                                    Toast.makeText(CreateNewBook.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            btnCreateBook.setEnabled(true);
                            Toast.makeText(CreateNewBook.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        }

    }

    @Override
    public void onClick(View v) {
        if(v.equals(imgBookCover)){
            openGallery();
        } else if (v.equals(btnCreateBook)) {
            createNewBook();
        }
    }

}
