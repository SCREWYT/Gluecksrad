package com.example.gluecksrad.ui;

import android.content.Intent;                                // Um zur nächsten Activity zu wechseln (z. B. MainActivity)
import android.database.sqlite.SQLiteDatabase;               // Für den Zugriff auf die SQLite-Datenbank
import android.os.Bundle;                                   // Für den Lebenszyklus von Android-Activities
import android.widget.Button;                              // Für die Login- und Registrier-Buttons
import android.widget.EditText;                           // Für die Eingabefelder für Benutzername und Passwort
import android.widget.Toast;                             // Um kurze Meldungen (z. B. "Login fehlgeschlagen") anzuzeigen

import androidx.appcompat.app.AppCompatActivity;       // Basis-Klasse für moderne Android-Activities

import com.example.gluecksrad.R;                     // Zugriff auf Ressourcen (z. B. layout, strings, ids)
import com.example.gluecksrad.database.DBHelper;    // Unsere selbst geschriebene Klasse zum Erstellen der Datenbank
import com.example.gluecksrad.database.UserDao;    // DAO für Benutzer-Login und Registrierung

public class LoginActivity extends AppCompatActivity {

    // Die Eingabefelder und Buttons, die im Layout (XML Datei) sind
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonToRegister;
    private UserDao userDao; // Zugriff auf Benutzer-Datenbankfunktionen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Verbindet Java-Code mit dem XML-Layout (activity_login.xml)

        // XML-Elemente mit Java-Code verknüpfen (findet sie anhand ihrer IDs)
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonToRegister = findViewById(R.id.buttonToRegister);

        // In R.java sind alle Ressourcen zusammengefasst

        // Datenbank vorbereiten: DBHelper liefert eine SQLite-Datenbankinstanz
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        userDao = new UserDao(db); // Benutzer-Datenbankzugriff initialisieren

        // Was passiert, wenn man auf "Login" klickt
        buttonLogin.setOnClickListener(v -> {
            // Text aus den Eingabefeldern holen und Leerzeichen entfernen
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Wenn eins der Felder leer ist, kommt eine Warnung
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Bitte Benutzername und Passwort eingeben", Toast.LENGTH_SHORT).show();
                return;
            }

            // Login prüfen (gibt true zurück, wenn Kombination korrekt ist)
            boolean loginSuccess = userDao.login(username, password);

            if (loginSuccess) {
                // Wenn Login erfolgreich ist: zur Hauptansicht (Glücksrad) wechseln
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", username); // Den Benutzernamen zur nächsten Activity mitgeben
                startActivity(intent); // Activity starten
                finish(); // LoginActivity beenden, damit man nicht per Zurück-Button wieder hier landet
            } else {
                // Wenn Login fehlschlägt: kurze Fehlermeldung anzeigen
                Toast.makeText(this, "Login fehlgeschlagen – bitte prüfen", Toast.LENGTH_SHORT).show();
            }
        });

        // Wenn man auf "Registrieren" klickt: zur RegisterActivity wechseln
        buttonToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // LoginActivity beenden
        });
    }
}