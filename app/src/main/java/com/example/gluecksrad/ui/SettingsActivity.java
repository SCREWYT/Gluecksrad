package com.example.gluecksrad.ui;

// Notwendige Imports
import android.content.Intent; // Um zwischen Aktivitäten zu wechseln
import android.os.AsyncTask;   // Für Hintergrundaufgaben wie Netzwerkanfragen
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gluecksrad.R; // Zugriff auf Layouts und Ressourcen

import java.io.BufferedReader;          // Zum Lesen der Serverantwort
import java.io.InputStreamReader;      // Wandelt Byte-Daten in Text um
import java.net.HttpURLConnection;    // Für HTTP-Verbindungen (GET)
import java.net.URL;                 // Repräsentiert eine Webadresse

public class SettingsActivity extends AppCompatActivity {

    // UI-Elemente (Buttons und TextView)
    private Button buttonBackToMain;
    private Button buttonLogout;
    private Button buttonTestNetwork;
    private TextView textViewNetworkResult;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // Verknüpft mit settings.xml

        // Alle UI-Elemente mit der XML verbinden
        buttonBackToMain = findViewById(R.id.buttonBackToMain);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonTestNetwork = findViewById(R.id.buttonTestNetwork);
        textViewNetworkResult = findViewById(R.id.textViewNetworkResult);

        // Benutzername aus vorheriger Activity empfangen
        username = getIntent().getStringExtra("username");
        if (username == null) username = "testuser"; // Fallback

        // Wenn "Zurück zum Glücksrad" gedrückt wird
        buttonBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("username", username); // Nutzername wieder mitgeben
            startActivity(intent); // Neue Activity starten
            finish(); // Diese hier schließen
        });

        // Wenn "Logout" gedrückt wird
        buttonLogout.setOnClickListener(v -> {
            // Mit Flags wird sichergestellt, dass die App zurückgesetzt wird
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Wenn "Netzwerkverbindung testen" gedrückt wird
        buttonTestNetwork.setOnClickListener(v -> {
            // Führt eine GET-Anfrage an den lokalen Server durch
            new NetworkTestTask().execute("http://10.0.2.2:8080/test");
        });
    }

    // NetworkTestTask = Interne Klasse, die zu URL verbindet, Text dort liest und ausgibt
    private class NetworkTestTask extends AsyncTask<String, Void, String> {

        // Diese Methode läuft im Hintergrund-Thread
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                // URL vorbereiten und Verbindung öffnen
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Antwortcode abfragen
                int code = connection.getResponseCode();

                // Richtigen Stream zum Lesen auswählen
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        (code == 200) ? connection.getInputStream() : connection.getErrorStream()
                ));

                // Antwort Zeile für Zeile lesen
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                // Antwort zusammensetzen. sb = Stringbuilder
                response = "Antwort: " + sb.toString();

            } catch (Exception e) {
                // Bei Fehler: Nachricht anzeigen & e = exception
                response = "Fehler: " + e.getMessage();
            }

            return response; // Rückgabe als onPostExecute
        }

        // Diese Methode läuft wieder im UI-Thread (nach dem Task)
        @Override
        protected void onPostExecute(String result) {
            textViewNetworkResult.setText(result); // Antwort anzeigen
            Toast.makeText(SettingsActivity.this, "Netzwerk-Test abgeschlossen", Toast.LENGTH_SHORT).show();
        }
    }
}
