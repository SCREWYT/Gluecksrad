package com.example.gluecksrad.database;

/* Notwendige Pakete für SQL
Contest = Paket, um Zugriff auf Ressourcen, Datenbanken, Dateien, Aktivitäten zu bekommen
    --> Hier: Context wird dem Konstruktor von "DBHelper" übergeben, damit "SQLiteOpenHelper" weiß, wo er
    die Datenbank speichern soll.

SQLiteDatabase = Klasse, die für alle Datenbank Operationen zuständig ist.
    --> Für so Befehle wie CREATE, INSERT oder DELETE etc.

SQLiteOpenHelper = Android Hilfsklasse, die die SQLite Datenbank automatisch erstellt, öffnet und aktualisiert.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// DBHelper verwaltet halt die Datenbank der App, wo Nutzer, Scores etc. gespeichert werden.
public class DBHelper extends SQLiteOpenHelper {

    /* Name der Datenbank-Datei, die intern auf dem Gerät gespeichert wird. db = signalisiert,
    dass Datei in Datenbank gespeichert wird
    */
    public static final String DATABASE_NAME = "gluecksrad.db";

    /* Versionsnummer der Datenbank – bei Änderungen (z. B. neue Spalten) muss man die erhöhen, da sonst
    die alte Version genutzt wird. ODER man deinstalliert ganz einfach die App und initialisiert sie neu */
    public static final int DATABASE_VERSION = 2;

    // Konstruktor: Wird immer dann aufgerufen, wenn DBHelper erstellt wird
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Wird automatisch ausgeführt, wenn die Datenbank zum ersten Mal erstellt wird
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabelle für Benutzer (Benutzername + Passwort)
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +   // Automatische ID
                        "username TEXT NOT NULL UNIQUE, " +         // Benutzername MUSS eindeutig sein
                        "password TEXT NOT NULL)"                  // Passwort darf NICHT leer sein
        );
        // Tabelle für Punkte
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS score (" +
                        "username TEXT PRIMARY KEY, " +             // Benutzername ist der eindeutige Schlüssel
                        "score INTEGER)"                           // Punktestand als Zahl
        );
        // Tabelle für Upgrades (Multiplikator & passives Einkommen)
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS upgrades (" +
                        "username TEXT, " +                           // Zu welchem Benutzer gehört das Upgrade
                        "upgrade TEXT, " +                           // Name des Upgrades
                        "level INTEGER, " +                         // Level des Upgrades
                        "PRIMARY KEY(username, upgrade))"          // Kombination aus Benutzername + Upgrade ist eindeutig
        );
    }

    // Wird automatisch ausgeführt, wenn sich die Versionsnummer ändert (z. B. von 1 auf 2)
    /* Override überschreibt Methoden, die es eigentlich schon in der Elternklasse gibt UND
       er warnt einen, wenn man sich z.B. vertippt
       */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS score");
        db.execSQL("DROP TABLE IF EXISTS upgrades");
        // Neue Tabellen anlegen
        onCreate(db);
    }
}
