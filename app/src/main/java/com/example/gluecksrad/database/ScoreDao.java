package com.example.gluecksrad.database;

// Diese Klasse verwaltet alle Zugriffe auf die Punktestände und Upgrades in der SQLite-Datenbank

import android.content.ContentValues;             // Wird verwendet, um Daten in Tabellen einzufügen/aktualisieren
import android.database.Cursor;                  // Repräsentiert die Ergebnisse einer Datenbankabfrage
import android.database.sqlite.SQLiteDatabase;  // Repräsentiert die SQLite-Datenbank selbst

public class ScoreDao {

    private final SQLiteDatabase db;         // Referenz auf die Datenbank

    public ScoreDao(SQLiteDatabase db) {   // Konstruktor: übergibt die Datenbank an diese Klasse
        this.db = db;
    }

    // Holt den aktuellen Punktestand eines Nutzers aus der Datenbank
    public int getScore(String username) {
        // Führt eine SQL-Abfrage aus, um den Score eines bestimmten Nutzers zu erhalten
        Cursor cursor = db.rawQuery("SELECT score FROM score WHERE username = ?", new String[]{username});
        // Wenn ein Ergebnis gefunden wurde, holt es den Wert
        if (cursor.moveToFirst()) {
            int score = cursor.getInt(0);  // Index 0, da nur eine Spalte abgefragt wurde
            cursor.close();                          // Cursor muss geschlossen werden, um Speicher freizugeben
            return score;
        }
        // Wenn kein Ergebnis gefunden wurde: Rückgabe = 0
        cursor.close();
        return 0;
    }

    // Aktualisiert/erstellt Punktestand des eingeloggten Nutzers
    public void updateScore(String username, int score) {
        // ContentValues speichert Spaltenname-Wert-Paare wie ein kleines Dictionary
        ContentValues values = new ContentValues();
        values.put("username", username);              // Spalte „username“
        values.put("score", score);                   // Spalte „score“

        // Fügt den neuen Score ein oder ersetzt vorhandenen, falls der Nutzer schon existiert
        db.insertWithOnConflict("score", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Gibt das Level eines bestimmten Upgrades eines bestimmten Nutzers zurück
    public int getUpgradeLevel(String username, String upgrade) {
        Cursor cursor = db.rawQuery("SELECT level FROM upgrades WHERE username = ? AND upgrade = ?", new String[]{username, upgrade});
        if (cursor.moveToFirst()) {
            int level = cursor.getInt(0);
            cursor.close();
            return level;
        }
        cursor.close();
        return 0;                                    // Wenn kein Upgrade-Level existiert, ist der Standardwert 0
    }

    // Upgrade-Level setzen (ersetzen)
    public void setUpgradeLevel(String username, String upgrade, int level) {
        ContentValues values = new ContentValues();
        values.put("username", username);              // Spalte „username“
        values.put("upgrade", upgrade);               // Spalte „username“
        values.put("level", level);                  // Spalte „level“

        // Fügt Datensatz ein oder ersetzt, wenn derselbe Primärschlüssel (username + upgrade) schon existiert
        db.insertWithOnConflict("upgrades", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
