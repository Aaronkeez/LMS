package com.thetravella.librarymanagementsystem.Profiles;

public class UserProfile {
    private String Firstname;
    private String Lastname;
    private String AccountType;
    private String Email;

    public UserProfile(String firstname, String lastname, String accountType, String email) {
        Firstname = firstname;
        Lastname = lastname;
        AccountType = accountType;
        Email = email;
    }

    public String getFirstname() {
        return Firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public String getAccountType() {
        return AccountType;
    }

    public String getEmail() {
        return Email;
    }
}
