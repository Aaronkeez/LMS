package com.thetravella.librarymanagementsystem.Categories;

public class BookCategory {
    private String category;
    private String description;

    public BookCategory(){}

    public BookCategory(String category, String description) {
        this.category = category;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }
}
