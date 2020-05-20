package com.thetravella.librarymanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thetravella.librarymanagementsystem.Categories.BookCategory;
import com.thetravella.librarymanagementsystem.Categories.CategoriesAdapter;
import com.thetravella.librarymanagementsystem.FirebaseFunctions.FirebaseFx;


public class BookCategories extends AppCompatActivity implements View.OnClickListener {
    private Dialog addCatDialog;
    private AppCompatEditText txtCategoryName, txtCategoryDescription;
    private Button btnCreateCategory;
    private FirebaseFx fb;
    private RecyclerView recyclerView;

    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private CollectionReference CategoriesRef = Fdb.collection("Categories");
    private CategoriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_categories);
        fb = new FirebaseFx(this);
        recyclerView = findViewById(R.id.bookCatsRecyclerview);

        addCatDialog = new Dialog(this);
        addCatDialog.setContentView(R.layout.dialog_new_category);
        addCatDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window1 = addCatDialog.getWindow();
        WindowManager.LayoutParams wlp1 = window1.getAttributes();
        wlp1.gravity = Gravity.BOTTOM;
        window1.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window1.setAttributes(wlp1);

        txtCategoryName = addCatDialog.findViewById(R.id.txtCategoryName);
        txtCategoryDescription = addCatDialog.findViewById(R.id.txtCategoryDescription);
        btnCreateCategory = addCatDialog.findViewById(R.id.btnCreateCategory);

        btnCreateCategory.setOnClickListener(this);
        loadCategories();
    }

    private void loadCategories(){
        Query query = CategoriesRef.orderBy("category", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<BookCategory> options = new FirestoreRecyclerOptions.Builder<BookCategory>()
                .setQuery(query, BookCategory.class)
                .build();
        adapter = new CategoriesAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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

    private void createCategory() {
        String Category = txtCategoryName.getText().toString().trim();
        String Description = txtCategoryDescription.getText().toString().trim();
        if(Category.isEmpty()){
            Toast.makeText(this, "Please Enter New Category Name", Toast.LENGTH_SHORT).show();
        } else if(Description.isEmpty()){
            Toast.makeText(this, "Please Add A Description", Toast.LENGTH_SHORT).show();
        } else{
            addCatDialog.dismiss();
            BookCategory category = new BookCategory(Category, Description);
            fb.createNewBookCategory(category);
            txtCategoryName.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.categories_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_addCategory:
                addCatDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnCreateCategory)){
            createCategory();
        }
    }
}
