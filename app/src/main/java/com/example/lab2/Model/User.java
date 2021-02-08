package com.example.lab2.Model;

public class User {
    public String idUser;//shoud be public
    public String email;
    public String numberPhone;
    public String userName;
    public String firstname;
    public String lastname;

    public User(){
        this.email = "";
        this.idUser = "";
        this.numberPhone = "";
        this.userName = "";
        this.firstname = "";
        this.lastname = "";
    }

    public User(String idUser, String email, String numberPhone, String userName, String fistname, String lastname){
        this.email = email;
        this.idUser = idUser;
        this.numberPhone = numberPhone;
        this.userName = userName;
        this.firstname = fistname;
        this.lastname = lastname;
    }
}
