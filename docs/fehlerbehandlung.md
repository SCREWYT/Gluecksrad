---
title: Fehlerbehandlung
nav_order: 4
---

# Fehlerbehandlung

Diese Seite dokumentiert die wichtigsten Maßnahmen zur Fehlervermeidung und -behandlung innerhalb der 
Glücksrad-App.

---

## ⚠️ Allgemeine Prinzipien

- Validierung von Benutzereingaben
- Abfangen von Netzwerkfehlern (z. B. Server nicht erreichbar)
- Begrenzung des Punktestands (z. B. kein negativer Wert)
- Verwendung von `try-catch`-Blöcken für kritische Vorgänge
- Benutzerfreundliche Fehlermeldungen per `Toast`

`Toast.makeText(this, "Upgrade gekauft!", Toast.LENGTH_SHORT).show();`

---

## 🔐 Login & Registrierung

- **Fehlende Eingabe:** Bei leerem Benutzernamen oder Passwort wird keine Anmeldung/Registrierung ausgeführt.

  `if (username.isEmpty() || password.isEmpty()) {
  Toast.makeText(this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
  return;
  }`

- **Benutzername bereits vergeben:** Der Code prüft, ob ein Benutzername bereits in der Datenbank vorhanden ist.

`if (existingUser != null) {
Toast.makeText(this, "Benutzername bereits vergeben", Toast.LENGTH_SHORT).show();
} else {
userDao.insertUser(user);
}`

- **Login mit falschem Passwort:** Es erfolgt ein Abgleich mit der Datenbank; bei Fehlschlag wird eine Fehlermeldung per `Toast` angezeigt.

`if (user != null && user.password.equals(password)) {
// Login erfolgreich
} else {
Toast.makeText(this, "Falsches Passwort oder Nutzer existiert nicht", Toast.LENGTH_SHORT).show();
}`

---

## 🎡 Glücksrad & Punktevergabe

- **Kein negativer Punktestand möglich:** Punktabzug auf einem negativen Feld kann den Punktestand nicht unter 0 bringen.

`if (currentPoints + adjustedValue < 0) {
currentPoints = 0;
} else {
currentPoints += adjustedValue;
}
`

- **Dreh-Logik:** Die Rotation ist gesichert gegen mehrfache gleichzeitige Klicks.

`spinButton.setEnabled(false);
// ... Drehung starten ...
// am Ende:
spinButton.setEnabled(true);
`

---

## 💰 Upgrade-Käufe

- **Nicht genug Punkte:** Upgrades können nur gekauft werden, wenn genügend Punkte vorhanden sind.

`if (currentPoints >= currentCost) {
// Upgrade kaufen
} else {
Toast.makeText(this, "Nicht genügend Punkte", Toast.LENGTH_SHORT).show();
}
`
---

## 🌐 Netzwerk (HttpURLConnection)


- **Fehlende Verbindung:** Falls keine Verbindung aufgebaut werden kann, zeigt ein `Toast` die Fehlermeldung.
```
try {
    URL url = new URL(urls[0]);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    int code = connection.getResponseCode();

    BufferedReader reader = new BufferedReader(new InputStreamReader(
        (code == 200) ? connection.getInputStream() : connection.getErrorStream()
    ));

    // lesen ...
} catch (IOException e) {
    e.printStackTrace();
    runOnUiThread(() ->
        Toast.makeText(SettingsActivity.this, "Netzwerkfehler", Toast.LENGTH_SHORT).show()
    );
}
```

---

> 🔍 Hinweis: Durch den modularen Aufbau (UI, DAO, Logik getrennt) lässt sich die Fehlerbehandlung 
> gezielt auf einzelne Bereiche eingrenzen und bei Bedarf erweitern.
