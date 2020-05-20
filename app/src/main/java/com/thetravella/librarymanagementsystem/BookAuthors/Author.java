package com.thetravella.librarymanagementsystem.BookAuthors;

public class Author {
    private String firstname;
    private String lastname;
    private String bio;

    private Author(){}

    public Author(String firstname, String lastname, String bio) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.bio = bio;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getBio() {
        return bio;
    }
}
