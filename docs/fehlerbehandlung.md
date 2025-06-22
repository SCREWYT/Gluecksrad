---
title: Fehlerbehandlung
nav_order: 6
---

# Fehlerbehandlung

Die App behandelt folgende Fehlerquellen:

- Keine Internetverbindung (Netzwerkfehler)
- Ungültige Eingaben im Login
- Fehler beim Zugriff auf die Datenbank
- Fallback bei ungültigen Antworten

Beispiel:

```java
try {
   HttpURLConnection conn = ...
} catch (IOException e) {
   showErrorDialog("Verbindung fehlgeschlagen.");
}
