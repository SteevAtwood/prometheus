package com.example.application.Model;

import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String INN;
    private Timestamp date;
    private Integer userId;
    private String fileName;

    public History() {

    }

    public History(String INN, Timestamp date, Integer userId, String fileName) {
        this.INN = INN;
        this.date = date;
        this.userId = userId;
        this.fileName = fileName;
    }

    public String getINN() {
        return INN;
    }

    public void setINN(String INN) {
        this.INN = INN;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
