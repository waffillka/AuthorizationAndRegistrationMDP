package com.example.lab2.Model;

public class Answer {
    public String idUser;
    public String answer;
    public String expression;

    public Answer(){
        this.answer = "";
        this.expression = "";
        this.idUser = "";
    }

    public Answer(String idUser, String answer, String expression){
        this.answer = answer;
        this.expression = expression;
        this.idUser = idUser;
    }
}
