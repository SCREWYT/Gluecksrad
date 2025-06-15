package com.example.gluecksrad.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gluecksrad.model.User;

public class UserDAO {

    private DBHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Einen neuen User speichern (Registrierung)
    public boolean insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword()); // Achtung: Nur Klartext zum Testen!
        long result = db.insert("users", null, values);
        db.close();
        return result != -1; // -1 bedeutet Fehler
    }

    // User anhand Username suchen
    public User getUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users",
                new String[]{"id", "username", "password"},
                "username = ?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
            );
            cursor.close();
            db.close();
            return user;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    // Login prüfen: Username + Passwort vergleichen
    public boolean checkUserCredentials(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) return false;
        return user.getPassword().equals(password);
    }
}
