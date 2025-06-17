package com.example.gluecksrad.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
// DAO = Data Access Object → Diese Klasse kümmert sich darum, mit der users-Tabelle der Datenbank zu arbeiten.
public class UserDao {

    // Referenz auf die Datenbank, um darin arbeiten zu können
    private SQLiteDatabase db;

    // Konstruktor – bekommt die Datenbank übergeben und speichert sie in der Variable db
    public UserDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Benutzer registrieren: Speichert neuen Nutzer mit Name/Passwort in der Tabelle "users"
    public boolean register(String username, String password) {
        ContentValues values = new ContentValues();                   // Container für Spalten und Werte
        values.put("username", username);                            // Spalte "username" bekommt den Namen
        values.put("password", password);                           // Spalte "password" bekommt das Passwort

        long result = db.insert("users", null, values);   // Füge neuen Eintrag in die Tabelle ein
        return result != -1;         // Wenn das Einfügen geklappt hat, ist result nicht -1
    }

    // Login prüfen: Gibt true zurück, wenn es einen Eintrag mit Name und Passwort gibt
    public boolean login(String username, String password) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username = ? AND password = ?",
                new String[]{username, password}
        );

        // moveToFirst() gibt true zurück, wenn ein Ergebnis gefunden wurde
        boolean success = cursor.moveToFirst();

        cursor.close();      // Cursor schließen, wenn man ihn nicht mehr braucht
        return success;     // true = Login erfolgreich, false = fehlgeschlagen
    }
}
