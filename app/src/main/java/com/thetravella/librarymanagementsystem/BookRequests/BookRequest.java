package com.thetravella.librarymanagementsystem.BookRequests;


public class BookRequest {
    private String user;
    private String book;
    private int return_status;
    private int confirm_status;
    private String confirmed_by;
    private String request_date;
    private String confirm_date;
    private String date_issued;
    private String return_date;


    public BookRequest(){}

    public BookRequest(String user, String book, int return_status, int confirm_status, String confirmed_by, String request_date, String confirm_date, String date_issued, String return_date) {
        this.user = user;
        this.book = book;
        this.return_status = return_status;
        this.confirm_status = confirm_status;
        this.confirmed_by = confirmed_by;
        this.request_date = request_date;
        this.confirm_date = confirm_date;
        this.date_issued = date_issued;
        this.return_date = return_date;
    }

    public String getUser() {
        return user;
    }

    public String getBook() {
        return book;
    }

    public int getReturn_status() {
        return return_status;
    }

    public int getConfirm_status() {
        return confirm_status;
    }

    public String getConfirmed_by() {
        return confirmed_by;
    }

    public String getRequest_date() {
        return request_date;
    }

    public String getConfirm_date() {
        return confirm_date;
    }

    public String getDate_issued() {
        return date_issued;
    }

    public String getReturn_date() {
        return return_date;
    }
}
