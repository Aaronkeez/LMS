package com.thetravella.librarymanagementsystem.Books;

public class Book {
    private String category;
    private String author;
    private String title;
    private String description;
    private String cover;

    public Book(){}

    public Book(String category, String author, String title, String description, String cover) {
        this.category = category;
        this.author = author;
        this.title = title;
        this.description = description;
        this.cover = cover;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCover() {
        return cover;
    }
}
