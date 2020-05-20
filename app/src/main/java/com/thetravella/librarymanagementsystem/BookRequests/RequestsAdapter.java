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

public class RequestsAdapter extends FirestoreRecyclerAdapter<BookRequest, RequestsAdapter.RequestHolder> {

    private onRequestConfirmation ConfirmListener;
    private onRequestDecline DeclineListener;

    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private CollectionReference BooksDbRef = Fdb.collection("Books");

    public RequestsAdapter(@NonNull FirestoreRecyclerOptions<BookRequest> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final RequestHolder holder, int i, @NonNull final BookRequest book) {
        BooksDbRef.document(book.getBook())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            Book mBook = documentSnapshot.toObject(Book.class);
                            holder.txtBookTitle.setText(mBook.getTitle());
                            holder.txtRequestedBy.setText( "Requested By " + book.getUser());
                        }
                    }
                });
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_book_request,
                parent, false);
        return new RequestHolder(v);
    }

    class RequestHolder extends RecyclerView.ViewHolder {
        TextView txtBookTitle, txtRequestedBy;
        Button btnAccept, btnDecline;
        public RequestHolder(@NonNull View itemView) {
            super(itemView);
            txtBookTitle = itemView.findViewById(R.id.bookTitle);
            txtRequestedBy = itemView.findViewById(R.id.txtRequestedBy);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && ConfirmListener != null ){
                        ConfirmListener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && DeclineListener != null ){
                        DeclineListener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    public interface onRequestConfirmation{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void onRequestConfirmListener(onRequestConfirmation listener) {
        this.ConfirmListener = listener;
    }

    public interface onRequestDecline{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void onRequestDeclineListener(onRequestDecline listener) {
        this.DeclineListener = listener;
    }

}
