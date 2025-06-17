package com.example.gluecksrad.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gluecksrad.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity {

    private Button buttonBackToMain;
    private Button buttonLogout;
    private Button buttonTestNetwork;
    private TextView textViewNetworkResult;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        buttonBackToMain = findViewById(R.id.buttonBackToMain);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonTestNetwork = findViewById(R.id.buttonTestNetwork);
        textViewNetworkResult = findViewById(R.id.textViewNetworkResult);

        username = getIntent().getStringExtra("username");
        if (username == null) username = "testuser";

        // Zurück zur Hauptseite
        buttonBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });

        // Logout
        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Netzwerkverbindung testen
        buttonTestNetwork.setOnClickListener(v -> {
            new NetworkTestTask().execute("http://10.0.2.2:8080/test");
        });
    }

    private class NetworkTestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int code = connection.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        (code == 200) ? connection.getInputStream() : connection.getErrorStream()
                ));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                response = "Antwort: " + sb.toString();
            } catch (Exception e) {
                response = "Fehler: " + e.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            textViewNetworkResult.setText(result);
            Toast.makeText(SettingsActivity.this, "Netzwerk-Test abgeschlossen", Toast.LENGTH_SHORT).show();
        }
    }
}
