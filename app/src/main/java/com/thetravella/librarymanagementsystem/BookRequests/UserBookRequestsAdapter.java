package com.thetravella.librarymanagementsystem.BookRequests;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thetravella.librarymanagementsystem.Books.Book;
import com.thetravella.librarymanagementsystem.R;

public class UserBookRequestsAdapter extends FirestoreRecyclerAdapter<BookRequest, UserBookRequestsAdapter.UserRequestHolder> {
    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private CollectionReference BooksDbRef = Fdb.collection("Books");

    public UserBookRequestsAdapter(@NonNull FirestoreRecyclerOptions<BookRequest> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final UserRequestHolder holder, int i, @NonNull final BookRequest book) {
        BooksDbRef.document(book.getBook())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Book mBook = documentSnapshot.toObject(Book.class);
                        holder.txtBookTitle.setText(mBook.getTitle());
                        holder.txtRequestedBy.setText( "Requested By " + book.getUser());
                        int status = book.getConfirm_status();
                        if (status == 0) {
                            holder.txtStatus.setTextColor(Color.parseColor("#01579B"));
                            holder.txtStatus.setText("Pending Confirmation");
                        } else if(status == 9) {
                            holder.txtStatus.setTextColor(Color.parseColor("#FF0000"));
                            holder.txtStatus.setText("Request Cancelled On " + book.getConfirm_date());
                        } else {
                            holder.txtStatus.setTextColor(Color.parseColor("#32CD32"));
                            holder.txtStatus.setText("To Be Returned On " + book.getReturn_date());
                        }
                    }
                });
    }

    @NonNull
    @Override
    public UserRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_myrequests,
                parent, false);
        return new UserRequestHolder(v);
    }

    class UserRequestHolder extends RecyclerView.ViewHolder{
        TextView txtBookTitle, txtRequestedBy, txtStatus;
        public UserRequestHolder(@NonNull View itemView) {
            super(itemView);
            txtBookTitle = itemView.findViewById(R.id.bookTitle);
            txtRequestedBy = itemView.findViewById(R.id.txtRequestedBy);
            txtStatus = itemView.findViewById(R.id.txtRequestStatus);
        }
    }

}
