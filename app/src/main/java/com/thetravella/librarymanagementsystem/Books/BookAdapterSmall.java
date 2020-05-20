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

public class BookAdapterSmall extends FirestoreRecyclerAdapter<Book, BookAdapterSmall.SmallbookHlder> {
    private onSmallBookSelected Listener;

    public BookAdapterSmall(@NonNull FirestoreRecyclerOptions<Book> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SmallbookHlder holder, int i, @NonNull Book book) {
        holder.txtTitle.setText(book.getTitle());
        Picasso.get().load(book.getCover()).placeholder(R.drawable.books).into(holder.bookCover);
    }

    @NonNull
    @Override
    public SmallbookHlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_book_small,
                parent, false);
        return new SmallbookHlder(v);
    }

    class SmallbookHlder extends RecyclerView.ViewHolder {
        ImageView bookCover;
        TextView txtTitle;
        public SmallbookHlder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.bookCover);
            txtTitle = itemView.findViewById(R.id.txtBookTitle);

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

    public interface onSmallBookSelected {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void onSmallBookSelected (onSmallBookSelected listener) {
        this.Listener = listener;
    }

}
