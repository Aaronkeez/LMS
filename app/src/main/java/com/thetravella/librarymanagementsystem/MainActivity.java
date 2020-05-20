package com.thetravella.librarymanagementsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.ButtonBarLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thetravella.librarymanagementsystem.FirebaseFunctions.FirebaseFx;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseFx fb;
    private EditText txtInputEmail, txtInputPassword;
    private AppCompatButton btnLogin;
    private TextView txtCreateAccount, txtForgotPassord;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtInputEmail = findViewById(R.id.input_email);
        txtInputPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);
        txtCreateAccount = findViewById(R.id.link_signup);
        txtForgotPassord = findViewById(R.id.link_forgotPassowrd);
        btnLogin.setOnClickListener(this);
        txtCreateAccount.setOnClickListener(this);
        txtForgotPassord.setOnClickListener(this);

        fb = new FirebaseFx(this);
        FirebaseUser UserAuth = mAuth.getCurrentUser();
        if( UserAuth != null ){//If user is already Logged In
            startActivity(new Intent(MainActivity.this, Home.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

    }

    private void loginUser() {
        btnLogin.setEnabled(false);
        String email = txtInputEmail.getText().toString().trim();
        String password = txtInputPassword.getText().toString().trim();
        if ( !email.contains("@") ) {
            Toast.makeText(this, "Please Enter A Valid Email Address", Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
        } else if ( !email.contains(".") ) {
            Toast.makeText(this, "Please Enter A Valid Email Address", Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
        } else if (password.isEmpty() || password.length() <= 5 ) {
            Toast.makeText(this, "Password Must Be Atleast 5 Characters Long", Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
        } else {
            fb.loginUser(email, password, "Authenticating user...");
            btnLogin.setEnabled(true);
        }
    }

    private void sendPasswordResetMail(){
        String email = txtInputEmail.getText().toString().trim();
        if(email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            Toast.makeText(this, "Please Enter a Valid Email Address", Toast.LENGTH_SHORT).show();
        } else {
            fb.resetPassword(email);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnLogin)) {
            loginUser();
        } else if (v.equals(txtCreateAccount)) {
            startActivity(new Intent(MainActivity.this, CreateAccount.class));
        } else if(v.equals(txtForgotPassord)){
            sendPasswordResetMail();
        }
    }
}
