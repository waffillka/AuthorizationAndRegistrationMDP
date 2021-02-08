package com.example.lab2.Model;

public class User {
    public String email;
    public String numberPhone;
    public String firstname;
    public String lastname;
    public String image;

    public User(){
        this.email = "";
        this.numberPhone = "";
        this.firstname = "";
        this.lastname = "";
    }

    public User(String email, String numberPhone, String fistname, String lastname){
        this.email = email;
        this.numberPhone = numberPhone;
        this.firstname = fistname;
        this.lastname = lastname;
        this.image = "default";
    }

    public User(String email, String numberPhone, String fistname, String lastname, String image){
        this.email = email;
        this.numberPhone = numberPhone;
        this.firstname = fistname;
        this.lastname = lastname;
        this.image = image;
    }
}
