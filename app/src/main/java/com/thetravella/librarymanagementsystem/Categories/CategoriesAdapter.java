package com.thetravella.librarymanagementsystem.Categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.thetravella.librarymanagementsystem.R;

public class CategoriesAdapter extends FirestoreRecyclerAdapter<BookCategory, CategoriesAdapter.CategoryHolder> {
    private onCategoryItemclick Listener;
    public CategoriesAdapter(@NonNull FirestoreRecyclerOptions<BookCategory> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryHolder categoryHolder, int i, @NonNull BookCategory bookCategory) {
        categoryHolder.txtCategory.setText(bookCategory.getCategory());
        categoryHolder.txtDescription.setText(bookCategory.getDescription());
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,
                parent, false);
        return new CategoryHolder(v);
    }

    class CategoryHolder extends RecyclerView.ViewHolder{
        TextView txtCategory, txtDescription;
        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.Category);
            txtDescription = itemView.findViewById(R.id.Description);

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

    public interface onCategoryItemclick{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void onCategoryClickListener(onCategoryItemclick listener) {
        this.Listener = listener;
    }

}
