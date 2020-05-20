package com.thetravella.librarymanagementsystem.FirebaseFunctions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.thetravella.librarymanagementsystem.Categories.BookCategory;
import com.thetravella.librarymanagementsystem.Home;
import com.thetravella.librarymanagementsystem.MainActivity;
import com.thetravella.librarymanagementsystem.Profiles.UserProfile;
import com.thetravella.librarymanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class FirebaseFx {
    private Context mContext;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private UserProfile userProfile;
    private FirebaseAuth mAuth;

    public FirebaseFx (Context context) {
        mContext = context;
        progressDialog = new ProgressDialog(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public Boolean isAuthenticated() {
        Boolean auth = false;
        if (mAuth.getCurrentUser() == null){
            auth =false;
        } else {
            auth = true;
        }
        return auth;
    }

    public String getCurrentUser(String EmailId) {
       return "User";
    }

    public void loginUser(String email, String password, String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            mContext.startActivity(new Intent(mContext, Home.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void createUser(String email, String password, String message, final UserProfile profile) {
        progressDialog.setMessage(message);
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser UserAuth = mAuth.getCurrentUser();
                            if( UserAuth != null ) {
                                String muserId = mAuth.getUid();

                                db.collection("Profiles").document(muserId)
                                        .set(profile)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    mContext.startActivity(new Intent(mContext, Home.class)
                                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                                progressDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                                                mContext.startActivity(new Intent(mContext, Home.class)
                                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void Logout(){
        mAuth.signOut();
        mContext.startActivity(new Intent(mContext, MainActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void resetPassword(String emailAddress){
        progressDialog.setMessage("Sending Password Reset Email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            Toast.makeText(mContext, "Success, Password reset email sent", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    //Book Category Functions
    public void createNewBookCategory(final BookCategory category){
        progressDialog.setMessage("Creating New Category...");
        progressDialog.show();
        db.collection("Categories").whereEqualTo("category", category.getCategory())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() == 0) {
                            db.collection("Categories")
                                    .add(category)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            progressDialog.dismiss();
                                            Toast.makeText(mContext, "Success " + category.getCategory() + " Was Created", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else{
                            progressDialog.dismiss();
                            Toast.makeText(mContext, "Sorry " + category.getCategory() + " Already Exists", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void DeleteBookCategory(String categoryId){
        db.collection("Categories").document(categoryId).delete();
    }

}
