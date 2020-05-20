package com.thetravella.librarymanagementsystem.FirebaseFunctions;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.thetravella.librarymanagementsystem.BookAuthors.Author;
import com.thetravella.librarymanagementsystem.R;

public class AuthorFunctions {
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference authors = db.collection("Authors");
    private ProgressDialog progressDialog;

    public AuthorFunctions(Context context){
        mContext = context;
        progressDialog = new ProgressDialog(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminate(true);
    }

    public void createAuthor(final Author author){
        progressDialog.setMessage("Creating Author...");
        progressDialog.show();
        db.collection("Authors")
                .whereEqualTo("firstName", author.getFirstname())
                .whereEqualTo("lastName", author.getLastname())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() == 0) {
                            authors.add(author)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            progressDialog.dismiss();
                                            Toast.makeText(mContext, "Success "+ author.getFirstname() + " Was Added!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(mContext, "Sorry " + author.getFirstname() + " "+ author.getLastname() + " Already Exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
