package com.thetravella.librarymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thetravella.librarymanagementsystem.Books.Book;
import com.thetravella.librarymanagementsystem.Books.BooksAdapter;
import com.thetravella.librarymanagementsystem.FirebaseFunctions.FirebaseFx;
import com.thetravella.librarymanagementsystem.Profiles.UserProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseFx fb;
    private TextView txtFullname, txtEmail;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private String userEmailId, userId, FULL_NAME, USER_ROLE;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private RecyclerView recyclerView;
    private BooksAdapter booksAdapter;
    private FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private CollectionReference BooksDbRef = Fdb.collection("Books");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawer = findViewById(R.id.drawer_layout);
        fb = new FirebaseFx(this);
        recyclerView = findViewById(R.id.home_recyclerview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().clear();

        View headerView = navigationView.getHeaderView(0);
        txtFullname = headerView.findViewById(R.id.user_name);
        txtEmail = headerView.findViewById(R.id.user_email);

        //Load Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        checkAuth();
        loadBooks();
    }

    private void loadBooks() {
        Query query = BooksDbRef.orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Book> books = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();
        booksAdapter = new BooksAdapter(books);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(booksAdapter);

        booksAdapter.onBookSelectListener(new BooksAdapter.onBookSelected() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                if (documentSnapshot.exists()) {
                    Book book = documentSnapshot.toObject(Book.class);
                    startActivity(new Intent(Home.this, BookReview.class)
                            .putExtra("title", book.getTitle())
                            .putExtra("category", book.getCategory())
                            .putExtra("author", book.getAuthor())
                            .putExtra("image", book.getCover())
                            .putExtra("about", book.getDescription())
                            .putExtra("id", documentSnapshot.getId()));
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        booksAdapter.startListening();
    }

    private void checkAuth() {
        // Check Auth
        FirebaseUser UserAuth = mAuth.getCurrentUser();
        if( UserAuth != null ){//If user is already Logged In
            userEmailId = UserAuth.getEmail();
            userId = UserAuth.getUid();
            loadProfile();
        } else {
            startActivity(new Intent(Home.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        booksAdapter.stopListening();
    }

    private void loadProfile(){
        // Get Fb Profile
        Fdb.collection("Profiles").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            FULL_NAME = documentSnapshot.get("firstname").toString() + " " + documentSnapshot.get("lastname").toString();
                            USER_ROLE = documentSnapshot.get("accountType").toString();

                            txtFullname.setText(FULL_NAME);
                            txtEmail.setText(userEmailId);
                            navigationView.getMenu().clear();

                            // Set Menu Depending On Profile
                            if(USER_ROLE.equals("Admin")) {
                                navigationView.inflateMenu(R.menu.activity_home_drawer);
                            } else {
                                navigationView.inflateMenu(R.menu.student_lecturer);
                            }
                        } else {
                            Toast.makeText(Home.this, "Your Are Not A Registered User.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id){
            case R.id.nav_logout:
                fb.Logout();
                break;
            case R.id.nav_categories:
                startActivity(new Intent(Home.this, BookCategories.class));
                break;
            case R.id.nav_authors:
                startActivity(new Intent(Home.this, Authors.class));
                break;
            case R.id.nav_bookRequests:
                startActivity(new Intent(Home.this, BorrowRequests.class));
                break;
            case R.id.user_requests:
                startActivity(new Intent(Home.this, UserBookRequests.class)
                .putExtra("Request", 1));
                break;
            case R.id.nav_overdue:
                startActivity(new Intent(Home.this, UserBookRequests.class)
                        .putExtra("Request", 9));
                break;
            case R.id.nav_receiveReturn:
                startActivity(new Intent(Home.this, BookReturns.class)
                        .putExtra("Request", 9));
                break;
            case R.id.nav_allOverdues:
                startActivity(new Intent(Home.this, UserBookRequests.class)
                        .putExtra("Request", 4));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
