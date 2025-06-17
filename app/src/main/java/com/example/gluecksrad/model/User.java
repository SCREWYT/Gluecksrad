package com.example.gluecksrad.model;

// Eine einfache Datenklasse (Model), die die Daten eines Benutzers speichert
public class User {

    // Öffentliche Variablen: Die Eigenschaften eines Users

    public int id;                // Eindeutige ID des Benutzers (z. B. 1, 2, 3 …)
    public String username;     // Benutzername (z. B. "max123")
    public String password;   // Passwort als Text (Hier im Klartext, in richtiger App nicht gut)

    // Konstruktor: Wird aufgerufen, wenn man ein neues User-Objekt erstellen will
    // Beispiel: new User(1, "max", "1234")

    public User(int id, String username, String password) {
        this.id = id;                // Das "this" sagt: Speichere den übergebenen Wert in das Feld dieser Klasse
        this.username = username;   // → username im Objekt wird gesetzt
        this.password = password;  // → passwort im Objekt wird gesetzt
    }
}
