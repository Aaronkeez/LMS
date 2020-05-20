package com.thetravella.librarymanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thetravella.librarymanagementsystem.BookRequests.BookRequest;
import com.thetravella.librarymanagementsystem.BookRequests.BookReturnsAdapter;

import java.util.HashMap;

public class BookReturns extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private CollectionReference BookRequests = Fdb.collection("Requests");
    private BookReturnsAdapter adapter;

    private Dialog dialog;
    private Button btnConfirmReturn;
    private EditText txtRequestId;
    private TextView txtBookTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_returns);
        recyclerView = findViewById(R.id.returnsRecyclerview);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirm_return);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window1 = dialog.getWindow();
        WindowManager.LayoutParams wlp1 = window1.getAttributes();
        wlp1.gravity = Gravity.BOTTOM;
        window1.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window1.setAttributes(wlp1);

        btnConfirmReturn = dialog.findViewById(R.id.dialog_btnConfirm);
        txtBookTitle = dialog.findViewById(R.id.dialogBookTitle);
        txtRequestId = dialog.findViewById(R.id.dialog_requestId);
        btnConfirmReturn.setOnClickListener(this);

        loadReturns();
    }

    private void loadReturns() {
        Query query = BookRequests.whereEqualTo("return_status", 0)
                .whereEqualTo("confirm_status", 8)
                .orderBy("return_date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<BookRequest> books = new FirestoreRecyclerOptions.Builder<BookRequest>()
                .setQuery(query, BookRequest.class)
                .build();

        adapter = new BookReturnsAdapter(books);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.onBookReturnClickListener(new BookReturnsAdapter.onBookReturnSelect() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                BookRequest request = documentSnapshot.toObject(BookRequest.class);
                txtBookTitle.setText(request.getUser());
                txtRequestId.setText(documentSnapshot.getId());
                dialog.show();
            }
        });

    }

    private void confirmBookReturn() {
        String requestId = txtRequestId.getText().toString().trim();
        if(!requestId.isEmpty()) {
            dialog.dismiss();
            HashMap<String, Object> record = new HashMap<>();
            record.put("return_status", 1);
            BookRequests.document(requestId).update(record)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(BookReturns.this, "Success!..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
    public void onClick(View v) {
        if(v.equals(btnConfirmReturn)) {
            confirmBookReturn();
        }
    }
}
