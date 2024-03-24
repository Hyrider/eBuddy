package com.example.myapplication;

public class Expense {
    private String description;
    private String amount;
    private String billOnNonPayer;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public Expense(String description, String amount, String billOnNonPayer, String status, String id) {
        this.description = description;
        this.amount = amount;
        this.billOnNonPayer = billOnNonPayer;
        this.status = status;
        this.id=id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getbillOnNonPayer() {
        return billOnNonPayer;
    }

    public void setbillOnNonPayer(String billOnNonPayer) {
        this.billOnNonPayer = billOnNonPayer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String text) {
        this.status = status;
    }
}
