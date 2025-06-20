package com.example.gluecksrad.ui;

// Importiert Klassen, die wir für Eingabe, Datenbank und Bildschirmfunktionen brauchen
import android.content.ContentValues;                              // Zum Einfügen von Daten in die Datenbank
import android.content.Intent;                                    // Um zwischen Screens zu wechseln
import android.database.Cursor;                                  // Um Daten aus der Datenbank zu lesen
import android.database.sqlite.SQLiteDatabase;                  // Datenbankobjekt
import android.os.Bundle;                                      // Für übergebene Daten zwischen Screens
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;                                // Für kurze Popup-Nachrichten

import androidx.appcompat.app.AppCompatActivity;          // Basis für eine Bildschirm-Klasse (Activity)

import com.example.gluecksrad.R;
import com.example.gluecksrad.database.DBHelper;       // Unsere selbstgeschriebene Hilfsklasse für SQLite

public class RegisterActivity extends AppCompatActivity {

    // Eingabe-Felder und Buttons
    private EditText editTextUsername, editTextPassword;
    private EditText editTextConfirmPassword;  // NEU: Eingabefeld für Passwort-Wiederholung
    private Button buttonRegister, buttonBackToLogin;

    // Wird beim Start der Activity (dem Screen) ausgeführt
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);  // Verbindet mit dem Layout

        // Referenzen zu den Elementen im Layout holen (XML)
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);  // Verbindung zum zweiten Feld
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);

        // Zugriff auf die Datenbank vorbereiten
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Wenn der Button "Registrieren" gedrückt wird:
        buttonRegister.setOnClickListener(v -> {
            // Eingaben auslesen und zuschneiden (Leerzeichen entfernen)
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim(); // NEU


            // Falls ein Feld leer ist → Hinweis anzeigen
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                return;
            }

            // Neu: Prüfen, ob die beiden Passwörter gleich sind
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show();
                return;
            }


            // Prüfen, ob es den Benutzernamen schon gibt
            Cursor cursor = db.query(
                    "users",                                  // Tabelle
                    new String[]{"username"},                      // Welche Spalten interessieren uns?
                    "username = ?",                               // Bedingung (WHERE)
                    new String[]{username},                      // Wert für die Bedingung (Platzhalter wird ersetzt)
                    null, null, null                     // GROUP BY, HAVING, ORDER BY (nicht gebraucht)
            );

            if (cursor.moveToFirst()) {
                // Benutzername gibt's schon → Fehler anzeigen
                Toast.makeText(this, "Benutzername existiert bereits", Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }
            cursor.close();  // WICHTIG: Cursor schließen

            // Benutzer neu in die Datenbank einfügen
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);

            long result = db.insert("users", null, values);

            // Erfolg oder Misserfolg anzeigen & Toast ist der kleine aufploppende Text unten
            if (result == -1) {
                Toast.makeText(this, "Fehler bei der Registrierung", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();
                // Weiterleitung zurück zum Login-Screen
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();  // Diese Activity schließen
            }
        });

        // Wenn man "Zurück zum Login" klickt
        buttonBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
