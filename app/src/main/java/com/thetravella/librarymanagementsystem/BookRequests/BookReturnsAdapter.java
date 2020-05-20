package com.thetravella.librarymanagementsystem.BookRequests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thetravella.librarymanagementsystem.Books.Book;
import com.thetravella.librarymanagementsystem.R;

public class BookReturnsAdapter extends FirestoreRecyclerAdapter<BookRequest, BookReturnsAdapter.ReturnHolder> {
    private onBookReturnSelect Listener;

    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private CollectionReference BooksDbRef = Fdb.collection("Books");

    public BookReturnsAdapter(@NonNull FirestoreRecyclerOptions<BookRequest> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ReturnHolder holder, int i, @NonNull final BookRequest book) {
        BooksDbRef.document(book.getBook())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            Book mBook = documentSnapshot.toObject(Book.class);
                            holder.txtBookName.setText(mBook.getTitle());
                            holder.txtBorrowerName.setText(book.getUser());
                            holder.txtReturnDate.setText("Return Date: " + book.getReturn_date());
                        }
                    }
                });
    }

    @NonNull
    @Override
    public ReturnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_book_return,
                parent, false);
        return new ReturnHolder(v);
    }

    class ReturnHolder extends RecyclerView.ViewHolder {
        TextView txtBookName, txtBorrowerName, txtReturnDate;
        Button btnConfirm;

        public ReturnHolder(@NonNull View itemView) {
            super(itemView);
            txtBookName = itemView.findViewById(R.id.bookTitle);
            txtBorrowerName = itemView.findViewById(R.id.txtRequestedBy);
            txtReturnDate = itemView.findViewById(R.id.txtReturnDate);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && Listener != null ){
                        Listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    public interface onBookReturnSelect{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void onBookReturnClickListener (onBookReturnSelect listener) {
        this.Listener = listener;
    }

}
