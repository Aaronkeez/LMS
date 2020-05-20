package com.thetravella.librarymanagementsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.thetravella.librarymanagementsystem.FirebaseFunctions.FirebaseFx;
import com.thetravella.librarymanagementsystem.Profiles.UserProfile;

public class CreateAccount extends AppCompatActivity implements View.OnClickListener {
    private EditText txtFirstName, txtLastName, txtEmail, txtPassword, txtPasswordConfirm;
    private Button btnCreateAccount;
    private FirebaseFx fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        fb = new FirebaseFx(this);

        txtFirstName = findViewById(R.id.input_firstname);
        txtLastName = findViewById(R.id.input_lastname);
        txtEmail = findViewById(R.id.input_email);
        txtPassword = findViewById(R.id.input_password);
        txtPasswordConfirm = findViewById(R.id.input_passwordConfirm);
        btnCreateAccount = findViewById(R.id.btn_signup);
        btnCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btnCreateAccount)) {
            createAccount();
        }
    }

    private void createAccount() {
        btnCreateAccount.setEnabled(false);
        String AccountType = "Student";
        String firstName = txtFirstName.getText().toString().trim();
        String lastName = txtLastName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String passwordConfirm = txtPasswordConfirm.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            btnCreateAccount.setEnabled(true);
            Toast.makeText(this, "All Fields Are Required", Toast.LENGTH_LONG).show();
        } else if ( !email.contains("@") ) {
            Toast.makeText(this, "Please Enter A Valid Email Address", Toast.LENGTH_SHORT).show();
            btnCreateAccount.setEnabled(true);
        } else if ( !email.contains(".") ) {
            Toast.makeText(this, "Please Enter A Valid Email Address", Toast.LENGTH_SHORT).show();
            btnCreateAccount.setEnabled(true);
        } else if (password.isEmpty() || password.length() <= 5 ) {
            Toast.makeText(this, "Password Must Be At least 6 Characters Long", Toast.LENGTH_LONG).show();
            btnCreateAccount.setEnabled(true);
        } else if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Sorry, Passwords Do not match", Toast.LENGTH_LONG).show();
            btnCreateAccount.setEnabled(true);
        } else {
            UserProfile profile = new UserProfile(firstName, lastName, AccountType, email);
            fb.createUser(email,password,"Creating Account. Please Wait...", profile);
            btnCreateAccount.setEnabled(true);
        }

    }

}
