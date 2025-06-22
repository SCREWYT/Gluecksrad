---
title: Codeauszüge
nav_order: 5
---

# 📌 Codeauszüge mit Erklärung

In diesem Kapitel werden einige zentrale Codeausschnitte der Glücksrad-App vorgestellt und erklärt. 
Der Fokus liegt auf typischen Programmierlogiken wie Login-Prüfung, Rad-Drehmechanik und der 
Netzwerkkommunikation.

# Beispiel 1: Login-Prüfung:

Die folgende Login-Logik stammt aus der `LoginActivity.java`. Sie überprüft, ob Benutzername 
und Passwort korrekt eingegeben wurden und leitet den Nutzer entsprechend weiter oder gibt eine 
Fehlermeldung aus:

```
// Wenn der Nutzer auf den "Login"-Button klickt
buttonLogin.setOnClickListener(v -> {
    // Eingaben auslesen und Leerzeichen am Anfang/Ende entfernen
    String username = editTextUsername.getText().toString().trim();
    String password = editTextPassword.getText().toString().trim();

    // Prüfung: Wurde etwas eingegeben?
    if (username.isEmpty() || password.isEmpty()) {
        Toast.makeText(this, "Bitte Benutzername und Passwort eingeben", Toast.LENGTH_SHORT).show();
        return; // Abbruch – ohne Eingabe kein Login
    }

    // Datenbank: Kombination Benutzername + Passwort prüfen
    boolean loginSuccess = userDao.login(username, password);

    if (loginSuccess) {
        // Login war erfolgreich – zur Hauptansicht wechseln
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username); // Username an MainActivity übergeben
        startActivity(intent); // Wechsle zum Spiel
        finish(); // Login-Activity schließen, damit man nicht zurück kann
    } else {
        // Login fehlgeschlagen – Hinweis anzeigen
        Toast.makeText(this, "Login fehlgeschlagen – bitte prüfen", Toast.LENGTH_SHORT).show();
    }
});

```

---

# Beispiel 2: Drehlogik des Rads:

Die Drehung des Glücksrads basiert auf einem `ObjectAnimator`, der die Rotation des Rads animiert. Die Bewegung besteht aus vier vollständigen Umdrehungen plus dem Zusatzwinkel, der nötig ist, um ein zufällig ausgewähltes Feld unter dem Zeiger zu zentrieren. Dabei wird die aktuelle Position des Rads berücksichtigt, um flüssige Übergänge zwischen mehreren Drehungen zu ermöglichen.

```
// Anzahl der Felder auf dem Rad (z. B. 8)
int sectorCount = spinValues.length;

// Zufälliger Index eines Feldes (0 bis 7)
int randomSector = random.nextInt(sectorCount);

// Jedes Feld ist gleich breit (360° / Anzahl Felder)
float degreesPerSector = 360f / sectorCount;

// Das Rad soll 4 volle Umdrehungen machen (4 * 360° = 1440°)
float rotations = 4f;

// Zielrotation berechnen:
// - Start bei currentRotation (aktuelle Position)
// - 4 volle Umdrehungen
// - zusätzlicher Winkel, um das Ziel-Feld oben zu platzieren
// - zentrieren auf Mitte des Feldes mit + degreesPerSector / 2
float targetRotation = currentRotation
        + rotations * 360f
        + randomSector * degreesPerSector
        + degreesPerSector / 2f;

// Erstelle die Dreh-Animation
ObjectAnimator animator = ObjectAnimator.ofFloat(
        wheelContainer,           // zu drehendes Layout (das Rad)
        "rotation",               // animierte Eigenschaft
        currentRotation,          // Startwinkel
        targetRotation            // Zielwinkel
);

// Die Animation dauert 3 Sekunden
animator.setDuration(3000);

// Die Drehung wird am Ende langsamer (realistischer Effekt)
animator.setInterpolator(new DecelerateInterpolator());

// Startet die Animation
animator.start();
```

---

# Beispiel 3: Netzwerk-Check:

```
// Beim Klick auf den Button wird eine Test-Anfrage gestartet
buttonTestNetwork.setOnClickListener(v -> {
    new NetworkTestTask().execute("http://10.0.2.2:8080/test");
});

// Interne Hilfsklasse, die AsyncTask verwendet, um eine GET-Anfrage auszuführen
private class NetworkTestTask extends AsyncTask<String, Void, String> {

    // Diese Methode wird im Hintergrund ausgeführt (nicht im UI-Thread)
    @Override
    protected String doInBackground(String... urls) {
        String response = "";
        try {
            // Erstellt eine URL aus dem übergebenen String
            URL url = new URL(urls[0]);

            // Öffnet eine Verbindung und legt die Methode fest (GET)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Liest den Antwortcode (z. B. 200 = OK, 404 = nicht gefunden)
            int code = connection.getResponseCode();

            // Wählt je nach Code den passenden Stream: Input (bei Erfolg) oder Error (bei Fehler)
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    (code == 200) ? connection.getInputStream() : connection.getErrorStream()
            ));

            // Liest die Serverantwort zeilenweise ein und fügt sie zusammen
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // Rückgabe vorbereiten
            response = "Antwort: " + sb.toString();

        } catch (Exception e) {
            // Bei Fehlern (z. B. keine Verbindung): Nachricht erzeugen
            response = "Fehler: " + e.getMessage();
        }

        return response; // Wird an onPostExecute übergeben
    }

    // Diese Methode wird nach Abschluss im UI-Thread ausgeführt
    @Override
    protected void onPostExecute(String result) {
        // Zeigt die Serverantwort im TextView an
        textViewNetworkResult.setText(result);

        // Informiert den Nutzer, dass der Test abgeschlossen ist
        Toast.makeText(SettingsActivity.this, "Netzwerk-Test abgeschlossen", Toast.LENGTH_SHORT).show();
    }
}
```