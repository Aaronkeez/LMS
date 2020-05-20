package com.thetravella.librarymanagementsystem.FirebaseFunctions;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thetravella.librarymanagementsystem.R;

public class AuthorBooksFunctions {
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference authors = db.collection("Books");
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private ProgressDialog progressDialog;

    public AuthorBooksFunctions(Context context) {
        this.mContext = context;
        progressDialog = new ProgressDialog(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminate(true);
    }

    //Create A New Book

}
