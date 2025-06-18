package com.example.gluecksrad.ui; // Das ist das Package – also der „Ordner“ in dem diese Klasse liegt

// Imports: Wir holen uns hier benötigte Klassen und Funktionen
import android.content.Intent;                                // Um zwischen Screens (Activities) zu wechseln
import android.database.sqlite.SQLiteDatabase;               // Für direkten Zugriff auf die SQLite-Datenbank
import android.os.Bundle;                                   // Enthält Daten beim Erstellen einer Activity
import android.widget.Button;                              // UI-Element für Buttons
import android.widget.TextView;                           // UI-Element für Textanzeige
import android.widget.Toast;                             // Zeigt kurze Hinweisfenster (Benachrichtigungen) an

import androidx.appcompat.app.AppCompatActivity;       // Grundklasse für alle Android-Bildschirme

import com.example.gluecksrad.R;                     // Zugriff auf Ressourcen wie Layouts, IDs, Texte etc.
import com.example.gluecksrad.database.DBHelper;    // Eigene Hilfsklasse zum Verwalten der Datenbank
import com.example.gluecksrad.database.ScoreDao;   // Eigene Klasse zum Lesen/Schreiben des Punktestands

public class UpgradeActivity extends AppCompatActivity { // Activity = ein Bildschirm in Android

    // Diese Buttons und Textfelder steuern das Layout
    private Button buttonUpgradeMultiplier, buttonUpgradeFieldValue, buttonBackToMain;
    private TextView textViewUpgradeStatus;

    private String username; // Aktueller eingeloggter Benutzer
    private ScoreDao scoreDao; // Objekt zum Zugriff auf Score-Tabellen in der Datenbank

    // Namen der Upgrades (für Datenbank-Einträge)
    private static final String UPGRADE_MULTIPLIER = "multiplier";
    private static final String UPGRADE_FIELDVALUE = "field_value";

    // Basispreise der Upgrades
    private static final int BASE_COST_MULTIPLIER = 5;
    private static final int BASE_COST_FIELDVALUE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Aufruf der Elternklasse (AppCompatActivity)
        setContentView(R.layout.activity_upgrade); // Layout-Datei mit der Oberfläche laden

        // Username vom vorherigen Screen holen
        username = getIntent().getStringExtra("username");
        if (username == null) username = "testuser"; // Falls keiner mitgegeben wurde: Fallback

        // Datenbank-Initialisierung
        DBHelper dbHelper = new DBHelper(this); // Hilfsklasse für SQLite-Verbindung
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // Datenbank schreibbar öffnen
        scoreDao = new ScoreDao(db); // ScoreDao-Objekt erstellen für Upgrades und Punkte

        // Buttons und TextViews mit dem XML verknüpfen
        buttonUpgradeMultiplier = findViewById(R.id.buttonUpgradeMultiplier);
        buttonUpgradeFieldValue = findViewById(R.id.buttonUpgradeFieldValue);
        buttonBackToMain = findViewById(R.id.buttonBackToMain);
        textViewUpgradeStatus = findViewById(R.id.textViewUpgradeStatus);

        // Anzeige der Upgrade-Kosten und Level aktualisieren
        updateUpgradeButtons();

        // Multiplikator-Upgrade: Klick-Listener
        buttonUpgradeMultiplier.setOnClickListener(v -> {
            int level = scoreDao.getUpgradeLevel(username, UPGRADE_MULTIPLIER); // Aktuelles Level
            int cost = (int) Math.ceil(BASE_COST_MULTIPLIER * Math.pow(1.1, level)); // Preis steigt um 10% pro Stufe
            int currentScore = scoreDao.getScore(username); // Aktueller Punktestand

            if (currentScore >= cost) { // Nur kaufen, wenn genug Punkte da sind
                scoreDao.updateScore(username, currentScore - cost); // Punkte abziehen
                scoreDao.setUpgradeLevel(username, UPGRADE_MULTIPLIER, level + 1); // Level hochzählen
                Toast.makeText(this, "Multiplikator-Upgrade gekauft!", Toast.LENGTH_SHORT).show(); // Hinweis anzeigen
                updateUpgradeButtons(); // Anzeige aktualisieren
            } else {
                Toast.makeText(this, "Nicht genug Punkte!", Toast.LENGTH_SHORT).show(); // Fehlermeldung
            }
        });

        // Feldwerte-Upgrade: Klick-Listener
        buttonUpgradeFieldValue.setOnClickListener(v -> {
            int level = scoreDao.getUpgradeLevel(username, UPGRADE_FIELDVALUE); // Aktuelles Level
            int cost = (int) Math.ceil(BASE_COST_FIELDVALUE * Math.pow(1.1, level)); // Preisberechnung (steigend)
            int currentScore = scoreDao.getScore(username); // Aktueller Punktestand

            if (currentScore >= cost) {
                scoreDao.updateScore(username, currentScore - cost); // Punkte abziehen
                scoreDao.setUpgradeLevel(username, UPGRADE_FIELDVALUE, level + 1); // Upgrade-Level erhöhen
                Toast.makeText(this, "Feldwerte-Upgrade gekauft!", Toast.LENGTH_SHORT).show();
                updateUpgradeButtons();
            } else {
                Toast.makeText(this, "Nicht genug Punkte!", Toast.LENGTH_SHORT).show();
            }
        });

        // Zurück zum Glücksrad (MainActivity)
        buttonBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(UpgradeActivity.this, MainActivity.class); // Wechsel zur Hauptseite
            intent.putExtra("username", username); // Username mitgeben
            startActivity(intent); // Neue Activity starten
            finish(); // Diese Activity schließen
        });
    }

    // Diese Methode aktualisiert die Texte der Buttons und zeigt aktuelle Kosten + Level an
    private void updateUpgradeButtons() {
        int levelMultiplier = scoreDao.getUpgradeLevel(username, UPGRADE_MULTIPLIER);
        int levelField = scoreDao.getUpgradeLevel(username, UPGRADE_FIELDVALUE);
        int score = scoreDao.getScore(username);

        int costMultiplier = (int) Math.ceil(BASE_COST_MULTIPLIER * Math.pow(1.1, levelMultiplier));
        int costField = (int) Math.ceil(BASE_COST_FIELDVALUE * Math.pow(1.1, levelField));

        // Multiplikator-Button: Text anzeigen mit aktuellem + nächstem Wert
        buttonUpgradeMultiplier.setText("Multiplikator x" +
                String.format("%.1f", 1 + 0.1 * levelMultiplier) + // Aktueller Wert (z. B. x1.0)
                " → x" + String.format("%.1f", 1 + 0.1 * (levelMultiplier + 1)) + // Nächster Wert
                "\nKosten: " + costMultiplier);

        // Feldwerte-Button: Zeigt wie stark Felder erhöht wurden und werden
        buttonUpgradeFieldValue.setText("Feldwerte ±" + (levelField * 5) +
                " → ±" + ((levelField + 1) * 5) +
                "\nKosten: " + costField);

        // Punktestand anzeigen
        textViewUpgradeStatus.setText("Dein Punktestand: " + score);
    }
}
