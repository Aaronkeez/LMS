package com.thetravella.librarymanagementsystem.BookAuthors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.thetravella.librarymanagementsystem.R;

public class AuthorsAdapter extends FirestoreRecyclerAdapter<Author, AuthorsAdapter.AuthorHolder> {
    private onAuthorSelectedListener Listener;

    public AuthorsAdapter(@NonNull FirestoreRecyclerOptions<Author> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AuthorHolder holder, int i, @NonNull Author author) {
        holder.txtBio.setText(author.getBio());
        holder.txtFname.setText(author.getFirstname() + " " + author.getLastname());
        // holder.txtLName.setText(author.getLastname());
    }

    @NonNull
    @Override
    public AuthorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_author,
                parent, false);
        return new AuthorHolder(v);
    }

    class AuthorHolder extends RecyclerView.ViewHolder {
        TextView txtFname, txtLName, txtBio;
        public AuthorHolder(@NonNull View itemView) {
            super(itemView);
            txtFname = itemView.findViewById(R.id.firstName);
            txtLName = itemView.findViewById(R.id.lastName);
            txtBio = itemView.findViewById(R.id.bio);

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

    public interface onAuthorSelectedListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnAuthorClickListener(onAuthorSelectedListener listener){
        this.Listener = listener;
    }

}
