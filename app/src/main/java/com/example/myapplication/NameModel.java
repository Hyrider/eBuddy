package com.example.myapplication;

public class NameModel {
    String name;
    Float total_sum;
    String idInChat;
    String textToShow;

    public NameModel(String name, Float total_sum, String idInChat, String textToShow) {
        this.name = name;
        this.total_sum = total_sum;
        this.idInChat = idInChat;
        this.textToShow = textToShow;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getTotal_sum() {
        return total_sum;
    }

    public void setTotal_sum(Float total_sum) {
        this.total_sum = total_sum;
    }

    public String getIdInChat() {
        return idInChat;
    }

    public void setIdInChat(String idInChat) {
        this.idInChat = idInChat;
    }

    public String gettextToShow() {
        return textToShow;
    }

    public void settextToShow(String textToShow) {
        this.textToShow = textToShow;
    }
}
