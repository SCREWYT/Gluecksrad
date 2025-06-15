package com.example.gluecksrad.model;

public class User {

    private int id;
    private String username;
    private String password;

    // Konstruktor ohne ID (für Registrierung)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Konstruktor mit ID (für Datenbank-Lesen)
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Getter und Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
