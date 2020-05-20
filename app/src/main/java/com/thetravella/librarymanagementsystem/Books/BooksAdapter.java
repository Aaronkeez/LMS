package com.thetravella.librarymanagementsystem.Books;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.thetravella.librarymanagementsystem.R;

public class BooksAdapter extends FirestoreRecyclerAdapter<Book, BooksAdapter.BookHolder> {
    private onBookSelected Listener;

    public BooksAdapter(@NonNull FirestoreRecyclerOptions<Book> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookHolder holder, int i, @NonNull Book model) {
        holder.txtBookName.setText(model.getTitle());
        holder.txtBookDescription.setText(model.getDescription());
        Picasso.get().load(model.getCover()).placeholder(R.drawable.books).into(holder.bookCover);
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_author_book_item,
                parent, false);
        return new BookHolder(v);
    }

    class BookHolder extends RecyclerView.ViewHolder{
        ImageView bookCover;
        TextView txtBookName, txtBookDescription;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.bookCover);
            txtBookName = itemView.findViewById(R.id.bookTitle);
            txtBookDescription = itemView.findViewById(R.id.bookDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
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

    public interface onBookSelected{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void onBookSelectListener(onBookSelected listener) {
        this.Listener = listener;
    }
}
